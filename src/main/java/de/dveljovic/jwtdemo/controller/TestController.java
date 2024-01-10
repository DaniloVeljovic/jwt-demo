package de.dveljovic.jwtdemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/home")
    public ResponseEntity<String> protectedRoute() {
        return ResponseEntity.ok("Hello World");
    }
}
