package com.example.myapplication.Services

import com.example.stores.Model.Store.Data
import com.example.stores.Model.Store.Store
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface StoreApi {
    // GET /Store
    @GET("api/stores")
    fun getStores(): Call<Store>

    @Multipart
    @POST("api/stores")
    fun addStore(@Part("store[name]") name: RequestBody?,
                @Part("store[tagline]") tagLine: RequestBody?,
                @Part("store[description]") description: RequestBody?,
                @Part("store[address]") address: RequestBody?,
                @Part("store[zipcode]") zipCode: RequestBody?,
                @Part("store[city]") city:RequestBody?,
                @Part("store[state]") state:RequestBody?,
                @Part("store[country]") country:RequestBody?,
                @Part("store[phone]") phone:RequestBody?,
                @Part logoImage : MultipartBody.Part?,
                @Part coverImage : MultipartBody.Part?
                ): Call<Data>
//
//
//    // PATCH /students
//    @FormUrlEncoded
    @Multipart
    @PATCH("api/stores/{id}")
    fun updateStore(
//        @Query("page") page: Int,
        @Path("id") id:Int,
        @Part("store[name]") name: RequestBody?,
        @Part("store[tagline]") tagLine: RequestBody?,
        @Part("store[description]") description: RequestBody?,
        @Part("store[address]") address: RequestBody?,
        @Part("store[zipcode]") zipCode: RequestBody?,
        @Part("store[city]") city:RequestBody?,
        @Part("store[state]") state:RequestBody?,
        @Part("store[country]") country:RequestBody?,
        @Part("store[phone]") phone:RequestBody?,
        @Part logoImage : MultipartBody.Part?,
        @Part coverImage : MultipartBody.Part?
    ): Call<Data>

    // DELETE /Product/{id}
    @DELETE("api/stores/{id}")
    fun deleteStore(@Path("id") id:Int): Call<Store>
}