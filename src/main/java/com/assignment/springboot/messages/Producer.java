package com.assignment.springboot.messages;

import com.assignment.springboot.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class Producer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    public void UserCreationTopic(List<User> users) throws JsonProcessingException {
        String userJson = new ObjectMapper().writeValueAsString(users);
        kafkaTemplate.send("user-created", userJson);
    }

    public void UserUpdateTopic(List<User> users) throws JsonProcessingException {
        String userJson = new ObjectMapper().writeValueAsString(users);
        kafkaTemplate.send("user-updated", userJson);
    }

    public void UserDeleteTopic(UUID userId) throws JsonProcessingException {
        String userIdString = userId.toString();
        kafkaTemplate.send("user-deleted", userIdString);
        System.out.println("User delete request sent successfully.");
    }

}
