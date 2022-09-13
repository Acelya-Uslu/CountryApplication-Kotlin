package com.acelyauslu.countryapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.acelyauslu.countryapplication.R
import com.acelyauslu.countryapplication.model.CountryModel
import java.util.ArrayList

class PopulationAdapter(private var countryModelList: ArrayList<CountryModel>): RecyclerView.Adapter<PopulationAdapter.ViewHolder>()
{

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView)
    {
        val yearTxt = itemView.findViewById<TextView>(R.id.yearTxt)
        val populationTxt = itemView.findViewById<TextView>(R.id.populationTxt)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        //LayoutInflater
        val view: View =
            LayoutInflater.from(parent.context)
            .inflate(R.layout.population_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {//kaç row oluşturucağımızı söylüyoruz
        return countryModelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.yearTxt.text = countryModelList[position].getName()
        holder.populationTxt.text = countryModelList[position].getFlag()
    }
}