package com.assignment.springboot.model;

public class UserSearchCriteria{
    public long id;
    public String mobileNumber;
    public UserSearchCriteria(){

    }

    public UserSearchCriteria(long id, String mobileNumber) {
        this.id = id;
        this.mobileNumber = mobileNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
