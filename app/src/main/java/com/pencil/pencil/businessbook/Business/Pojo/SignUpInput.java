package com.pencil.pencil.businessbook.Business.Pojo;

import com.google.gson.annotations.SerializedName;

public class SignUpInput {
    @SerializedName("name")
    private String username;

    @SerializedName("password")
    private String password;
    @SerializedName("email")
    private String email;
    @SerializedName("token")
    private String token;
public  SignUpInput(String username,String password,String email,String token){
    this.username=username;
    this.password=password;
    this.email=email;
    this.token=token;
}


}
