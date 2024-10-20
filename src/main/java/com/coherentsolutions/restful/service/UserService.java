package com.coherentsolutions.restful.service;

import com.coherentsolutions.restful.exception.BadRequestException;
import com.coherentsolutions.restful.exception.FailedDependencyException;
import com.coherentsolutions.restful.model.User;
import com.coherentsolutions.restful.model.ZipCode;
import com.coherentsolutions.restful.repository.UserRepository;
import com.coherentsolutions.restful.repository.ZipCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ZipCodeRepository zipCodeRepository;

    @Transactional
    public User createUser(User user) {
        // Validate required fields
        if (user.getName() == null || user.getName().isEmpty() ||
                user.getSex() == null || user.getSex().isEmpty()) {
            throw new BadRequestException("Name and sex are required fields");
        }

        // Check for duplicate user
        if (userRepository.existsByNameAndSex(user.getName(), user.getSex())) {
            throw new BadRequestException("User with the same name and sex already exists");
        }

        // Validate zip code if provided
        if (user.getZipCode() != null) {
            ZipCode zipCode = zipCodeRepository.findByCode(user.getZipCode().getCode());
            if (zipCode == null) {
                throw new FailedDependencyException("Zip code is unavailable");
            } else {
                user.setZipCode(zipCode);
            }
        }

        // Save user
        return userRepository.save(user);
    }
}
