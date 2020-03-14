package com.example.stores.Model.Store

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Store(val data: ArrayList<Data>, val meta: Meta): Parcelable

@Parcelize
data class Data(val id: String,
                val type: String,
                val attributes: Attribute//,
//                val relationships: Relationships
                ): Parcelable

@Parcelize
data class Attribute(val id: Int,
                     val name : String,
                     val tagline : String,
                     val description : String,
                     val street : String?,
                     val zipcode : String,
                     val city : String,
                     val state : String?,
                     val country : String,
                     val phone : String,
                     val latitude : Double,
                     val longitude : Double,
                     val createdAt : String,
                     val updatedAt : String,
                     val logoUrl : String?,
                     val coverUrl : String?): Parcelable

//@Parcelize
//data class Relationships(val products : ArrayList<Data>): Parcelable

@Parcelize
data class Meta(val pagination: Pagination): Parcelable

@Parcelize
data class Pagination(val currentPage: Int,
                      val prevPage: Int,
                      val nextPage: Int,
                      val totalPage: Int): Parcelable