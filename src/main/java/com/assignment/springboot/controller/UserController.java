package com.assignment.springboot.controller;

import com.assignment.springboot.Services.UserService;
import com.assignment.springboot.model.User;
import com.assignment.springboot.model.UserSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/user/_search")
    public ResponseEntity<User>getUser(@RequestBody UserSearchCriteria user){
        User foundUser = userService.Search(user.getId(), user.getMobileNumber());
        if(foundUser!=null){
            return ResponseEntity.ok(foundUser);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            //System.out.println(user);
            userService.create(user);
            return new ResponseEntity<>("User was created successfully.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/user/{id}")
    public ResponseEntity<String> updateUser(@PathVariable("id") long id, @RequestBody User user) {
        User user1 = new User();

        if (user1 != null) {
            user1.setId(id);
            user1.setName(user.getName());
            user1.setGender(user.getGender());
            user1.setMobileNumber(user.getMobileNumber());
            user1.setAddress(user.getAddress());

            userService.update(user1);
            return new ResponseEntity<>("User was updated successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Cannot find User with id=" + id, HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {
        try {
            userService.deleteById(id);
            return new ResponseEntity<>("User was deleted successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Cannot delete User.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
