package com.ayal.honeyimhome;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesManager  {

//    public static LocationInfo homeLocation;

    public static LocationInfo getHomeLocationFromPreferences(Context context) {


        // get the location from the gson
        SharedPreferences sp = context.getSharedPreferences("sp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = sp.getString("location", null);
        LocationInfo homeLocation = gson.fromJson(json, new TypeToken<LocationInfo>() {
        }.getType());
        return homeLocation;
//        editor.apply();

    }

    public static void saveHomeToPreferences(Context context, LocationInfo location) {
        SharedPreferences sp = context.getSharedPreferences("sp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(location);
        editor.putString("location", json);
        editor.apply();
    }
}









