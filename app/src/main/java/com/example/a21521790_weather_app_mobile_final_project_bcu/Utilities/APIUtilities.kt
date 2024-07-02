package com.example.a21521790_weather_app_mobile_final_project_bcu.Utilities

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIUtilities {
    private var retrofit:Retrofit? = null //initial declaration
    var BASE_URL = "https://api.openweathermap.org/data/2.5/"; //base URL of the OpenWeatherMap API

    //this method is declare to return an API_Interface object
    fun getAPI_Interface():API_Interface?{
        if(retrofit == null){
            //create a retrofit object use GsonConverterFactory to convert JSON data into Java objects
            retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        }
        //use the create() method to create an API_Interface object and return it
        return retrofit!!.create(API_Interface::class.java)
    }

}