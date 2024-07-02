package com.example.a21521790_weather_app_mobile_final_project_bcu.POJO

import com.google.gson.annotations.SerializedName


data class Sys (

    @SerializedName("type"    ) var type    : Int?    = null,
    @SerializedName("id"      ) var id      : Int?    = null,
    @SerializedName("country" ) var country : String? = null,
    @SerializedName("sunrise" ) var sunrise : Int?    = null,
    @SerializedName("sunset"  ) var sunset  : Int?    = null

)