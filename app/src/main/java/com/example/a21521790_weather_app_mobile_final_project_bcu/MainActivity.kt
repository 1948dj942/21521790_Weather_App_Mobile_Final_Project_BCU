package com.example.a21521790_weather_app_mobile_final_project_bcu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.a21521790_weather_app_mobile_final_project_bcu.POJO.ModelClass
import com.example.a21521790_weather_app_mobile_final_project_bcu.Utilities.APIUtilities
import com.example.a21521790_weather_app_mobile_final_project_bcu.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import kotlin.math.roundToInt

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
        activityMainBinding.etGetCityName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                      getCityWeather(activityMainBinding.etGetCityName.text.toString())
                val view = this.currentFocus
                if (view != null) {
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    activityMainBinding.etGetCityName.clearFocus()
                }
                true
            } else {
                false
            }
        }
    }

//    private fun getCityWeather(cityName: String) {
//        activityMainBinding.pbLoading.visibility = View.GONE
//        APIUtilities.getAPI_Interface()?.getCityWeatherData(cityName, API_KEY)?.enqueue(object :
//            Callback<ModelClass> {
//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
//                setDataOnViews(response.body())
//            }
//
//            override fun onFailure(call: Call<ModelClass>, t: Throwable) {
//                Toast.makeText(applicationContext, "Not a valid city", Toast.LENGTH_SHORT).show()
//            }
//
//        })
//    }

    private fun getCityWeather(cityName: String) {
        //based input validation
        if(cityName.isBlank()) {
            Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
                return
        }
        activityMainBinding.pbLoading.visibility = View.VISIBLE //show loading indicator
        APIUtilities.getAPI_Interface()?.getCityWeatherData(cityName, API_KEY)?.enqueue(object: Callback<ModelClass>
        {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                runOnUiThread { //update UI on the main thread
                    activityMainBinding.pbLoading.visibility = View.GONE //hide loading indicator
                    if(response.isSuccessful && response.body()!=null) {
                        Log.d("API Response", "Success: ${response.body()}") // Log the response
                        setDataOnViews(response.body())
                    }
                    else {
                        //handle API errors
                        val errorMessage = when(response.code()) {
                            404 -> "City not found"
                            401 -> "Invalid API key"
                            else -> "Unknown error"
                        }
                        Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                runOnUiThread{
                    activityMainBinding.pbLoading.visibility = View.GONE
                    Toast.makeText(applicationContext, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
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
            @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setDataOnViews(body: ModelClass?) {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm")
        val currentDate = sdf.format(Date())

        activityMainBinding.tvDateAndTime.text = currentDate
        activityMainBinding.tvDayMaxTemp.text = "Day "+ kelvinToCelsius(body!!.main.tempMax)+ "°"
        activityMainBinding.tvDayMinTemp.text = "Night "+ kelvinToCelsius(body.main.tempMin) + "°"

        activityMainBinding.tvTemp.text = "" + kelvinToCelsius(body.main.temp) + "°"
        activityMainBinding.tvFeelsAlike.text = "Feels like " + kelvinToCelsius(body.main.feelsLike) + "°"

        activityMainBinding.tvWeatherStatus.text = body.weather[0].main

        activityMainBinding.tvSunrise.text = timeStampToLocalDate(body.sys.sunrise.toLong())
        activityMainBinding.tvSunset.text = timeStampToLocalDate(body.sys.sunset.toLong())

        activityMainBinding.tvPressure.text = body.main.pressure.toString()
        activityMainBinding.tvHumidity.text = body.main.humidity.toString() + " %"
        activityMainBinding.tvWindSpeed.text = body.wind.speed.toString() + " m/s"

        val tempCelsius = body.main.temp
        activityMainBinding.tvTempF.text =""+ ((tempCelsius * 1.8) + 32).roundToInt().toString()

        activityMainBinding.etGetCityName.setText(body.name)

        val weatherId = body.weather[0].id
        updateUI(weatherId)
    }

    private fun updateUI(id: Int?) {
        //thunderstorm
        when (id) {
            in 200..232 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.thunderstorm)

                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.thunderstorm))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.thunderstrom_bg)

                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.thunderstrom_bg)
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.thunderstrom_bg)

                activityMainBinding.ivWeatherBg.setImageResource(R.drawable.thunderstrom_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.thunderstorm_)
            }
            //Drizzle
            in 300..321 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.drizzle)

                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.drizzle))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.drizzle_bg)

                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.drizzle_bg)
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.drizzle_bg)

                activityMainBinding.ivWeatherBg.setImageResource(R.drawable.drizzle_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.drizzle_)
            }
            //Rain
            in 500..532 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.rain)

                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.rain))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rain_bg)

                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rain_bg)
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.rain_bg)

                activityMainBinding.ivWeatherBg.setImageResource(R.drawable.rain_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.rain_)
            }
            //Snow
            in 600..622 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.snow)

                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.snow))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.snow_bg)

                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.snow_bg)
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.snow_bg)

                activityMainBinding.ivWeatherBg.setImageResource(R.drawable.snow_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.snow_)
            }
            //Atmosphere
            in 701..781 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.atmosphere)

                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.atmosphere))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.atmosphere_bg)

                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.atmosphere_bg)
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.atmosphere_bg)

                activityMainBinding.ivWeatherBg.setImageResource(R.drawable.atmosphere_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.atmosphere_)
            }
            //Clear
            800 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.clear)

                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.clear))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.clear_bg)

                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.clear_bg)
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.clear_bg)

                activityMainBinding.ivWeatherBg.setImageResource(R.drawable.clear_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.clear_)
            }
            //Clouds
            in 801..804 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.cloudy)

                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.cloudy))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.clouds_bg)

                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.clouds_bg)
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.clouds_bg)

                activityMainBinding.ivWeatherBg.setImageResource(R.drawable.clouds_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.cloud_)
            }
        }

        activityMainBinding.pbLoading.visibility = View.GONE
        activityMainBinding.rlMainLayout.visibility = View.VISIBLE
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private  fun timeStampToLocalDate(timeStamp: Long): String {
        val localTime = timeStamp.let {
            Instant.ofEpochSecond(it)
                .atZone((ZoneId.systemDefault()))
                .toLocalTime()
        }

        return localTime.toString()
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
        const val API_KEY = "25fe3891c3f9dab6e259618892ec2507" // input API key
    }

    private fun checkPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
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


