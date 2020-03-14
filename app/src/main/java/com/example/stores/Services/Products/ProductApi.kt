package com.example.stores.Services.Products

import android.widget.ImageView
import com.example.stores.Model.Product.Data

import com.example.stores.Model.Product.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ProductApi {
    // GET /Product
    @GET("api/stores/{id}/products")
    fun getProducts(@Path("id") id:Int,
                    @Query("page") page: Int): Call<Product>

    // GET /Product/{id}
    @GET("api/products/{id}")
    fun getProduct(@Path("id") id:Int): Call<Data>

    // POST /Product
//    @FormUrlEncoded
    @Multipart
    @POST("api/stores/{id}/products")
    fun addProduct(
        @Path("id") id:Int,
        @Part("product[name]") name: RequestBody?,
        @Part("product[price]") price: RequestBody?,
        @Part("product[instock]") inStock: RequestBody?,
        @Part("product[description]") description: RequestBody?,
        @Part("product[color]") color: RequestBody?,
        @Part("product[size]") size: RequestBody?,
        @Part image: ArrayList<MultipartBody.Part>
    ): Call<Data>


    // PATCH /students
    @FormUrlEncoded
//    @Multipart
    @PATCH("api/products/{id}")
    fun updateProduct(
        @Path("id") id:Int,
        @Field("product[name]") name: String?,
        @Field("product[price]") price: Int?,
        @Field("product[instock]") inStock: Int?,
        @Field("product[description]") description: String?,
        @Field("product[color]") color: String?,
        @Field("product[size]") size: String?,
        @Field("product[image][]") image: ByteArray? = null
    ): Call<Data>

    // DELETE /Product/{id}
    @DELETE("api/products/{id}")
    fun deleteProduct(@Path("id") id:Int): Call<Product>
}