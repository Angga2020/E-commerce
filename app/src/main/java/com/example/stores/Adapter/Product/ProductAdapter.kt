package com.example.stores.Adapter.Product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.utils.GlideApp
import com.example.stores.Model.Product.Data
import com.example.stores.R
import com.example.stores.UI.Products.onProductsItemClickListener
import kotlinx.android.synthetic.main.product_list.view.*

class ProductAdapter(val products: ArrayList<Data>, var clickListner: onProductsItemClickListener) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun getItemCount(): Int = products.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.product_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(products.get(position), clickListner)
    }

    fun refreshAdapter(productList: ArrayList<Data>) {
        this.products.addAll(productList)
        notifyItemRangeChanged(0, this.products.size)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var view: View = itemView
        private lateinit var product: Data

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            Toast.makeText(view.context, "${product.attributes.name} Diklik", Toast.LENGTH_SHORT)
                .show()
        }

        fun bindData(product: Data, action: onProductsItemClickListener) {
            this.product = product
            if (product.attributes.images?.size > 0) {
                GlideApp.with(view.context)
                    .load(product.attributes.images!!.first().imageUrl)
                    .placeholder(R.drawable.noimage)
//                    .circleCrop()
                    .into(view.ivProductImage)
            } else {
                GlideApp.with(view.context)
                    .load(R.drawable.noimage)
//                    .circleCrop()
                    .into(view.ivProductImage)
            }
            view.tvProductName.setText(product.attributes.name)
            view.tvProductPrice.setText(product.attributes.price.toString())
            itemView.setOnClickListener {
                action.onItemClick(product, adapterPosition)
            }
        }
    }
}






