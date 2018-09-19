package com.pencil.pencil.businessbook.Business.Pojo;

import java.util.ArrayList;

public class Owner {
    private int id;
    private String name;
    private String email;
    private String password;
    private String token;
    private String created_at;
    private String updated_at;
    private int owner_id;
    private ArrayList<Business> bussines;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public ArrayList<Business> getBussines() {
        return bussines;
    }
}
