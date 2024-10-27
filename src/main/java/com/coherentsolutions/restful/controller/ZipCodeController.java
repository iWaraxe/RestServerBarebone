package com.coherentsolutions.restful.controller;

import com.coherentsolutions.restful.model.ZipCode;
import com.coherentsolutions.restful.service.ZipCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zip-codes")
public class ZipCodeController {

    private static final Logger logger = LoggerFactory.getLogger(ZipCodeController.class);

    @Autowired
    private ZipCodeService zipCodeService;

    @GetMapping
    public ResponseEntity<List<ZipCode>> getAvailableZipCodes() {
        logger.info("Received request to get available zip codes");
        List<ZipCode> zipCodes = zipCodeService.getAvailableZipCodes();
        logger.info("Returning {} zip codes", zipCodes.size());
        return ResponseEntity.ok(zipCodes);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetZipCodes(@RequestBody List<String> zipCodes) {
        logger.info("Received request to reset zip codes: {}", zipCodes);
        try {
            zipCodeService.resetZipCodes(zipCodes);
            logger.info("Successfully reset zip codes");
            return ResponseEntity.ok("Zip codes reset successfully");
        } catch (Exception e) {
            logger.error("Error resetting zip codes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resetting zip codes: " + e.getMessage());
        }
    }

    @PostMapping("/expand")
    public ResponseEntity<String> addZipCodes(@RequestBody List<String> zipCodes) {
        logger.info("Received request to add zip codes: {}", zipCodes);
        try {
            zipCodeService.addZipCodes(zipCodes);
            logger.info("Successfully added zip codes");
            return ResponseEntity.status(HttpStatus.CREATED).body("Zip codes added successfully");
        } catch (Exception e) {
            logger.error("Error adding zip codes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding zip codes: " + e.getMessage());
        }
    }
}