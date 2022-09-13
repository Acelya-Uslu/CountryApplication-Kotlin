package com.acelyauslu.countryapplication.model


class CountryModel(private var name:String ,private var flag:String)
{
    fun getName(): String
    {
        return name
    }

    fun getFlag(): String
    {
        return flag
    }
}