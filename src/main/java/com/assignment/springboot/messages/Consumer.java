package com.assignment.springboot.messages;

import com.assignment.springboot.Services.UserService;
import com.assignment.springboot.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.internal.org.objectweb.asm.TypeReference;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class Consumer {
    private final UserService userService;

    public Consumer(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics="user-created")
    public void consumeUserCreateRequest(String userJson){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<User> users = objectMapper.readValue(userJson, objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));


            userService.create(users);

            System.out.println("User creation request processed successfully.");
        } catch (IOException e) {
            // Handle deserialization or database errors
            e.printStackTrace();
        }
    }

    @KafkaListener(topics="user-updated")
    public void consumeUserUpdateRequest(String userJson){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<User> users = objectMapper.readValue(userJson, objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));

            userService.update(users)   ;

            System.out.println("User update request processed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics="user-deleted")
    public void consumeUserDeleteRequest(String userId){
        try {
            UUID id = UUID.fromString(userId);
            User existingUser = userService.findById(id);

            if (existingUser != null) {
                userService.deleteById(id);
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("Cannot find User with the provided ID.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid User ID format: " + userId);
        } catch (Exception e) {
            System.out.println("Error deleting User: " + e.getMessage());
        }
    }
}
