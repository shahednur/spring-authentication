package com.auth.authjwt.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth.authjwt.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String role) {
        String token = JwtUtil.generateToken(username, role);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
