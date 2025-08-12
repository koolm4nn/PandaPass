package com.pandaPass.network;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Send http GET request to HIBP Api
 *
 *
 */
public class PwnedPasswordApiClient {
    private final String BASE_URL = "https://api.pwnedpasswords.com/range/";

    private final HttpClient httpClient;

    public PwnedPasswordApiClient(){
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String querySuffixesForPrefix(String prefix) throws IOException, InterruptedException {
        String url = BASE_URL + prefix;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("User-Agent", "PandaPass/1.0") // TODO: value correct?
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200){
            throw new IOException("Unexpected Response from HIBP API: " + response.statusCode());
        }

        return response.body();
    }
}
