package com.auth.authexam.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping("/api/public")
    public String publicApi() {
        return "Public API Access";
    }

    @GetMapping("/api/secure")
    public String secureApi() {
        return "Secure API Access";
    }
}
