package com.ai.persona.gateway_server.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
