package com.coherentsolutions.restful.service;

import com.coherentsolutions.restful.dto.UpdateUserDto;
import com.coherentsolutions.restful.dto.UserDto;
import com.coherentsolutions.restful.exception.BadRequestException;
import com.coherentsolutions.restful.exception.ConflictException;
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
import java.util.Map;
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

        if (userRepository.existsByNameAndSex(user.getName(), user.getSex())) {
            throw new ConflictException("User with the same name and sex already exists");
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
    public User updateUser(Long id, User user, boolean isPartial) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!isPartial) {
            // Validate required fields
            if (user.getName() == null || user.getName().isEmpty() ||
                    user.getSex() == null || user.getSex().isEmpty()) {
                throw new BadRequestException("Name and sex are required fields");
            }

            // Update all fields
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setSex(user.getSex());
            existingUser.setAge(user.getAge());

            // Update zip code
            if (user.getZipCode() != null) {
                ZipCode zipCode = zipCodeRepository.findByCode(user.getZipCode().getCode());
                if (zipCode == null) {
                    throw new FailedDependencyException("Zip code is unavailable");
                } else {
                    existingUser.setZipCode(zipCode);
                }
            } else {
                existingUser.setZipCode(null);
            }
        } else {
            // Partial update logic
            // ...
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public User updateUser(UpdateUserDto updateUserDto) {
        UserDto userToChangeDto = updateUserDto.getUserToChange();
        UserDto userNewValuesDto = updateUserDto.getUserNewValues();

        // Validate that userToChange fields are provided
        if (userToChangeDto.getName() == null || userToChangeDto.getName().isEmpty() ||
                userToChangeDto.getSex() == null || userToChangeDto.getSex().isEmpty()) {
            throw new BadRequestException("Name and sex are required to identify the user to update");
        }

        // Find the user to update
        User existingUser = userRepository.findByNameAndSex(userToChangeDto.getName(), userToChangeDto.getSex())
                .orElseThrow(() -> new BadRequestException("User to update not found"));

        // Update fields
        if (userNewValuesDto.getName() != null) {
            existingUser.setName(userNewValuesDto.getName());
        }
        if (userNewValuesDto.getEmail() != null) {
            existingUser.setEmail(userNewValuesDto.getEmail());
        }
        if (userNewValuesDto.getSex() != null) {
            existingUser.setSex(userNewValuesDto.getSex());
        }
        if (userNewValuesDto.getAge() != null) {
            existingUser.setAge(userNewValuesDto.getAge());
        }

        // Update zip code if provided
        if (userNewValuesDto.getZipCode() != null) {
            ZipCode zipCode = zipCodeRepository.findByCode(userNewValuesDto.getZipCode());
            if (zipCode == null) {
                throw new FailedDependencyException("Zip code is unavailable");
            } else {
                existingUser.setZipCode(zipCode);
            }
        }

        // Validate updated user
        if (existingUser.getName() == null || existingUser.getName().isEmpty() ||
                existingUser.getSex() == null || existingUser.getSex().isEmpty()) {
            throw new BadRequestException("Name and sex are required fields");
        }

        // Validate that required fields are present in userNewValuesDto
        if (userNewValuesDto.getName() == null || userNewValuesDto.getName().isEmpty() ||
                userNewValuesDto.getSex() == null || userNewValuesDto.getSex().isEmpty()) {
            throw new BadRequestException("Name and sex are required fields in new values");
        }

        // Check for duplicate user after update
        boolean duplicateExists = userRepository.existsByNameAndSex(existingUser.getName(), existingUser.getSex())
                && !existingUser.getId().equals(existingUser.getId());
        if (duplicateExists) {
            throw new BadRequestException("User with the same name and sex already exists");
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public User partialUpdateUser(Long id, Map<String, Object> updates) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Apply updates
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    existingUser.setName((String) value);
                    break;
                case "email":
                    existingUser.setEmail((String) value);
                    break;
                case "sex":
                    existingUser.setSex((String) value);
                    break;
                case "age":
                    existingUser.setAge((Integer) value);
                    break;
                case "zipCode":
                    Map<String, String> zipCodeMap = (Map<String, String>) value;
                    String zipCodeStr = zipCodeMap.get("code");
                    ZipCode zipCode = zipCodeRepository.findByCode(zipCodeStr);
                    if (zipCode == null) {
                        throw new FailedDependencyException("Zip code is unavailable");
                    } else {
                        existingUser.setZipCode(zipCode);
                    }
                    break;
                default:
                    // Ignore unknown fields
                    break;
            }
        });

        // Validate required fields if missing after update
        if (existingUser.getName() == null || existingUser.getName().isEmpty() ||
                existingUser.getSex() == null || existingUser.getSex().isEmpty()) {
            throw new BadRequestException("Name and sex are required fields");
        }

        return userRepository.save(existingUser);
    }


    @Transactional
    public void deleteAllUsers() {
        userRepository.deleteAll(); // Deletes all users from the database
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
