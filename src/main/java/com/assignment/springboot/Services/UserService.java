package com.assignment.springboot.Services;

import com.assignment.springboot.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class UserService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void createTableIfNotExists() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS my_data (" +
                "id BIGINT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "gender VARCHAR(10), " +
                "mobileNumber VARCHAR(20), " +
                "address VARCHAR(255))");
    }
    public void create(User user){
        System.out.println(user);
         jdbcTemplate.update("INSERT into my_data (id,name,gender,mobileNumber,address) VALUES(?,?,?,?,?)",
                user.getId(),user.getName(),user.getGender(),user.getMobileNumber(),user.getAddress());
//         System.out.println(user);
    }
    public void update(User user){
        jdbcTemplate.update("update my_data set name=?,gender=?,mobileNumber=?,address=?where id=?",
        user.getName(), user.getGender(),user.getMobileNumber(),user.getAddress(), user.getId());
    }
    public User Search(long id, String MobileNumber){
        return jdbcTemplate.queryForObject("select * from my_data where id=? AND mobileNumber=?", new Object[]{id, MobileNumber},
                new BeanPropertyRowMapper<User>(User.class));
    }

    public User findByMobileNumber(String mobileNumber){
        return jdbcTemplate.queryForObject("select * from my_data where mobileNumber=?", new Object[]{mobileNumber},
                new BeanPropertyRowMapper<User>(User.class));
    }

    public void deleteById(long id){
        jdbcTemplate.update("delete from my_data where id=?", new Object[]{id});

    }
}
