package com.avis.awscloud.dto;

public class S3ObjectDto {
    private final String key;
    private final String url;
    public S3ObjectDto(String key, String url) { this.key = key; this.url = url; }
    public String getKey() { return key; }
    public String getUrl() { return url; }
}
