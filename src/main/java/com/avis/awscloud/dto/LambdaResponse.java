package com.avis.awscloud.dto;

public class LambdaResponse {
    private final int result;
    public LambdaResponse(int result) { this.result = result; }
    public int getResult() { return result; }
}
