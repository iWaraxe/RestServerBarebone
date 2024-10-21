package com.coherentsolutions.restful.controller;

import com.coherentsolutions.restful.exception.BadRequestException;
import com.coherentsolutions.restful.exception.FailedDependencyException;
import com.coherentsolutions.restful.model.User;
import com.coherentsolutions.restful.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        try {
            return userService.createUser(user);
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (FailedDependencyException e) {
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, e.getMessage(), e);
        }
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllUsers() {
        userService.deleteAllUsers();  // Implement this in the service to delete all users
    }

}
