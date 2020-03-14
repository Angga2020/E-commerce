package com.example.stores

import com.example.stores.Adapter.Product.ProductAdapter
import com.example.stores.Model.Product.Data
import com.example.stores.Model.Product.Product
import com.example.stores.Services.Products.ProductApi
import com.example.stores.UI.Products.onProductsItemClickListener
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.activity_main.progress_bar_horizontal_activity_main
import kotlinx.android.synthetic.main.activity_main.swipeContainer
import kotlinx.android.synthetic.main.activity_main_product.*
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Services.ApiClient
import com.example.stores.UI.Products.AddProduct
import com.example.stores.UI.Products.DetailProduct
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates


class MainActivityProduct : AppCompatActivity(), onProductsItemClickListener {

    private val products : ArrayList<Data> = ArrayList()

    private val TAG = javaClass.simpleName
    private var productAdapter by Delegates.notNull<ProductAdapter>()
    private var isLoading by Delegates.notNull<Boolean>()
    private var page by Delegates.notNull<Int>()
    private var totalPage by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_product)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        var storeId = intent.getStringExtra("storeId")

        page = 1
        totalPage = 0

        fab.setOnClickListener {
            val intent = Intent(this, AddProduct::class.java)
            intent.putExtra("storeId", storeId)
            startActivity(intent)
        }

        rvProducts.layoutManager = GridLayoutManager(this, 2 ) as RecyclerView.LayoutManager?
        setUpRecyleView()
        initListener()

        //** Set the colors of the Pull To Refresh View
        swipeContainer.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorPrimary))
        swipeContainer.setColorSchemeColors(Color.WHITE)

        swipeContainer.setOnRefreshListener {
            products.clear()
            setUpRecyleView()
            swipeContainer.isRefreshing = false
        }

    }
    fun setUpRecyleView() {
        var storeId = intent.getStringExtra("storeId")

        showLoading(true)

        val apiInterface : ProductApi = ApiClient.getClient().create(ProductApi::class.java)

        apiInterface.getProducts(storeId.toInt(), page)
            .enqueue(object : Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    response.body()?.data?.forEach {
                        products.add(it)
                    }
                    if (page == 1) {
                        productAdapter = ProductAdapter(products, this@MainActivityProduct)
                        rvProducts.adapter = productAdapter
                    } else {
                        productAdapter.refreshAdapter(products)
                    }

                    totalPage = response.body()!!.meta.pagination.totalPage
                    hideLoading()
                }

                override fun onFailure(call: Call<Product>, t: Throwable){
                    t.printStackTrace()
                }
            })
    }

    override fun onItemClick(product: Data, position:Int){
        var intent = Intent(this, DetailProduct::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }


    private fun initListener() {
        rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView?.layoutManager as LinearLayoutManager
                val countItem = linearLayoutManager?.itemCount
                val lastVisiblePosition = linearLayoutManager?.findLastCompletelyVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                Log.d(TAG, "countItem: $countItem")
                Log.d(TAG, "lastVisiblePosition: $lastVisiblePosition")
                Log.d(TAG, "isLastPosition: $isLastPosition")
                if (!isLoading && isLastPosition && page < totalPage) {
                    showLoading(true)
                    page = page.let { it.plus(1) }
                    setUpRecyleView()
                }
            }
        })
    }

    private fun showLoading(isRefresh: Boolean) {
        isLoading = true
        progress_bar_horizontal_activity_main.visibility = View.VISIBLE
        rvProducts.visibility.let {
            if (isRefresh) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun hideLoading() {
        isLoading = false
        progress_bar_horizontal_activity_main.visibility = View.GONE
        rvProducts.visibility = View.VISIBLE
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
