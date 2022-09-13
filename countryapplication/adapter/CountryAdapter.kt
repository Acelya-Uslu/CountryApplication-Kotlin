package com.acelyauslu.countryapplication.adapter


import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener
import com.acelyauslu.countryapplication.R
import com.acelyauslu.countryapplication.model.CountryModel
import java.util.ArrayList


class CountryAdapter(

    private val context: Context,
    private var countryModelList: ArrayList<CountryModel>,
    private var countryDialog: Dialog,
    val countryImg: ImageView,
    private val txtname: TextView

) : RecyclerView.Adapter<CountryAdapter.ViewHolder>()
{//ViewHolder sınıfı oluştur


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView)
    {
        val flag = itemView.findViewById<ImageView>(R.id.country_flag)
        val name = itemView.findViewById<TextView>(R.id.country_title)
        val layoutClick = itemView.findViewById<LinearLayout>(R.id.rootView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    { //ViewHolder döndürüyoruz
        //Layout ile adapteri birbirine bağlıyoruz->LayoutInflater kullan

        val view: View =
            LayoutInflater.from(parent.context)
            .inflate(R.layout.country_item_layout, parent, false)
                     //bağlamak istediğimiz layoutu yazıyoruz, ViewGroup
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {  //kullandığımız layouttaki itemelara ulaşabiliyoruz


        //Görsel için GlideToVectorYou kütüphanesi kullanıyoruz
        GlideToVectorYou
            .init()
            .with(context)
            .withListener(object: GlideToVectorYouListener
            {
                override fun onLoadFailed() {}

                override fun onResourceReady() {}
            })
            .setPlaceHolder(R.mipmap.ic_launcher, R.mipmap.ic_launcher)
            .load(Uri.parse(countryModelList[position].getFlag()), holder.flag)

        holder.name.text = countryModelList[position].getName()
        holder.layoutClick.setOnClickListener {
            countryImg.setImageDrawable(holder.flag.drawable)
            txtname.text = countryModelList[position].getName()
            countryDialog.dismiss()
        }
    }

    override fun getItemCount(): Int
    {  // kaç tane row oluşturacağını söylüyoruz
        return countryModelList.size
    }

}