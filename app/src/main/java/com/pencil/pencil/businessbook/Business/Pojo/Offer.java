package com.pencil.pencil.businessbook.Business.Pojo;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class Offer {
    @SerializedName("image")
    private String offerImg;
    @SerializedName("caption")
    private String offerText;
    private Bitmap offerBitmap;
    private Uri mUri;
    @SerializedName("id")
    private int id;
    @SerializedName("bussines")
    private BusinessProfileResponse owner;

    public Offer(String img, String text) {
        offerImg = img;
        offerText = text;
    }

    //getter
    public String getOfferImg() {
        return offerImg;
    }

    public String getOfferText() {
        return offerText;
    }

    public Bitmap getOfferBitmap() {
        return offerBitmap;
    }

    public int getId() {
        return id;
    }

    public Uri getmUri() {
        return mUri;
    }
    //setter

    public void setOfferBitmap(Bitmap offerBitmap) {
        this.offerBitmap = offerBitmap;
    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }

    public void setOfferText(String offerText) {
        this.offerText = offerText;
    }
}
