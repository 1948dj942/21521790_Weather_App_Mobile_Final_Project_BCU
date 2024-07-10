package com.example.a21521790_weather_app_mobile_final_project_bcu.POJO

import com.google.gson.annotations.SerializedName


data class Coord (

    @SerializedName("lon" ) val lon : Double,
    @SerializedName("lat" ) val lat : Double

)