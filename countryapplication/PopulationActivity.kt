package com.acelyauslu.countryapplication


import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.graphics.BitmapFactory
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import com.android.volley.toolbox.Volley
import com.android.volley.*

import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.acelyauslu.countryapplication.adapter.PopulationAdapter
import com.acelyauslu.countryapplication.model.CountryModel



class PopulationActivity : AppCompatActivity() {

    private lateinit var map: GoogleMap
    private lateinit var countryModelList: ArrayList<CountryModel>
    private var populationAdapter: PopulationAdapter? = null
    private lateinit var  countryName:String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_population)
        val loadingDialog = Dialog(this)
        loadingDialog.setContentView(R.layout.loading)
        loadingDialog.window!!.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        countryModelList = ArrayList()
        countryName = intent.getStringExtra("country_name")!!
        val byteArray: ByteArray = intent.extras!!.getByteArray("country_image")!!

        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        findViewById<TextView>(R.id.countryName).text = countryName
        findViewById<ImageView>(R.id.imageView2).setImageBitmap(bmp)
        val countryRV = findViewById<RecyclerView>(R.id.recycler_view)
        val jsonObject = JSONObject()
        jsonObject.put("country", countryName)

       //Population için api :https://countriesnow.space/api/v0.1/countries/population
        val apiKey = "https://countriesnow.space/api/v0.1/countries/population"
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            apiKey, jsonObject,
            Response.Listener { response ->

                println("Request Successfully!!")
                val dataArray = response.getJSONObject("data")

                val populationCounts = dataArray.getJSONArray("populationCounts")
                var sum = 0
                for (i in 0 until populationCounts.length()) {
                    val dara = populationCounts.getJSONObject(i)
                    val year = dara.getLong("year")
                    val value = dara.getLong("value")
                    countryModelList.add(CountryModel(year.toString(), value.toString()))
                    sum += value.toInt()
                }
                val result = sum/populationCounts.length()
                findViewById<TextView>(R.id.average_population).text = "Average Population: $result"
                loadingDialog.dismiss()
                populationAdapter = PopulationAdapter(countryModelList)

                countryRV.adapter = populationAdapter
            }, Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)

            override fun getHeaders(): Map<String, String>
            {
                val headers: HashMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.google_map) as SupportMapFragment

        //2.sayfa konum için api:https://countriesnow.space/api/v0.1/countries/positions
        val apiKey1 = "https://countriesnow.space/api/v0.1/countries/positions"

        val jsonObjectRequest1: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            apiKey1, jsonObject,
            Response.Listener { response ->
                val dataArray = response.getJSONObject("data")
                val longitude = dataArray.getLong("long")
                val latitude = dataArray.getLong("lat")
                mapFragment.getMapAsync { googleMap ->
                    map = googleMap
                    map.uiSettings.isZoomControlsEnabled = true
                    val myPlace = LatLng(latitude.toDouble(), longitude.toDouble())
                    map.addMarker(MarkerOptions().position(myPlace).title(countryName))
                    map.moveCamera(CameraUpdateFactory.newLatLng(myPlace))
                }
                loadingDialog.dismiss()
            }, Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: HashMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        //  retry policy
        val socketTime = 3000
        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTime,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        jsonObjectRequest.retryPolicy = policy
        jsonObjectRequest1.retryPolicy = policy

        // request
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
        requestQueue.add(jsonObjectRequest1)
    }

}