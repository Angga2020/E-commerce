package com.example.stores.Model.Product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(val data: ArrayList<Data>, val meta: Meta): Parcelable

@Parcelize
data class Data(val id: String,
                val type: String,
                val attributes: Attribute): Parcelable
@Parcelize
data class Attribute(val id: Int,
                     val name : String,
                     val description : String,
                     val color : String,
                     val size : String,
                     val inStock : String,
                     val price : Int,
                     val createdAt : String,
                     val updatedAt : String,
                     val images: ArrayList<Image>): Parcelable
@Parcelize
data class Image(val fileName: String,
                 val imageUrl: String): Parcelable

@Parcelize
data class Meta(val pagination: Pagination): Parcelable

@Parcelize
data class Pagination(val currentPage: Int,
                      val prevPage: Int,
                      val nextPage: Int,
                      val totalPage: Int): Parcelable
