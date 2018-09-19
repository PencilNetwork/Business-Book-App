package com.pencil.pencil.businessbook.Business.Pojo;

import com.google.gson.annotations.SerializedName;

public class Business {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private String image;
    @SerializedName("description")
    private String description;
    @SerializedName("logo")
    private String logo;
    @SerializedName("contact_number")
    private String contact_number;
    @SerializedName("city")
    private String city;
    @SerializedName("regoin")
    private String regoin;
    @SerializedName("address")
    private String address;
    @SerializedName("langitude")
    private String langitude;
    @SerializedName("lattitude")
    private String lattitude;
    @SerializedName("category")
    private String categoryId;
    @SerializedName("owner_id")
    private String owner_id;

    public int getId() {
        return id;
    }

    public String getOwner_id() {
        return owner_id;
    }
}
