package com.coherentsolutions.restful.service;

import com.coherentsolutions.restful.model.ZipCode;
import com.coherentsolutions.restful.repository.ZipCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZipCodeService {

    private static final Logger logger = LoggerFactory.getLogger(ZipCodeService.class);

    @Autowired
    private ZipCodeRepository zipCodeRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAllZipCodes() {
        logger.info("Deleting all existing zip codes");
        zipCodeRepository.deleteAll();
        logger.info("All existing zip codes deleted");
    }

    @Transactional
    public void resetZipCodes(List<String> newZipCodes) {
        logger.info("Resetting zip codes. New zip codes: {}", newZipCodes);

        logger.info("Deleting all existing zip codes");
        zipCodeRepository.deleteAll();
        zipCodeRepository.flush(); // Ensure deletions are flushed to the database
        logger.info("All existing zip codes deleted");

        logger.info("Preparing new zip codes for insertion");
        List<ZipCode> zipCodesToSave = newZipCodes.stream()
                .distinct() // Remove duplicates
                .map(code -> new ZipCode(null, code))
                .collect(Collectors.toList());
        logger.info("Prepared {} unique zip codes for insertion", zipCodesToSave.size());

        try {
            logger.info("Saving new zip codes");
            List<ZipCode> savedZipCodes = zipCodeRepository.saveAll(zipCodesToSave);
            logger.info("Successfully saved {} zip codes", savedZipCodes.size());
        } catch (Exception e) {
            logger.error("Error saving zip codes", e);
            throw e;
        }
    }


    public List<ZipCode> getAllZipCodes() {
        logger.info("Retrieving all zip codes");
        List<ZipCode> zipCodes = zipCodeRepository.findAll();
        logger.info("Retrieved {} zip codes", zipCodes.size());
        return zipCodes;
    }

    @Transactional
    public void addZipCodes(List<String> newZipCodes) {
        logger.info("Adding new zip codes: {}", newZipCodes);

        List<String> existingCodes = zipCodeRepository.findAll().stream()
                .map(ZipCode::getCode)
                .collect(Collectors.toList());
        logger.info("Existing zip codes: {}", existingCodes);

        List<ZipCode> zipCodesToSave = newZipCodes.stream()
                .distinct()
                .filter(code -> !existingCodes.contains(code))
                .map(code -> new ZipCode(null, code))
                .collect(Collectors.toList());
        logger.info("Prepared {} new unique zip codes for insertion", zipCodesToSave.size());

        try {
            List<ZipCode> savedZipCodes = zipCodeRepository.saveAll(zipCodesToSave);
            logger.info("Successfully added {} new zip codes", savedZipCodes.size());
        } catch (Exception e) {
            logger.error("Error adding zip codes", e);
            throw e;
        }
    }
}