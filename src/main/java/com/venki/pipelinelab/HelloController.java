package com.venki.pipelinelab;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Pipeline Lab is running! Build: Maven -> GitHub -> Jenkins -> Docker -> Kubernetes";
    }

    @GetMapping("/api/version")
    public String version() {
        return "{\"app\":\"pipeline-lab\",\"version\":\"1.0.0\",\"deployedAt\":\"" + Instant.now() + "\"}";
    }

}
