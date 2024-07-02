package com.example.a21521790_weather_app_mobile_final_project_bcu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.a21521790_weather_app_mobile_final_project_bcu.POJO.ModelClass
import com.example.a21521790_weather_app_mobile_final_project_bcu.Utilities.APIUtilities
import com.example.a21521790_weather_app_mobile_final_project_bcu.Utilities.API_Interface
import com.example.a21521790_weather_app_mobile_final_project_bcu.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        activityMainBinding.rlMainLayout.visibility = View.GONE

        getCurrentLocation()
    }

    //@SuppressLint("SetTextI18n")
    private fun getCurrentLocation() {
        if(checkPermission())
        {
            if(isLocationEnabled()) {
                //final location here:
                if(ActivityCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task->
                    val location: Location?=task.result
                    if(location==null)
                    {
                        Toast.makeText(this, "Null Received", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        //fetch weather
                        fetchCurrentLocationWeather(
                            location.latitude.toString(),
                            location.longitude.toString()
                        )
                    }

                }
            }
            else {
                //setting open here
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent= Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        }
        else {
            //request permission
            requestPermission()
        }
    }

    private fun fetchCurrentLocationWeather(latitude: String, longitude: String) {
        activityMainBinding.pbLoading.visibility = View.VISIBLE
        APIUtilities.getAPI_Interface()?.getCurrentWeatherData(latitude, longitude, API_KEY)?.enqueue(object :
            Callback<ModelClass>{
            override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                if(response.isSuccessful) {
                    setDataOnViews(response.body())
                }
            }

            override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                Toast.makeText(applicationContext, "ERROR", Toast.LENGTH_SHORT).show()
            }

        })

    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setDataOnViews(body: ModelClass?) {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm")
        val currentDate = sdf.format(Date())
        activityMainBinding.tvDateAndTime.text = currentDate
        activityMainBinding.tvDayMaxTemp.text = "Day "+ body!!.main?.tempMax?.let { kelvinToCelsius(it) } + "°"
        activityMainBinding.tvDayMinTemp.text = "Day "+ body.main?.tempMin?.let { kelvinToCelsius(it) } + "°"
    }

    private fun kelvinToCelsius(temp: Double): Double {
        var intTemp = temp
        intTemp = intTemp.minus(273)
        return intTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }

    private fun isLocationEnabled():Boolean {
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //checking location is enabled by GPS or Network
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf( android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    companion object{
        private  const val  PERMISSION_REQUEST_ACCESS_LOCATION = 100
        const val API_KEY = "618c2c71cae71f2816c3191b9cf58989"
    }

    private fun checkPermission(): Boolean {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode== PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}