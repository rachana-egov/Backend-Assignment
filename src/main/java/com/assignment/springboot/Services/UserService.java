package com.assignment.springboot.Services;

import com.assignment.springboot.model.Address;
import com.assignment.springboot.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    @Autowired
    private Environment environment;

    @PostConstruct
    public void createTableIfNotExists() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS my_data (" +
                "id VARCHAR(255), " +
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
                String generatedId = generateIdFromIdGenApi();
                if (generatedId != null) {
                    user.setId(generatedId);
                } else {
                    errorMessages.add("Failed to generate ID from the API.");
                }
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
        jdbcTemplate.batchUpdate("UPDATE my_data SET name=?, gender=?, mobileNumber=?,active=? WHERE id=?",
                batchUser);
    }

    public User Search(String id, String MobileNumber){
        List<User> user= jdbcTemplate.query("select * from my_data WHERE id=? AND mobileNumber=?", new UserRowMapper(), id, MobileNumber);

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
    public User findById(String id){
        List<User> users = jdbcTemplate.query("select * from my_data WHERE id=?", new UserRowMapper(),id);
        if (!users.isEmpty()) {
            return users.get(0); // Retrieve the first user from the list
        }
        return null;
    }
    public void deleteById(String id){
        jdbcTemplate.update("delete from my_data WHERE id=?", new Object[]{id});

    }

    public String createUsersFromAPI() {
        String apiUrl = environment.getProperty("api.url");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        String responseBody = response.getBody();
        return  responseBody;
    }

    public String generateIdFromIdGenApi() {
        try {
            String idGenUrl = "http://localhost:8088/egov-idgen/id/_generate"; // The API endpoint URL

            // Create the request body as per the API specification
            String requestBody = "{\"RequestInfo\": {}, \"idRequests\": [{\"tenantId\": \"pb\",\"idName\": \"my.user-service.receipt.id\"}] }";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(idGenUrl, requestEntity, String.class);
            String responseBody = response.getBody();

            // Parse the response to get the ID field
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode idResponsesArray = rootNode.get("idResponses");
            if (idResponsesArray != null && idResponsesArray.isArray() && idResponsesArray.size() > 0) {
                JsonNode firstIdResponse = idResponsesArray.get(0);
                JsonNode idNode = firstIdResponse.get("id");
                if (idNode != null && idNode.isTextual()) {
                    String generatedId = idNode.asText();
                    //System.out.println("Generated ID: " + generatedId);
                    return generatedId;
                }
            }

            // Return null if the ID is not found in the response or the response is empty
            System.out.println("Failed to generate ID from the API.");
            return null;
        } catch (Exception e) {
            // Handle any exceptions or errors that might occur during the request
            e.printStackTrace();
            return null;
        }
    }

}

