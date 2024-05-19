package com.example.a21521790_weather_app_mobile_final_project_bcu

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text
class MainActivity : AppCompatActivity() {
    ////
    private lateinit var editSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnChangeActivity: Button
    private lateinit var txtName: TextView
    private lateinit var txtCountry: TextView
    private lateinit var txtTemp: TextView
    private lateinit var txtState: TextView
    private lateinit var txtHumidity: TextView
    private lateinit var txtCloud: TextView
    private lateinit var txtWind: TextView
    private lateinit var txtDay: TextView
    private lateinit var imgIcon: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mapping()
    }

    private fun mapping() {
        editSearch = findViewById(R.id.edittextSearch)
        btnSearch = findViewById(R.id.buttonSearch)
        btnChangeActivity = findViewById(R.id.buttonChangeActivity)
        txtName = findViewById(R.id.textviewName)
        txtCountry = findViewById(R.id.textviewCountry)
        txtTemp = findViewById(R.id.textviewTemp)
        txtState = findViewById(R.id.textviewHumidity)
        txtHumidity = findViewById(R.id.textviewHumidity)
        txtCloud = findViewById(R.id.textviewCloud)
        txtWind = findViewById(R.id.textviewWind)
        txtDay = findViewById(R.id.textviewDay)
        imgIcon = findViewById(R.id.imageIcon)
    }

}