package com.example.a21521790_weather_app_mobile_final_project_bcu.POJO

import com.google.gson.annotations.SerializedName


data class Wind (

    @SerializedName("speed" ) var speed : Double? = null,
    @SerializedName("deg"   ) var deg   : Int?    = null,
    @SerializedName("gust"  ) var gust  : Double? = null

)