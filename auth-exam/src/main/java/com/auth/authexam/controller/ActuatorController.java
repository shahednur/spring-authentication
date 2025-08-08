package com.auth.authexam.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActuatorController {

    @GetMapping("/actuator/health")
    public String health() {
        return "UP";
    }

    @GetMapping("/actuator/info")
    public String info() {
        return "Application info data";
    }

    @GetMapping("/actuator/admin")
    public String adminActuator() {
        return "Sensitive Actuator Data";
    }
}
