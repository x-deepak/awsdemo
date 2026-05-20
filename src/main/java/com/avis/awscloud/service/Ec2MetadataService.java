package com.avis.awscloud.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Service
public class Ec2MetadataService {

    private static final String IMDS_BASE = "http://169.254.169.254/latest";
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    public Map<String, String> getInstanceInfo() {
        try {
            String token = fetchToken();
            String instanceId = fetchMetadata(token, "/meta-data/instance-id");
            String region = fetchMetadata(token, "/meta-data/placement/region");
            return Map.of("instanceId", instanceId, "region", region);
        } catch (Exception e) {
            return Map.of("instanceId", "Not running on EC2 (local development)", "region", "N/A");
        }
    }

    private String fetchToken() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(IMDS_BASE + "/api/token"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("X-aws-ec2-metadata-token-ttl-seconds", "21600")
                .timeout(Duration.ofSeconds(2))
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    private String fetchMetadata(String token, String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(IMDS_BASE + path))
                .GET()
                .header("X-aws-ec2-metadata-token", token)
                .timeout(Duration.ofSeconds(2))
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }
}
