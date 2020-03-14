package com.example.stores.UI.Stores

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.Services.ApiClient
import com.example.myapplication.Services.StoreApi
import com.example.stores.MainActivity
import com.example.stores.MainActivityProduct
import com.example.stores.Model.Store.Data
import com.example.stores.Model.Store.Store
import com.example.stores.R
import com.example.stores.UI.Products.DetailProduct
import com.example.stores.UI.Products.EditProduct
import kotlinx.android.synthetic.main.detail_store.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStore : AppCompatActivity() {

    internal lateinit var myDialog: Dialog
    internal lateinit var txt : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_store)
        // manampilkan tombol back pada action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val store : Data = intent.getParcelableExtra("store")

        btnShowProducts.setOnClickListener {
            var intent = Intent(this, MainActivityProduct::class.java)
            intent.putExtra("storeId", store.attributes.id.toString())
            startActivity(intent)
        }

        btnUpdate.setOnClickListener {
            val intent = Intent(this, UpdateStore::class.java)
            intent.putExtra("store", store)
            startActivity(intent)
        }
        btnDelete.setOnClickListener{
            ShowDialog()
        }

        Glide.with(this)
            .load(store.attributes.logoUrl)
            .centerCrop()
            .placeholder(R.drawable.noimage)
            .error(R.drawable.broken_image)
//                    .circleCrop()
            .into(ivLogo)
        Glide.with(this)
            .load(store.attributes.coverUrl)
            .centerCrop()
            .placeholder(R.drawable.no_image_avaible)
            .error(R.drawable.broken_image)
//                    .circleCrop()
            .into(ivCover)

        tvDetailName.setText(store.attributes.name)
        tvDetailTagLine.setText(store.attributes.tagline)
        tvDetailDesc.setText(store.attributes.description)
        tvDetailStreet.setText(store.attributes.street)
        tvDetailZipCode.setText(store.attributes.zipcode)
        tvDetailCity.setText(store.attributes.city)
    }
    fun ShowDialog(){
        val store : Data = intent.getParcelableExtra("store")
        myDialog = Dialog(this)
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        myDialog.setContentView(R.layout.activity_confirm_delete)
        myDialog.setTitle("My PopUp")

        txt = myDialog.findViewById(R.id.tvbYes) as TextView
        txt.isEnabled = true
        txt.setOnClickListener{
            val loading = ProgressDialog(this)
            loading.setMessage(getString(R.string.loading_message))
            loading.show()
            val apiInterface : StoreApi = ApiClient.getClient().create(StoreApi::class.java)
            apiInterface.deleteStore(store.attributes.id)
                .enqueue(object : Callback<Store> {

                    override fun onResponse(call: Call<Store>, response: Response<Store>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@DetailStore,R.string.product_deleted,
                                Toast.LENGTH_LONG).show()
                            val intent = Intent(this@DetailStore, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(this@DetailStore, response.message(), Toast.LENGTH_LONG).show()
                        }
                        // dismiss progress dialog
                        loading.dismiss()
                    }
                    override fun onFailure(call: Call<Store>, t: Throwable) = t.printStackTrace()

                })
            myDialog.dismiss()

        }

        txt = myDialog.findViewById(R.id.tvbCancel) as TextView
        txt.isEnabled = true
        txt.setOnClickListener{
            myDialog.cancel()
        }
        myDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                this.finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}