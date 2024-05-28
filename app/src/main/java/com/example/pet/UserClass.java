package com.example.pet;

public class UserClass {
    public int id;
    public String name;
    public String createdDateTime;

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
    public String getCreatedDateTime() {
        return createdDateTime;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
