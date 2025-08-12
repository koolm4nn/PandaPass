package com.pandaPass.services;

import com.pandaPass.models.PwnedCheckResult;
import com.pandaPass.utils.HashUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Accept a list of passwords/emails?
 *
 * Hash and split them
 *
 * Group by prefix
 *
 * Kick off async batches
 */
public class PwnedPasswordService {
    private final PwnedPasswordBatcher batcher;

    public record PrefixSuffix(String prefix, String suffix){};

    public PwnedPasswordService(){
        this.batcher = new PwnedPasswordBatcher(5);
    }

    public CompletableFuture<List<PwnedCheckResult>> checkPasswordsAsync(Map<String, String> identifierToPasswords){
        Map<String, List<PwnedPasswordBatcher.PasswordHashEntry>> prefixToSuffixMap = new HashMap<>();

        identifierToPasswords.forEach((key, value) -> {
            String sha1 = HashUtil.hashSha1(value);
            PrefixSuffix splitted = splitHash(sha1);
            String prefix = splitted.prefix;
            String suffix = splitted.suffix;

            if (!prefixToSuffixMap.containsKey(prefix)) {
                prefixToSuffixMap.put(prefix, new ArrayList<>());
            }

            prefixToSuffixMap.get(prefix).add(new PwnedPasswordBatcher.PasswordHashEntry(key, suffix));
        });

        return batcher.batchCheck(prefixToSuffixMap);
    }

    private PrefixSuffix splitHash(String hash){
        return new PrefixSuffix(hash.substring(0, 5), hash.substring(5));
    }

    public CompletableFuture<PwnedCheckResult> checkSinglePasswordAsync(String identifier, String password){
        var map = new HashMap<String, String>();
        map.put(identifier, password);
        return checkPasswordsAsync(map).thenApply(List::getFirst);
    }
}
