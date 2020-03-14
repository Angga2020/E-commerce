package com.example.stores.UI.Products

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.vvalidator.form
import com.bumptech.glide.request.RequestOptions
import com.coursion.freakycoder.mediapicker.galleries.Gallery
import com.example.myapplication.Services.ApiClient
import com.example.stores.MainActivityProduct
import com.example.stores.Model.Product.Data
import com.example.stores.Model.Product.Product
import com.example.stores.R
import com.example.stores.Services.Products.ProductApi
import com.example.stores.utils.getMimeType
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.animations.DescriptionAnimation
import com.glide.slider.library.slidertypes.BaseSliderView
import com.glide.slider.library.slidertypes.DefaultSliderView
import com.glide.slider.library.tricks.ViewPagerEx
import kotlinx.android.synthetic.main.add_product.*
import kotlinx.android.synthetic.main.detail_product.*
import kotlinx.android.synthetic.main.edit_product.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class EditProduct : AppCompatActivity(), BaseSliderView.OnSliderClickListener,
    ViewPagerEx.OnPageChangeListener {

    val images: ArrayList<MultipartBody.Part> = ArrayList()
    private val OPEN_MEDIA_PICKER = 1  // Request code
//    lateinit var imageUri : Uri
//    var imageData: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_product)
        //actionbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var filePath: String? = String()

        val apiInterface: ProductApi = ApiClient.getClient().create(ProductApi::class.java)

        val product: Data = intent.getParcelableExtra("product")

        // Displaying images
        val requestOptions = RequestOptions()
        requestOptions.centerCrop()
//        .diskCacheStrategy(DiskCacheStrategy.NONE)
//        .placeholder(R.drawable.placeholder)
//        .error(R.drawable.placeholder)

        for (image in product.attributes.images) {
            val sliderView = DefaultSliderView(this)
            // if you want show image only / without description text use DefaultSliderView instead
            // initialize SliderLayout
            sliderView
                .image(image.imageUrl)
                .description(image.fileName)
                .setRequestOption(requestOptions)
                .setProgressBarVisible(true)
//                .setOnSliderClickListener(this@EditProduct)
            //add your extra information
            sliderView.bundle(Bundle())
            sliderView.bundle.putString("extra", image.fileName)
            ivEditProductImage.addSlider(sliderView)
        }

        // set Slider Transition Animation
        ivEditProductImage.setPresetTransformer(SliderLayout.Transformer.Default)
//        ivDetailProductImage.setPresetTransformer(SliderLayout.Transformer.Accordion)

        ivEditProductImage.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        ivEditProductImage.setCustomAnimation(DescriptionAnimation())
        ivEditProductImage.setDuration(5000)
        ivEditProductImage.addOnPageChangeListener(this)
        ivEditProductImage.stopCyclingWhenTouch(false)

        val radioButtonColor = "cb${product.attributes.color}Update"
        var rbColor =
            findViewById<RadioButton>(resources.getIdentifier(radioButtonColor, "id", packageName))
        rgColorUpdate.check(rbColor.id)

        val radioButtonSize = "cb${product.attributes.size}Update"
        var rbSize = findViewById<RadioButton>(resources.getIdentifier(radioButtonSize, "id", packageName))
        rgSizeUpdate.check(rbSize.id)

        etProductNameUpdate.setText(product.attributes.name)
        etDescProductUpdate.setText(product.attributes.description)
        etPriceUpdate.setText(product.attributes.price.toString())
        etStockProductUpdate.setText(product.attributes.inStock.toString())

        btnPickImagesUpdate.setOnClickListener {

            if (EasyPermissions.hasPermissions(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                val intent = Intent(this, Gallery::class.java)
                // Set the title for toolbar
                intent.putExtra("title", "Select media")

                // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
                intent.putExtra("mode", 2)
                intent.putExtra("maxSelection", 5) // Optional
                startActivityForResult(intent, OPEN_MEDIA_PICKER)
            } else {
                // tampilkan permission request saat belum mendapat permission dari user
                EasyPermissions.requestPermissions(
                    this,
                    R.string.permission_confirm.toString(),
                    991,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }

        form {
            input(etProductNameUpdate) {
                isNotEmpty()
            }
            input(etPriceUpdate) {
                isNotEmpty()
            }
            input(etStockProductUpdate) {
                isNotEmpty()
            }
            input(etDescProductUpdate) {
                isNotEmpty()
            }
            submitWith(btnSubmitUpdate) { result ->
                val loading = ProgressDialog(this@EditProduct)
                loading.setMessage(getString(R.string.loading_message))
                loading.show()

                val file: File = File(filePath)
                var mediaType = file.getMimeType().toMediaType()
                val imageFile = RequestBody.create(mediaType, file)

                // Create MultipartBody.Part using file request-body,file name and part name
                val part: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "product[images][]",
                    file.getName(),
                    imageFile
                )

                val productNameUpdate = etProductNameUpdate.text.toString()
                val productPriceUpdate = etPriceUpdate.text.toString().toInt()
                val productStockUpdate = etStockProductUpdate.text.toString().toInt()
                val productDescriptionUpdate = etDescProductUpdate.text.toString()

                val checkedRbColor: Int = rgColorUpdate.checkedRadioButtonId
                val radioColor: RadioButton = findViewById(checkedRbColor)
                val productColorUpdate: String = radioColor.text.toString()

                val checkedRbSize: Int = rgSizeUpdate.checkedRadioButtonId
                val radioSize: RadioButton = findViewById(checkedRbSize)
                val productSizeUpdate: String = radioSize.text.toString()

                apiInterface.updateProduct(
                        product.attributes.id,
                        productNameUpdate,
                        productPriceUpdate,
                        productStockUpdate,
                        productDescriptionUpdate,
                        productColorUpdate,
                        productSizeUpdate
                    )
                    .enqueue(object : Callback<Data> {
                        override fun onResponse(call: Call<Data>, response: Response<Data>) {
                            println(response.body())
                            val data = response.body()
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@EditProduct,
                                    R.string.product_created,
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent =
                                    Intent(this@EditProduct, MainActivityProduct::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@EditProduct,
                                    response.message(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            loading.dismiss()
                        }

                        override fun onFailure(call: Call<Data>, t: Throwable) = t.printStackTrace()
                    })
            }
        }
    }
    override fun onStop() { // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        ivEditProductImage.stopAutoCycle()
        super.onStop()
    }

    override fun onSliderClick(slider: BaseSliderView) {
        Toast.makeText(this, slider.bundle.getString("extra") + "", Toast.LENGTH_SHORT).show()
    }

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {}

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
                        var mediaType = file.getMimeType().toMediaType()
                        val fileReqBody = RequestBody.create(mediaType, file)
                        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("product[images][]", file.name, fileReqBody)
                        images.add(filePart)

                        val imageView = ImageView(this)
                        imageView.layoutParams = LinearLayout.LayoutParams(250, 250) // value is in pixels

                        imageView.setImageBitmap(bm)

                        // Add ImageView to LinearLayout
                        ivEditProductImage?.addView(imageView)
                    } catch (exp: IOException) {
                        exp.printStackTrace()
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(applicationContext, getString(R.string.canceled_task), Toast.LENGTH_LONG).show()
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

