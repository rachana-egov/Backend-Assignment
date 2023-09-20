package com.assignment.springboot.Services;

import com.assignment.springboot.model.Address;
import com.assignment.springboot.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
            user.setGender(rs.getString("gender"));
            //user.setAddress((Address) rs.getObject("address"));
            String addressJson = rs.getString("address");
            if (addressJson != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                Address address = null;
                try {
                    address = objectMapper.readValue(addressJson, Address.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                user.setAddress(address);
            }
            user.setMobileNumber(rs.getString("mobileNumber"));
            user.setActive(rs.getBoolean("active"));
            user.setCreatedTime(rs.getLong("createdTime"));
            return user;
    }
}

