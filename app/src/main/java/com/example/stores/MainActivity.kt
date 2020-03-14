package com.example.stores

import android.content.Intent
import android.graphics.Color
import com.example.stores.Model.Store.Data
import com.example.stores.Model.Store.Store
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Services.ApiClient
import com.example.myapplication.Services.StoreApi
import com.example.stores.Adapter.Stores.AdapterStore
import com.example.stores.UI.Stores.AddStore
import com.example.stores.UI.Stores.DetailStore
import com.example.stores.UI.Stores.onStoresItemClickListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(),
    onStoresItemClickListener {

    private val stores : ArrayList<Data> = ArrayList()
//    private val product : Data = com.example.stores.Model.Product.Data

    private val TAG = javaClass.simpleName
    private var adapterStore by Delegates.notNull<AdapterStore>()
    private var isLoading by Delegates.notNull<Boolean>()
    private var page by Delegates.notNull<Int>()
    private var totalPage by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        page = 1
        totalPage = 0

        fab.setOnClickListener {
            val intent = Intent(this, AddStore::class.java)
            startActivity(intent)
        }

        rvStores.layoutManager = GridLayoutManager(this, 1 ) as RecyclerView.LayoutManager?
        setUpRecyleView()
        initListener()

        //** Set the colors of the Pull To Refresh View
        swipeContainer.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorPrimary))
        swipeContainer.setColorSchemeColors(Color.WHITE)

        swipeContainer.setOnRefreshListener {
            stores.clear()
            setUpRecyleView()
            swipeContainer.isRefreshing = false
        }
    }
    fun setUpRecyleView() {
        Log.d(TAG, "page: $page")
        showLoading(true)

        val apiInterface : StoreApi = ApiClient.getClient().create(StoreApi::class.java)

        apiInterface.getStores()
            .enqueue(object : Callback<Store> {
                override fun onResponse(call: Call<Store>, response: Response<Store>) {
                    response.body()?.data?.forEach {
                        stores.add(it)
                    }
                    if (page == 1) {
                        adapterStore =
                            AdapterStore(stores,/*product,*/ this@MainActivity )
                        rvStores.adapter = adapterStore
                    } else {
                        adapterStore.refreshAdapter(stores)
                    }

                    totalPage = response.body()!!.meta.pagination.totalPage
                    hideLoading()
                }

                override fun onFailure(call: Call<Store>, t: Throwable){
                    t.printStackTrace()
                }
            })
    }
    override fun onItemClick(store: Data, position:Int){
//        // Toast.makeText(this, product.attributes.name, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DetailStore::class.java)
        intent.putExtra("store", store)
//        intent.putExtra("product", product)
        startActivity(intent)
    }
    private fun initListener() {
        rvStores.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        rvStores.visibility.let {
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
        rvStores.visibility = View.VISIBLE
    }

}
