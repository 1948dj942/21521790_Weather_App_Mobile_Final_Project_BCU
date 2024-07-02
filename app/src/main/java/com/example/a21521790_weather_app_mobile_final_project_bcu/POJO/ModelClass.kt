package com.example.a21521790_weather_app_mobile_final_project_bcu.POJO

import com.google.gson.annotations.SerializedName

data class ModelClass (
    @SerializedName("coord"      ) var coord      : Coord?             = Coord(),
    @SerializedName("weather"    ) var weather    : ArrayList<Weather> = arrayListOf(),
    @SerializedName("wind"       ) var wind       : Wind?              = Wind(),
    @SerializedName("main"       ) var main       : Main?              = Main(),
    @SerializedName("clouds"     ) var clouds     : Clouds?            = Clouds(),
    @SerializedName("sys"        ) var sys        : Sys?               = Sys(),
    @SerializedName("id"         ) var id         : Int?               = null,
    @SerializedName("name"       ) var name       : String?            = null,

    //@SerializedName("base"       ) var base       : String?            = null,
    //@SerializedName("visibility" ) var visibility : Int?               = null,
    //@SerializedName("rain"       ) var rain       : Rain?              = Rain(),
    //@SerializedName("dt"         ) var dt         : Int?               = null,
    //@SerializedName("timezone"   ) var timezone   : Int?               = null,
    //@SerializedName("cod"        ) var cod        : Int?               = null
)