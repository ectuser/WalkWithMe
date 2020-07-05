package com.example.walkwithme.retrofit.build_route

import retrofit2.Call
import retrofit2.http.GET

interface Endpoints {

    @GET("/api/helloworld")
    fun getTest(): Call<ArrayList<String>>

}