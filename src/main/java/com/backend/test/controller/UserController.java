package com.backend.test.controller;

import com.backend.test.entity.User;
import com.backend.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) String firstname) {
        try {
            List<User> users = firstname == null ? userService.findAll() : userService.findAll();
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        User user = userService.findById(id).orElse(null);

        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            userService.save(user);
            return new ResponseEntity<>("User was created successfully.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable("id") long id, @RequestBody User user) {
        User userExist = userService.findById(id).orElse(null);

        if (userExist != null) {
            userExist.setFirstname(user.getFirstname());
            userExist.setLastname(user.getLastname());
            userExist.setEmail(user.getEmail());
            userExist.setAddress(user.getAddress());

            userService.update(userExist);
            return new ResponseEntity<>("User was updated successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Cannot find User with id=" + id, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {
        try {
            int result = userService.deleteById(id);
            if (result == 0) {
                return new ResponseEntity<>("Cannot find User with id=" + id, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("User was deleted successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}