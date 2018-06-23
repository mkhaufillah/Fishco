/*
 * Created by mkhaufillah on 6/14/18 9:29 PM
 * Copyright (c) 2018. All rights reserved.
 * Last modified 6/14/18 9:29 PM
 */

package id.fishco.fishco.adapter.helper

import id.fishco.fishco.model.rajaongkirApi.BaseRo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitApi {

    @FormUrlEncoded
    @POST("cost")
    fun getCost(
            @Field("origin") origin: String,
            @Field("destination") destination: String,
            @Field("weight") weight: String,
            @Field("courier") courier: String,
            @Header("key") key: String
    ): Call<BaseRo>

    @GET("province")
    fun getProvince(
            @Header("key") key: String
    ): Call<BaseRo>

    @GET("city")
    fun getCity(
            @Query("province") province: String,
            @Header("key") key: String
    ): Call<BaseRo>

}