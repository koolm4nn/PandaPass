package com.pandaPass.services;

import com.pandaPass.models.PwnedCheckResult;
import com.pandaPass.network.PwnedPasswordApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PwnedPasswordBatcher {
    private final ExecutorService executor;

    private final PwnedPasswordApiClient client;

    public record PasswordHashEntry(String passwordIdentifier, String suffix){};

    public PwnedPasswordBatcher(int maxThreads){
        this.executor = Executors.newFixedThreadPool(maxThreads);
        client = new PwnedPasswordApiClient();
    }

    /**
     * Async pwned lookups grouped by prefix.
     * @param prefixToSuffixMap Map of SHA-1 prefix to list of (password, suffix) pairs
     * @return A CompletableFuture with a list of PwnedCheckResults
     */
    public CompletableFuture<List<PwnedCheckResult>> batchCheck(Map<String, List<PasswordHashEntry>> prefixToSuffixMap){
        List<CompletableFuture<List<PwnedCheckResult>>> futures = new ArrayList<>();

        for(Map.Entry<String, List<PasswordHashEntry>> entry : prefixToSuffixMap.entrySet()){
            String prefix = entry.getKey();
            List<PasswordHashEntry> suffixEntries = entry.getValue();

            // One future per prefix group
            CompletableFuture<List<PwnedCheckResult>> future = CompletableFuture.supplyAsync(() -> checkPrefixGroup(prefix, suffixEntries), executor);

            futures.add(future);
        }

        // Combine all futures into one
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .flatMap(f -> f.join().stream())
                        .collect(Collectors.toList()));

    }

    /**
     * Calls HIBP api for prefix
     * @param prefix The prefix to query for
     * @param suffixEntries Entries with that prefix
     * @return Response for each
     */
    private List<PwnedCheckResult> checkPrefixGroup(String prefix, List<PasswordHashEntry> suffixEntries) {
        try{
            String response = client.querySuffixesForPrefix(prefix);
            Map<String, Integer> suffixToCountMap = parseResponse(response);

            List<PwnedCheckResult> result = new ArrayList<>();

            for(PasswordHashEntry entry : suffixEntries){
                Integer count = suffixToCountMap.get(entry.suffix());
                boolean isPwned = count != null;
                result.add(new PwnedCheckResult(entry.passwordIdentifier(), isPwned, count != null? count : 0));
            }

            return result;

        } catch (Exception e) {
            System.err.println("Error querying prefix " + prefix + ": " + e.getMessage());
            return suffixEntries.stream()
                    .map(entry -> new PwnedCheckResult(entry.passwordIdentifier(), false, 0))
                    .collect(Collectors.toList());
        }
    }

    private Map<String, Integer> parseResponse(String response){
        Map<String, Integer> result = new HashMap<>();
        String[] lines = response.split("\n");

        for(String line : lines){
            String[] parts = line.split(":");
            if(parts.length == 2){
                result.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }
        }

        return result;
    }

    public void shutdown(){
        executor.shutdown();
    }

}
