package com.example.stores.UI.Stores

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.vvalidator.form
import com.bumptech.glide.Glide
import com.example.myapplication.Services.ApiClient
import com.example.myapplication.Services.StoreApi
import com.example.myapplication.utils.GlideApp
import com.example.stores.MainActivity
import com.example.stores.Model.Store.Data
import com.example.stores.R
import com.example.stores.utils.getMimeType
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.update_store_activity.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateStore : AppCompatActivity() {

    var logoImagePathUpdate : String? = String()
    var coverImagePathUpdate : String? = String()
    val apiInterface: StoreApi = ApiClient.getClient().create(StoreApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_store_activity)

        // manampilkan tombol back pada action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val store : Data = intent.getParcelableExtra("store")

        Glide.with(this)
            .load(store.attributes.logoUrl)
            .placeholder(R.drawable.no_image_avaible)
//                    .circleCrop()
            .into(ibLogoUpdate)
        Glide.with(this)
            .load(store.attributes.coverUrl)
            .placeholder(R.drawable.noimage)
//                    .circleCrop()
            .into(ibCoverUpdate)

        etstorenameUpdate.setText(store.attributes.name)
        etstoretaglineUpdate.setText(store.attributes.tagline)
        etstoredescUpdate.setText(store.attributes.description)
        etstoreaddressUpdate.setText(store.attributes.street)
        etstorezipcodeUpdate.setText(store.attributes.zipcode)
        etstorecityUpdate.setText(store.attributes.city)
        etstorestateUpdate.setText(store.attributes.state)
        etstorecountryUpdate.setText(store.attributes.country)
        etstorephoneUpdate.setText(store.attributes.phone)


        form {
            input(etstorenameUpdate) {
                isNotEmpty()
            }
            input(etstoretaglineUpdate) {
                isNotEmpty()
            }
            input(etstoredescUpdate) {
                isNotEmpty()
            }
            input(etstoreaddressUpdate) {
                isNotEmpty()
            }
            input(etstorezipcodeUpdate) {
                isNotEmpty()
            }
            input(etstorecityUpdate) {
                isNotEmpty()
            }
            input(etstorestateUpdate) {
                isNotEmpty()
            }
            input(etstorecountryUpdate) {
                isNotEmpty()
            }
            input(etstorephoneUpdate) {
                isNotEmpty()
            }

            submitWith(btnSubmitUpdate) { result ->

                val loading = ProgressDialog(this@UpdateStore)
                loading.setMessage(getString(R.string.loading_message))
                loading.show()

                // setup logo image
                val logoFileUpdate: File = File(logoImagePathUpdate)
                var logoMediaTypeUpdate = logoFileUpdate.getMimeType().toMediaType()
                val logoImageBodyUpdate = RequestBody.create(logoMediaTypeUpdate, logoFileUpdate)

                // setup cover image
                val coverFileUpdate: File = File(coverImagePathUpdate)
                var coverMediaTypeUpdate = coverFileUpdate.getMimeType().toMediaType()
                val coverImageBodyUpdate = RequestBody.create(coverMediaTypeUpdate, coverFileUpdate)

                // Create MultipartBody.Part using file request-body,file name and part name
                val logoImagePartUpdate : MultipartBody.Part = MultipartBody.Part.createFormData("store[logo]", logoFileUpdate.getName(), logoImageBodyUpdate)
                val coverImagePartUpdate : MultipartBody.Part = MultipartBody.Part.createFormData("store[banner]", coverFileUpdate.getName(), coverImageBodyUpdate)


                val storeName = etstorenameUpdate.text.toString()
                val storeTagLine = etstoretaglineUpdate.text.toString()
                val storeDesc = etstoredescUpdate.text.toString()
                val storeStreet = etstoreaddressUpdate?.text.toString()
                val storeZipcode = etstorezipcodeUpdate.text.toString()
                val storeCity = etstorecityUpdate.text.toString()
                val storeState = etstorestateUpdate?.text.toString()
                val storeCountry = etstorecountryUpdate.text.toString()
                val storePhone = etstorephoneUpdate.text.toString()


                val storeMediaTypeUpdate = "text/plain".toMediaType()
                val Name = RequestBody.create(storeMediaTypeUpdate,storeName)
                val TagLine  = RequestBody.create(storeMediaTypeUpdate, storeTagLine)
                val Desc = RequestBody.create(storeMediaTypeUpdate, storeDesc)
                val Street = RequestBody.create(storeMediaTypeUpdate, storeStreet)
                val Zipcode = RequestBody.create(storeMediaTypeUpdate, storeZipcode)
                val City = RequestBody.create(storeMediaTypeUpdate, storeCity)
                val State = RequestBody.create(storeMediaTypeUpdate, storeState)
                val Country = RequestBody.create(storeMediaTypeUpdate, storeCountry)
                val Phone = RequestBody.create(storeMediaTypeUpdate, storePhone)

                apiInterface.updateStore(store.attributes.id,
                        Name,
                        TagLine,
                        Desc,
                        Street,
                        Zipcode,
                        City,
                        State,
                        Country,
                        Phone,
                        logoImagePartUpdate,
                        coverImagePartUpdate)
                    .enqueue(object : Callback<Data> {
                        override fun onResponse(call: Call<Data>, response: Response<Data>) {
                            println("TAG_: ${response.body()}")
                            val data = response.body()
                            if (response.isSuccessful) {
                                Toast.makeText(this@UpdateStore, R.string.product_updated, Toast.LENGTH_LONG).show()
                                val intent = Intent(this@UpdateStore, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@UpdateStore, response.message(), Toast.LENGTH_LONG).show()
                            }
                            // dismiss progress dialog
                            loading.dismiss()
                        }
                        override fun onFailure(call: Call<Data>, t: Throwable) = t.printStackTrace()
                    })
            }
        }
        ibCoverUpdate.setOnClickListener {
            //check runtime permission
            ImagePicker.with(this)
                .crop(1f, 1f)               //Crop Square image(Optional)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .start { resultCode, data ->
                    if (resultCode == Activity.RESULT_OK) {
                        //Image Uri will not be null for RESULT_OK
                        val fileUri = data?.data
                        ibCoverUpdate.setImageURI(fileUri)
                        coverImagePathUpdate = fileUri?.path

                        //You can get File object from intent
                    } else if (resultCode == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, R.string.message_canceled, Toast.LENGTH_SHORT).show()
                    }
                }

        }
        ibLogoUpdate.setOnClickListener {
            //check runtime permission
            ImagePicker.with(this)
                .crop(1f, 1f)               //Crop Square image(Optional)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .start { resultCode, data ->
                    if (resultCode == Activity.RESULT_OK) {
                        //Image Uri will not be null for RESULT_OK
                        val fileUri = data?.data
                        ibLogoUpdate.setImageURI(fileUri)
                        logoImagePathUpdate = fileUri?.path

                        //You can get File object from intent
                    } else if (resultCode == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, R.string.message_canceled, Toast.LENGTH_SHORT).show()
                    }
                }
        }
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



