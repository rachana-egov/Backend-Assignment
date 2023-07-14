package com.assignment.springboot.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String gender;
    private String mobileNumber;
    private Address address;
    private long createdTime;
    private boolean active;
    public User(){
        this.id = UUID.randomUUID();
        this.createdTime = Instant.now().toEpochMilli();
    }

    public User(UUID id, String name, String gender, String mobileNumber, Address address, long createdTime, boolean active) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.createdTime = createdTime;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", address='" + address + '\'' +
                ", createdTime=" + createdTime +
                ", active=" + active +
                '}';
    }

}
