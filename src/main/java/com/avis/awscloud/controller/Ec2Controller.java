package com.avis.awscloud.controller;

import com.avis.awscloud.service.Ec2MetadataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class Ec2Controller {

    private final Ec2MetadataService ec2MetadataService;

    public Ec2Controller(Ec2MetadataService ec2MetadataService) {
        this.ec2MetadataService = ec2MetadataService;
    }

    @GetMapping("/instance-info")
    public Map<String, String> instanceInfo() {
        return ec2MetadataService.getInstanceInfo();
    }
}
