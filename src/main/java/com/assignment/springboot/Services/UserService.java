package com.assignment.springboot.Services;

import com.assignment.springboot.model.Address;
import com.assignment.springboot.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class UserService {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    ObjectMapper objectMapper;

    private static final String apiUrl = "https://random-data-api.com/api/v2/users?size=1";

    @PostConstruct
    public void createTableIfNotExists() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS my_data (" +
                "id UUID, " +
                "name VARCHAR(255), " +
                "gender VARCHAR(10), " +
                "mobileNumber VARCHAR(20), " +
                "address JSON, " +
                "createdTime BIGINT,"+
                "active BOOLEAN, PRIMARY KEY (id, active)) PARTITION BY LIST (active);"

        );
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS active_user PARTITION OF my_data FOR VALUES IN (TRUE);");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS inactive_user PARTITION OF my_data FOR VALUES IN (FALSE);");
    }

    public List<String> create(List<User> users) {
        List<String> errorMessages = new ArrayList<>();
        for (User user : users) {
            //System.out.println(user);
            if (isUniqueCombination(user.getName(), user.getMobileNumber())) {
                String addressJson;
                try {
                    String jsonString = this.createUsersFromAPI();

                    JsonNode rootNode =  objectMapper.readTree(jsonString);
                    JsonNode addressNode = rootNode.get("address");
                    Address address = objectMapper.treeToValue(addressNode, Address.class);
                    user.setAddress(address);
                    addressJson = objectMapper.writeValueAsString(user.getAddress());
                    //System.out.println(addressNode);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                jdbcTemplate.update(

                        "INSERT INTO my_data (id,name, gender, mobileNumber, address,createdTime,active) VALUES (?,?, ?, ?, ?::json,?,?)",
                        user.getId(),user.getName(), user.getGender(), user.getMobileNumber(), addressJson, user.getCreatedTime(),user.isActive());

            } else {
                String errorMessage = String.format("User with the same name and mobile number already exists: Name: %s, Mobile Number: %s",
                        user.getName(), user.getMobileNumber());
                errorMessages.add(errorMessage);
            }
        }
        return errorMessages;
    }

    private boolean isUniqueCombination(String name, String mobileNumber) {
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM my_data WHERE name = ? AND mobileNumber = ?", Integer.class, name, mobileNumber);
        return count == 0;
    }
    public void update(List<User> users) {
        List<Object[]> batchUser = new ArrayList<>();
        for (User user : users) {
            Object[] newUser = new Object[]{
                    user.getName(),
                    user.getGender(),
                    user.getMobileNumber(),
                    user.isActive(),
                    user.getId()
            };
            batchUser.add(newUser);
        }
        jdbcTemplate.batchUpdate("UPDATE my_data SET name=?, gender=?, mobileNumber=?,active=? WHERE id::uuid=?",
                batchUser);
    }

    public User Search(UUID id, String MobileNumber){
        List<User> user= jdbcTemplate.query("select * from my_data WHERE id::uuid=? AND mobileNumber=?", new UserRowMapper(), id, MobileNumber);

        if(!user.isEmpty()){
            return user.get(0);
        }else{
            return null;
        }
    }
    public List<User> activeUsers(){
        return  jdbcTemplate.query("SELECT * FROM active_user", new UserRowMapper());

    }

    public List<User> inActiveUsers(){
        return  jdbcTemplate.query("SELECT * FROM inactive_user", new UserRowMapper());

    }
    public User findById(UUID id){
        List<User> users = jdbcTemplate.query("select * from my_data WHERE id::uuid=?", new UserRowMapper(),id);
        if (!users.isEmpty()) {
            return users.get(0); // Retrieve the first user from the list
        }
        return null;
    }
    public void deleteById(UUID id){
        jdbcTemplate.update("delete from my_data WHERE id::uuid=?", new Object[]{id});

    }

    public String createUsersFromAPI() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        String responseBody = response.getBody();
        return  responseBody;
    }
}

