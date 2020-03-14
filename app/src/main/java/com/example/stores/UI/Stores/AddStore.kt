package com.example.stores.UI.Stores

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.vvalidator.form
import com.example.myapplication.Services.ApiClient
import com.example.myapplication.Services.StoreApi
import com.example.stores.MainActivity
import com.example.stores.Model.Store.Data
import com.example.stores.R
import com.example.stores.utils.getMimeType
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.add_store.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStore :AppCompatActivity() {

    var logoImagePath : String? = String()
    var coverImagePath : String? = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_store)
        // manampilkan tombol back pada action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val apiInterface: StoreApi = ApiClient.getClient().create(StoreApi::class.java)

        form {
            input(etstorename) {
                isNotEmpty()
            }
            input(etstoretagline) {
                isNotEmpty()
            }
            input(etstoredesc) {
                isNotEmpty()
            }
            input(etstoreaddress) {
                isNotEmpty()
            }
            input(etstorezipcode) {
                isNotEmpty()
            }
            input(etstorecity) {
                isNotEmpty()
            }
            input(etstorestate) {
                isNotEmpty()
            }
            input(etstorecountry) {
                isNotEmpty()
            }
            input(etstorephone) {
                isNotEmpty()
            }

            submitWith(btnSubmit) { result ->
                val loading = ProgressDialog(this@AddStore)
                loading.setMessage(getString(R.string.loading_message))
                loading.show()

                // setup logo image
                val logoFile: File = File(logoImagePath)
                var logoMediaType = logoFile.getMimeType().toMediaType()
                val logoImageBody = RequestBody.create(logoMediaType, logoFile)

                // setup cover image
                val coverFile: File = File(coverImagePath)
                var coverMediaType = coverFile.getMimeType().toMediaType()
                val coverImageBody = RequestBody.create(coverMediaType, coverFile)

                 // Create MultipartBody.Part using file request-body,file name and part name
                val logoImagePart : MultipartBody.Part = MultipartBody.Part.createFormData("store[logo]", logoFile.getName(), logoImageBody)
                val coverImagePart : MultipartBody.Part = MultipartBody.Part.createFormData("store[banner]", coverFile.getName(), coverImageBody)

                val storeName = etstorename.text.toString()
                val storeTagLine = etstoretagline.text.toString()
                val storeDesc = etstoredesc.text.toString()
                val storeStreet = etstoreaddress?.text.toString()
                val storeZipcode = etstorezipcode.text.toString()
                val storeCity = etstorecity.text.toString()
                val storeState = etstorestate?.text.toString()
                val storeCountry = etstorecountry.text.toString()
                val storePhone = etstorephone.text.toString()

                val storeMediaType = "text/plain".toMediaType()
                val name: RequestBody = RequestBody.create(storeMediaType,storeName )
                val tagLine: RequestBody = RequestBody.create(storeMediaType, storeTagLine)
                val desc: RequestBody = RequestBody.create(storeMediaType, storeDesc)
                val address: RequestBody = RequestBody.create(storeMediaType, storeStreet)
                val zipCode: RequestBody = RequestBody.create(storeMediaType, storeZipcode)
                val city: RequestBody = RequestBody.create(storeMediaType, storeCity)
                val state: RequestBody = RequestBody.create(storeMediaType, storeState)
                val country: RequestBody = RequestBody.create(storeMediaType, storeCountry)
                val phone: RequestBody = RequestBody.create(storeMediaType, storePhone)

                apiInterface.addStore(name,
                                    tagLine,
                                    desc,
                                    address,
                                    zipCode,
                                    city,
                                    state,
                                    country,
                                    phone,
                                    logoImagePart,
                                    coverImagePart)
                    .enqueue(object : Callback<Data> {
                        override fun onResponse(call: Call<Data>, response: Response<Data>) {
                            println("TAG_: ${response.body()}")
                            val data = response.body()
                            if (response.isSuccessful) {
                                val intent = Intent(this@AddStore, MainActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(this@AddStore, R.string.product_created, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this@AddStore, response.message(), Toast.LENGTH_LONG).show()
                            }
                            // dismiss progress dialog
                            loading.dismiss()
                        }
                        override fun onFailure(call: Call<Data>, t: Throwable) = t.printStackTrace()
                    })
            }
        }

        ibCoverAdd.setOnClickListener {
            //check runtime permission
            ImagePicker.with(this)
                .crop(1f, 1f)               //Crop Square image(Optional)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .start { resultCode, data ->
                    if (resultCode == Activity.RESULT_OK) {
                        //Image Uri will not be null for RESULT_OK
                        val fileUri = data?.data
                        ibCoverAdd.setImageURI(fileUri)
                        coverImagePath = fileUri?.path

                        //You can get File object from intent
                    } else if (resultCode == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, R.string.message_canceled, Toast.LENGTH_SHORT).show()
                    }
                }

        }

        ibLogoAdd.setOnClickListener {
            //check runtime permission
            ImagePicker.with(this)
                .crop(1f, 1f)               //Crop Square image(Optional)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .start { resultCode, data ->
                    if (resultCode == Activity.RESULT_OK) {
                        //Image Uri will not be null for RESULT_OK
                        val fileUri = data?.data
                        ibLogoAdd.setImageURI(fileUri)
                        logoImagePath = fileUri?.path

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
