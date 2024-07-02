package com.example.a21521790_weather_app_mobile_final_project_bcu.Utilities

import com.example.a21521790_weather_app_mobile_final_project_bcu.POJO.ModelClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface API_Interface {
    @GET("weather")
    fun getCurrentWeatherData(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") api_key: String
    ): Call<ModelClass>

    @GET("weather")
    fun getCityWeatherData(
        @Query("q") cityName: String,
        @Query("appid") api_key: String
    ): Call<ModelClass>
}