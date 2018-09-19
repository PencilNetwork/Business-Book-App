package com.pencil.pencil.businessbook.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lincoln on 05/05/16.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
 
    // shared pref mode
    int PRIVATE_MODE = 0;
 
    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";
 
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String LANG = "Language";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    private static final String REMEMBER = "remember";
    private static final String EMAIL = "email";
    public static final String OWNER_ID = "owner";
    public static final String BUSINESS_ID = "business";
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
 
    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
 
    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
    public void setLanguage(String lang) {
        editor.putString(LANG, lang);
        editor.commit();
    }
    public String getLang() {
        return pref.getString(LANG, "eng");
    }
    //login
    public void setLoginData(String username,String password) {
        editor.putString(USER_NAME, username);
        editor.putString(PASSWORD, password);
        editor.putBoolean(REMEMBER, true);
        editor.commit();
    }
    public void resetRemember() {

        editor.putBoolean(REMEMBER, false);
        editor.commit();
    }
    public String getUsername() {
        return pref.getString(USER_NAME, "");
    }
    public String getPassword() {
        return pref.getString(PASSWORD, "");
    }


    public boolean isRemember() {
        return pref.getBoolean(REMEMBER, false);
    }
    public void setData(String key,String value) {
        editor.putString(key, value);


    }
    public String getDataString(String key) {
        return  pref.getString(key, "");
    }
    public void setDataInt(String key,Integer value) {
        editor.putInt(key, value);


    }
    public Integer getDataInt(String key) {
       return  pref.getInt(key, 1);
    }

    public void commit(){
        editor.commit();
    }
}