package com.acelyauslu.countryapplication


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.*


import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.acelyauslu.countryapplication.adapter.CountryAdapter
import com.acelyauslu.countryapplication.model.CountryModel
import android.graphics.Bitmap
import android.text.Editable
import android.text.TextWatcher

import java.io.ByteArrayOutputStream
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity()
{
    private lateinit var countryModelList: ArrayList<CountryModel>
    private var countryAdapter: CountryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        val loadingDialog = Dialog(this)
        loadingDialog.setContentView(R.layout.loading)
        loadingDialog.window!!.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        loadingDialog.setCancelable(false)
        countryModelList = ArrayList()
        loadingDialog.show()

        //api:https://countriesnow.space/api/v0.1/countries/flag/unicode -hata verdi
        //İlk sayfa için api:https://countriesnow.space/api/v0.1/countries/flag/images
        val apiKey = "https://countriesnow.space/api/v0.1/countries/flag/images"

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            apiKey, null,
            Response.Listener { response ->

                println("Request Successfully!")
                val dataArray = response.getJSONArray("data")
                for (i in 0 until dataArray.length())
                {
                    val dara = dataArray.getJSONObject(i)
                    val name = dara["name"].toString()
                    val flag = dara["flag"].toString()
                    countryModelList.add(CountryModel(name, flag))
                }
                loadingDialog.dismiss()
            }, Response.ErrorListener { }) {
        }

        // retryPolicy
        val socketTime = 3000
        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTime,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        jsonObjectRequest.retryPolicy = policy

        //Volley-> Android uygulamaları için ağ oluşturmayı  hızlandıran HTTP kütüphanesi
        // request
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)

        val countryImg = findViewById<ImageView>(R.id.countryImg)
        val textName = findViewById<TextView>(R.id.countryName)
        val countryLL = findViewById<LinearLayout>(R.id.ccp)
        val countryDialog = Dialog(this)

        countryDialog.setContentView(R.layout.country_layout)
        countryDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        countryDialog.setCancelable(true)
        val countryRV = countryDialog.findViewById<RecyclerView>(R.id.recycler_view)
        val closeImg: ImageView = countryDialog.findViewById(R.id.close_img)
        val Search: EditText = countryDialog.findViewById(R.id.search)

        Search.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        Search.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
            {
                if (Search.text.isNotEmpty())
                {
                    val freeServiceModelArrayList: ArrayList<CountryModel> = ArrayList()

                    for (i in countryModelList)
                    {
                        if (i.getName().lowercase(Locale.getDefault()).contains(
                                Search.text.toString().lowercase(
                                    Locale.getDefault() )
                            )
                        ) {
                            freeServiceModelArrayList.add(i)
                        }
                    }
                    countryAdapter = CountryAdapter(
                        this@MainActivity,
                        freeServiceModelArrayList,
                        countryDialog,
                        countryImg,
                        textName)
                    countryRV.adapter = countryAdapter
                    countryAdapter!!.notifyDataSetChanged()
                }
                else
                {
                    countryAdapter = CountryAdapter(
                        this@MainActivity,
                        countryModelList,
                        countryDialog,
                        countryImg,
                        textName)
                    countryRV.adapter = countryAdapter
                    countryAdapter!!.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        countryAdapter = CountryAdapter(
            this,
            countryModelList,
            countryDialog,
            countryImg,
            textName
        )
        countryRV.adapter = countryAdapter

        closeImg.setOnClickListener { countryDialog.dismiss() }
        countryLL.setOnClickListener{
            countryDialog.show()
        }

        findViewById<Button>(R.id.btnContinue).setOnClickListener{
            if (textName.text.toString() != "Select Country")
            {//ülke seçilip devam edildiğinde

                val bitmap = countryImg.drawable.toBitmap()
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

                val byteArray: ByteArray = stream.toByteArray()
                val intent = Intent(this, PopulationActivity::class.java)

                //putExtra bilgi göndermek için kullanılır
                intent.putExtra("country_name", textName.text.toString())
                intent.putExtra("country_image", byteArray)
                startActivity(intent)
            }
            else
            { //ülke seçilmeden devam edildiğinde
                Toast.makeText(this,"Select Country",Toast.LENGTH_LONG).show()
            }
        }
    }
}