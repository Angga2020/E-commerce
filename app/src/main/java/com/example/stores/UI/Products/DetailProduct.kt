package com.example.stores.UI.Products

import com.example.stores.MainActivityProduct
import com.example.stores.Model.Product.Data
import com.example.stores.Model.Product.Product
import com.example.stores.R
import com.example.stores.Services.Products.ProductApi
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.Services.ApiClient
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.animations.DescriptionAnimation
import com.glide.slider.library.slidertypes.BaseSliderView
import com.glide.slider.library.slidertypes.BaseSliderView.OnSliderClickListener
import com.glide.slider.library.slidertypes.TextSliderView
import com.glide.slider.library.tricks.ViewPagerEx
import kotlinx.android.synthetic.main.detail_product.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailProduct : AppCompatActivity(), OnSliderClickListener,
    ViewPagerEx.OnPageChangeListener{

    //    val images: ArrayList<MultipartBody.Part> = ArrayList()
    internal lateinit var myDialog: Dialog
    internal lateinit var txt : TextView
    var filePath:String? = String()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_product)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


//        val actionbar = supportActionBar
//        actionbar!!.title = "Detail Product"

        val product : Data = intent.getParcelableExtra("product")

        // Displaying images
        val requestOptions = RequestOptions()
        requestOptions.centerCrop()
//        .diskCacheStrategy(DiskCacheStrategy.NONE)
//        .placeholder(R.drawable.placeholder)
//        .error(R.drawable.placeholder)

        for (image in product.attributes.images) {
            val sliderView = TextSliderView(this)
            // if you want show image only / without description text use DefaultSliderView instead
            // initialize SliderLayout
            sliderView
                .image(image.imageUrl)
                .description(image.fileName)
                .setRequestOption(requestOptions)
                .setProgressBarVisible(true)
                .setOnSliderClickListener(this)
            //add your extra information
            sliderView.bundle(Bundle())
            sliderView.bundle.putString("extra", image.fileName)
            ivDetailProductImage.addSlider(sliderView)
        }

        // set Slider Transition Animation
        ivDetailProductImage.setPresetTransformer(SliderLayout.Transformer.Default)
//        ivDetailProductImage.setPresetTransformer(SliderLayout.Transformer.Accordion)

        ivDetailProductImage.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        ivDetailProductImage.setCustomAnimation(DescriptionAnimation())
        ivDetailProductImage.setDuration(5000)
        ivDetailProductImage.addOnPageChangeListener(this)
        ivDetailProductImage.stopCyclingWhenTouch(false)

        tvDetailProductName.setText(product.attributes.name)
        tvDetailProductDesc.setText(product.attributes.description)
        tvDetailProductColor.setText(product.attributes.color)
        tvDetailProductSize.setText(product.attributes.size)
        tvDetailProductPrice.setText(product.attributes.price.toString())
        tvDetailProductStock.setText(product.attributes.inStock.toString())

        updateBtn.setOnClickListener {
            val intent = Intent(this, EditProduct::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            ShowDialog()
        }
    }

    override fun onStop() { // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        ivDetailProductImage.stopAutoCycle()
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

    fun ShowDialog(){
        val product : Data = intent.getParcelableExtra("product")
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
            val apiInterface : ProductApi = ApiClient.getClient().create(ProductApi::class.java)
            apiInterface.deleteProduct(product.attributes.id)
                .enqueue(object : Callback<Product> {

                    override fun onResponse(call: Call<Product>, response: Response<Product>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@DetailProduct,"Product was successfully deleted",Toast.LENGTH_LONG).show()
                            var intent = Intent(this@DetailProduct, MainActivityProduct::class.java)
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(this@DetailProduct, response.message(),Toast.LENGTH_LONG).show()
                        }
                        loading.dismiss()
                    }
                    override fun onFailure(call: Call<Product>, t: Throwable) = t.printStackTrace()

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