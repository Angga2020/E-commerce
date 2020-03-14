package com.example.stores.UI.Products

import com.example.stores.Model.Product.Data

interface onProductsItemClickListener{
    fun onItemClick(product: Data, position: Int)
}