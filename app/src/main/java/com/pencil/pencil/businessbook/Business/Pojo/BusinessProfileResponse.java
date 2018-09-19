package com.pencil.pencil.businessbook.Business.Pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BusinessProfileResponse {
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
    private Category category;
    @SerializedName("owner")
    private Owner owner;
    @SerializedName("average_rating")
    private String average_rating;
    @SerializedName("files")
    private ArrayList<RelatedFile> files;
    @SerializedName("offers")
    private ArrayList<Offer>offers;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getLogo() {
        return logo;
    }

    public String getContact_number() {
        return contact_number;
    }

    public String getCity() {
        return city;
    }

    public String getRegoin() {
        return regoin;
    }

    public String getAddress() {
        return address;
    }

    public String getLangitude() {
        return langitude;
    }

    public String getLattitude() {
        return lattitude;
    }

    public Category getCategory() {
        return category;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getAverage_rating() {
        return average_rating;
    }

    public ArrayList<RelatedFile> getFiles() {
        return files;
    }

    public ArrayList<Offer> getOffers() {
        return offers;
    }
}
