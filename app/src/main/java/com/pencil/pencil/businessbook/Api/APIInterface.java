package com.pencil.pencil.businessbook.Api;



import com.pencil.pencil.businessbook.Business.Pojo.BusinessProfileResponse;
import com.pencil.pencil.businessbook.Business.Pojo.Category;
import com.pencil.pencil.businessbook.Business.Pojo.CreateBusinessInput;
import com.pencil.pencil.businessbook.Business.Pojo.GeneralResponseBody;
import com.pencil.pencil.businessbook.Business.Pojo.Owner;
import com.pencil.pencil.businessbook.Business.Pojo.SignUpInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import com.pencil.pencil.businessbook.Api.ResponseBody;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface APIInterface {
    @Headers("Content-Type: application/json")
    @POST("owner/signup")
    Call<ResponseBody> signup(@Body SignUpInput params);

    @Multipart
    @POST("bussines/store")
         Call<GeneralResponseBody<BusinessProfileResponse>> createBusiness(@Part("name") RequestBody username, @Part("description")RequestBody description, @Part("contact_number")RequestBody contact_number, @Part("city")RequestBody city, @Part("regoin")RequestBody regoin, @Part("address")RequestBody address, @Part("langitude")RequestBody langitude, @Part("lattitude")RequestBody lattitude, @Part("category_id")RequestBody category_id, @Part("owner_id")RequestBody owner_id, @Part MultipartBody.Part image, @Part MultipartBody.Part logo);
    //Call<Object> createBusiness(@PartMap() Map<String, String> partMap, @Part RequestBody image, @Part RequestBody logo);
   // Call<Object> createBusiness(@PartMap() RequestBody partMap, @Part RequestBody image, @Part RequestBody logo);
    @Headers("Content-Type: application/json")
    @POST("owner/login")
    Call<GeneralResponseBody<Owner>> login(@QueryMap Map<String, String> params);
    @Multipart
    @POST("files/store")
    Call<GeneralResponseBody<Object>> addRelatedFile( @Part List<MultipartBody.Part> bussines ,@Part("bussines_id")RequestBody businessId);
    @Headers("Content-Type: application/json")
    @GET("bussines/{bussines}")
    Call<GeneralResponseBody<BusinessProfileResponse>> getProfileData(@Path("bussines")int bussines );
    @Multipart
    @POST("files/{bussines}")
    Call<GeneralResponseBody<Object>> updateRelatedFile(@Path("bussines")int bussines,@Part MultipartBody.Part image );

    @DELETE("files/{id}")
    Call<GeneralResponseBody<Object>> deleteRelatedFile(@Path("id")int idbussines);

    @Multipart
    @POST("offers")
    Call<GeneralResponseBody<Object>> createOffer( @Part MultipartBody.Part image ,@Part("caption") RequestBody caption,@Part("bussines_id")RequestBody businessId);
    @Multipart

    @POST("bussines/{id}")
    Call<GeneralResponseBody<Object >> editProfile(@Path("id")int idbussines,@Part("name") RequestBody username, @Part("description")RequestBody description, @Part("contact_number")RequestBody contact_number, @Part("city")RequestBody city, @Part("regoin")RequestBody regoin, @Part("address")RequestBody address, @Part("langitude")RequestBody langitude, @Part("lattitude")RequestBody lattitude, @Part("category_id")RequestBody category_id, @Part("owner_id")RequestBody owner_id, @Part MultipartBody.Part image, @Part MultipartBody.Part logo);

    @Multipart
    @POST("offers/{id}")
    Call<GeneralResponseBody<Object>> updateOffer(@Path("id")int bussines,@Part("caption") RequestBody caption,@Part MultipartBody.Part image);
    @DELETE("offers/{id}")
    Call<GeneralResponseBody<Object>> deleteOffer(@Path("id")int idbussines);
    @Headers("Content-Type: application/json")
    @GET("categories")
    Call<GeneralResponseBody<ArrayList<Category>>> getCategory();


    @POST("owner/mail")
    Call<GeneralResponseBody<Object >>  forgetPassword(@Query("email") String email);


}