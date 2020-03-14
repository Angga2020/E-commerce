package com.example.stores.UI.Products

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.vvalidator.form
import com.coursion.freakycoder.mediapicker.galleries.Gallery
import com.example.myapplication.Services.ApiClient
import com.example.stores.MainActivityProduct
import com.example.stores.Model.Product.Data
import com.example.stores.R
import com.example.stores.Services.Products.ProductApi
import com.example.stores.utils.getMimeType
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_product.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException

class AddProduct : AppCompatActivity() {

    private val OPEN_MEDIA_PICKER = 1  // Request code

    val images: ArrayList<MultipartBody.Part> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product)
        // manampilkan tombol back pada action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var filePath:String? = String()

        val apiInterface : ProductApi = ApiClient.getClient().create(ProductApi::class.java)

        btnPickImages.setOnClickListener {
            if(EasyPermissions.hasPermissions(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                val intent = Intent(this, Gallery::class.java)
                // Set the title for toolbar
                intent.putExtra("title", "Select media")

                // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
                intent.putExtra("mode", 2)
                intent.putExtra("maxSelection", 5) // Optional
                startActivityForResult(intent, OPEN_MEDIA_PICKER)
            }
            else {
                // tampilkan permission request saat belum mendapat permission dari user
                EasyPermissions.requestPermissions(this,"This application need your permission to access photo gallery.",991,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            setVisible(false)
        }


//        btnSubmit.setOnClickListener {
        form {
            input(etproductname) {
                isNotEmpty()
            }
            input(etprice) {
                isNotEmpty()
            }
            input(etStockProduct) {
                isNotEmpty()
            }
            input(etDescProduct) {
                isNotEmpty()
            }

            submitWith(btnSubmit) { result ->
                val loading = ProgressDialog(this@AddProduct)
                loading.setMessage(getString(R.string.loading_message))
                loading.show()

                //upload image
                val file: File = File(filePath)
                var mediaType = file.getMimeType().toMediaType()
                val imageFile = RequestBody.create(mediaType, file)

                // Create MultipartBody.Part using file request-body,file name and part name
                val part : MultipartBody.Part = MultipartBody.Part.createFormData("product[images][]", file.getName(), imageFile)

                val productName = etproductname.text.toString()
                val productPrice = etprice.text.toString()
                val productStock = etStockProduct.text.toString()
                val productDescription = etDescProduct.text.toString()

                val checkedRbColor: Int = rgColor.checkedRadioButtonId
                val radioColor: RadioButton = findViewById(checkedRbColor)
                val productColor: String = radioColor.text.toString()

                val checkedRbSize: Int = rgSize.checkedRadioButtonId
                val radioSize: RadioButton = findViewById(checkedRbSize)
                val productSize: String = radioSize.text.toString()

                mediaType = "text/plain".toMediaType()
                val name: RequestBody = RequestBody.create(mediaType,productName )
                val description: RequestBody = RequestBody.create(mediaType, productDescription)
                val color: RequestBody = RequestBody.create(mediaType, productColor)
                val size: RequestBody = RequestBody.create(mediaType, productSize)
                val instock: RequestBody = RequestBody.create(mediaType, productStock)
                val price: RequestBody = RequestBody.create(mediaType, productPrice)

                var storeId = intent.getStringExtra("storeId")

                apiInterface.addProduct(storeId.toInt(),
                        name,
                        price,
                        instock,
                        description,
                        color,
                        size,
                        images)
                    .enqueue(object : Callback<Data> {
                        override fun onResponse(call: Call<Data>, response: Response<Data>) {
                            println("TAG_: ${response.body()}")
                            val data = response.body()
                            if (response.isSuccessful) {
                                Toast.makeText(this@AddProduct, "Product was successfully created", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@AddProduct, MainActivityProduct::class.java)
                                startActivity(intent)

                            } else {
                                Toast.makeText(this@AddProduct, response.message(), Toast.LENGTH_LONG).show()
                            }
                            loading.dismiss()
                        }
                        override fun onFailure(call: Call<Data>, t: Throwable) = t.printStackTrace()
                    })
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_MEDIA_PICKER) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val selectionResult = data.getStringArrayListExtra("result")
                selectionResult.forEach {
                    try {
                        Log.d("MyApp", "Image Path : " + it)
                        val uriFromPath = Uri.fromFile(File(it))
                        Log.d("MyApp", "Image URI : " + uriFromPath)
                        // Convert URI to Bitmap
                        val bm = BitmapFactory.decodeStream(
                            contentResolver.openInputStream(uriFromPath))

                        val file = File(it)
                        var mediaType = "image/png".toMediaType()
                        val fileReqBody = RequestBody.create(mediaType, file)
                        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("product[images][]", file.name, fileReqBody)

                        images.add(filePart)

                        val imageView = ImageView(this)
                        imageView.layoutParams = LinearLayout.LayoutParams(250, 250) // value is in pixels

                        imageView.setImageBitmap(bm)

                        // Add ImageView to LinearLayout
                        LlAdd?.addView(imageView)

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
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
