package com.assignment.springboot.controller;

import com.assignment.springboot.Services.UserService;
import com.assignment.springboot.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/user/_search")
    public ResponseEntity<User>getUser(@RequestParam UUID id, @RequestParam String mobileNumber){
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
    public ResponseEntity<String> createUsers(@RequestBody List<User> users) {
        try {
            userService.create(users);
            return new ResponseEntity<>("Users were created successfully.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred during user creation.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/user")
    public ResponseEntity<String> updateUser(@RequestBody List<User> users) {
        List<User> updateUsers = new ArrayList<>();
        for (User user : users) {
            User existingUser = userService.findById(user.getId());

            if (existingUser != null) {
                existingUser.setName(user.getName());
                existingUser.setGender(user.getGender());
                existingUser.setMobileNumber(user.getMobileNumber());
                existingUser.setAddress(user.getAddress());
                existingUser.setActive(user.isActive());

                updateUsers.add(existingUser);
            }
        }
        if (!updateUsers.isEmpty()) {
            userService.update(updateUsers);
            return new ResponseEntity<>("Users were updated successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Cannot find Users with provided IDs.", HttpStatus.NOT_FOUND);
        }

    }
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") UUID id) {
        try {
            userService.deleteById(id);
            return new ResponseEntity<>("User was deleted successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Cannot delete User.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
