package com.avis.awscloud.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LambdaInvokeService {

    private static final Pattern RESULT_PATTERN = Pattern.compile("\"result\"\\s*:\\s*(-?\\d+)");

    private final LambdaClient lambdaClient;
    private final String functionName;

    public LambdaInvokeService(LambdaClient lambdaClient,
                                @Value("${aws.lambda.function-name}") String functionName) {
        this.lambdaClient = lambdaClient;
        this.functionName = functionName;
    }

    public int doubleNumber(int number) throws Exception {
        InvokeResponse response = lambdaClient.invoke(
                InvokeRequest.builder()
                        .functionName(functionName)
                        .payload(SdkBytes.fromUtf8String("{\"number\": " + number + "}"))
                        .build()
        );
        String json = response.payload().asUtf8String();
        Matcher m = RESULT_PATTERN.matcher(json);
        if (!m.find()) throw new RuntimeException("Unexpected Lambda response: " + json);
        return Integer.parseInt(m.group(1));
    }
}
