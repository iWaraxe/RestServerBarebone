package com.coherentsolutions.restful.controller;
// File: TestController.java

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public String testGet() {
        return "GET request successful";
    }

    @PostMapping("/test")
    public String testPost() {
        return "POST request successful";
    }

    @PutMapping("/test")
    public String testPut() {
        return "PUT request successful";
    }

    @PatchMapping("/test")
    public String testPatch() {
        return "PATCH request successful";
    }

    @DeleteMapping("/test")
    public String testDelete() {
        return "DELETE request successful";
    }
}