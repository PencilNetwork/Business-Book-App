package com.pencil.pencil.businessbook.Business.Pojo;

import com.google.gson.annotations.SerializedName;

public class CreateBusinessInput{
        @SerializedName("name")
        private String username;
    @SerializedName("description")
    private String description;
    @SerializedName("contact_number")
    private String contact_number;
    @SerializedName("city")
    private String city;
    @SerializedName("regoin")
    private String regoin;
    @SerializedName("address")
    private String address;
    @SerializedName("langitude")
    private String longitude;
    @SerializedName("lattitude")
    private String lattitude;
    @SerializedName("category_id")
    private String category_id;
    @SerializedName("owner_id")
    private String owner_id;
    @SerializedName("image")
    private String image;
    @SerializedName("logo")
    private String logo;

public CreateBusinessInput(String username,String description,String contact_number,String city,String regoin,String address,String longitude,String lattitude,String category_id,String owner_id){
    this.username=username;
    this.description=description;
    this.contact_number=contact_number;
    this.city=city;
    this.regoin=regoin;
    this.address=address;
    this.lattitude=lattitude;
    this.longitude=longitude;
    this.category_id=category_id;
    this.owner_id=owner_id;
}





}
