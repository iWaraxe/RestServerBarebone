package com.coherentsolutions.restful.controller;

import com.coherentsolutions.restful.dto.UpdateUserDto;
import com.coherentsolutions.restful.dto.UserDto;
import com.coherentsolutions.restful.exception.BadRequestException;
import com.coherentsolutions.restful.model.User;
import com.coherentsolutions.restful.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers(
            @RequestParam(required = false) String olderThan,
            @RequestParam(required = false) String youngerThan,
            @RequestParam(required = false) String sex) {
        try {
            Integer olderThanInt = olderThan != null ? Integer.parseInt(olderThan) : null;
            Integer youngerThanInt = youngerThan != null ? Integer.parseInt(youngerThan) : null;
            return userService.getUsers(olderThanInt, youngerThanInt, sex);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid age parameter", e);
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
            return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody UpdateUserDto updateUserDto) {
        return userService.updateUser(updateUserDto);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user, false);
    }

    @PatchMapping("/{id}")
    public User partialUpdateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return userService.partialUpdateUser(id, updates);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@RequestBody UserDto userDto) {
        userService.deleteUser(userDto);
    }

    // Existing method to delete all users remains
    @DeleteMapping("/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }

    // Add new endpoint for uploading users
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> uploadUsers(@RequestParam("file") MultipartFile file) {
        return userService.uploadUsers(file);
    }

}
