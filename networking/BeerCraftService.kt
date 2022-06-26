package com.nereus.craftbeer.networking

import com.android.example.github.util.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object BeerCraftService {

    private const val BASE_URL = "http://alb-dev-apne1-cfbeer-01-87699778.ap-northeast-1.elb.amazonaws.com"

    fun create(): BeerCraftApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(BeerCraftApi::class.java)
    }
}