package com.coherentsolutions.restful.service;

import com.coherentsolutions.restful.exception.BadRequestException;
import com.coherentsolutions.restful.exception.FailedDependencyException;
import com.coherentsolutions.restful.model.User;
import com.coherentsolutions.restful.model.ZipCode;
import com.coherentsolutions.restful.repository.UserRepository;
import com.coherentsolutions.restful.repository.ZipCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(ZipCodeService.class);

    // Regex pattern for a valid email format
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ZipCodeRepository zipCodeRepository;

    public List<User> getUsers(Integer olderThan, Integer youngerThan, String sex) {
        List<User> users = userRepository.findAll();

        Stream<User> userStream = users.stream();

        if (olderThan != null) {
            userStream = userStream.filter(user -> user.getAge() > olderThan);
        }

        if (youngerThan != null) {
            userStream = userStream.filter(user -> user.getAge() < youngerThan);
        }

        if (sex != null && !sex.isEmpty()) {
            userStream = userStream.filter(user -> user.getSex().equalsIgnoreCase(sex));
        }

        return userStream.collect(Collectors.toList());
    }


    @Transactional
    public User createUser(User user) {
        // Validate required fields
        if (user.getName() == null || user.getName().isEmpty() ||
                user.getSex() == null || user.getSex().isEmpty()) {
            throw new BadRequestException("Name and sex are required fields");
        }

        // Validate length of name (e.g., max 255 characters)
        if (user.getName().length() > 255) {
            throw new BadRequestException("Name exceeds the maximum length of 255 characters");
        }

        // Validate email format if provided
        if (user.getEmail() != null && !isValidEmail(user.getEmail())) {
            throw new BadRequestException("Invalid email format");
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

    @Transactional
    public void deleteAllUsers() {
        userRepository.deleteAll(); // Deletes all users from the database
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
