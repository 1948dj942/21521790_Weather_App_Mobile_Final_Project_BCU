package com.example.a21521790_weather_app_mobile_final_project_bcu.POJO

import com.google.gson.annotations.SerializedName


data class Coord (

    @SerializedName("lon" ) var lon : Double? = null,
    @SerializedName("lat" ) var lat : Double? = null

)