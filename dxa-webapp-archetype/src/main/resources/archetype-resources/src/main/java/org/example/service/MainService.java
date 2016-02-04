package org.example.service;

import org.springframework.stereotype.Service;

@Service
public class MainService {
    public String hello() {
        return "Hello, $artifactId!";
    }
}
