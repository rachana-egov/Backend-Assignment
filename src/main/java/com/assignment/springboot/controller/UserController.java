package com.assignment.springboot.controller;

import com.assignment.springboot.Services.UserService;
import com.assignment.springboot.messages.Producer;
import com.assignment.springboot.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Producer producer;

    @GetMapping("/user/_search")
    public ResponseEntity<User>getUser(@RequestParam String id, @RequestParam String mobileNumber){
        User foundUser = userService.Search(id, mobileNumber);
        if(foundUser!=null){
            return ResponseEntity.ok(foundUser);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/user")
    public List<User> getUsersByActiveState(@RequestParam boolean active) {
        if (active) {
            return userService.activeUsers();
        } else {
            return userService.inActiveUsers();
        }
    }
    @PostMapping("/user")
    public ResponseEntity<?> createUsers(@RequestBody List<User> users) {
        List<String> errorMessages = new ArrayList<>();

        try {
            producer.UserCreationTopic(users);
        } catch (JsonProcessingException e) {
            errorMessages.add("Error processing users: " + e.getMessage());
        }

        if (errorMessages.isEmpty()) {
            return new ResponseEntity<>("Users were created successfully.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/user")
    public ResponseEntity<String> updateUser(@RequestBody List<User> users) {
        List<User> updateUsers = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        for (User user : users) {
            User existingUser = userService.findById(user.getId());
            System.out.println("Existing User: " + existingUser);

            if (existingUser != null) {
                existingUser.setName(user.getName());
                existingUser.setGender(user.getGender());
                existingUser.setMobileNumber(user.getMobileNumber());
                //existingUser.setAddress(user.getAddress());
                existingUser.setActive(user.isActive());

                updateUsers.add(existingUser);
            } else {
                String errorMessage = String.format("Cannot find User with ID: %s", user.getId());
                errorMessages.add(errorMessage);
            }
        }

        if (!updateUsers.isEmpty()) {
            try {
                producer.UserUpdateTopic(updateUsers);
                return new ResponseEntity<>("User update request sent successfully.", HttpStatus.OK);
            } catch (JsonProcessingException e) {
                errorMessages.add("Error processing user update request: " + e.getMessage());
            }
        }

        if (!errorMessages.isEmpty()) {
            return new ResponseEntity<>(errorMessages.toString(), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("Cannot find Users with provided IDs.", HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String userId) {
        User existingUser = userService.findById(userId);

        if (existingUser != null) {
            try {
                producer.UserDeleteTopic(userId);

                return new ResponseEntity<>("Delete request sent successfully.", HttpStatus.OK);
            } catch (JsonProcessingException e) {
                return new ResponseEntity<>("Error processing delete request.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Cannot find User with the provided ID.", HttpStatus.NOT_FOUND);
        }
    }
}
