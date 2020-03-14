package com.example.stores.Adapter.Stores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stores.Model.Store.Data
import com.example.stores.R
import com.example.stores.UI.Stores.onButtonClickListener
import com.example.stores.UI.Stores.onStoresItemClickListener
import kotlinx.android.synthetic.main.store_list.view.*

class AdapterStore(val stores: ArrayList<Data>,/*val product: com.example.stores.Model.Product.Data,*/ var clickListner: onStoresItemClickListener /*,var buttonClickListener:onButtonClickListener*/) :
    RecyclerView.Adapter<AdapterStore.ViewHolder>() {
    
    

    override fun getItemCount(): Int = stores.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.store_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(stores.get(position),/*product,*/clickListner/*,buttonClickListener*/)
    }

    fun refreshAdapter(storeList: ArrayList<Data>) {
        this.stores.addAll(storeList)
        notifyItemRangeChanged(0, this.stores.size)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var view: View = itemView
        private lateinit var store: Data

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            Toast.makeText(view.context, "${store.attributes.name}", Toast.LENGTH_SHORT)
                .show()
        }

        fun bindData(store: Data,/*product:com.example.stores.Model.Product.Data,*/ action: onStoresItemClickListener/*, click: onButtonClickListener*/) {
//            var btnShow = R.id.btnShowProducts as Button
            this.store = store
            if (store.attributes.logoUrl != null)
            else null //1
            Glide.with(itemView)  //2
                .load(store.attributes.logoUrl) //3
                .centerCrop() //4
                .placeholder(R.drawable.noimage) //5
                .error(R.drawable.broken_image) //6
                .fallback(R.drawable.noimage) //7
                .into(itemView.ivLogo) //8
            Glide.with(itemView)  //2
                .load(store.attributes.coverUrl) //3
                .centerCrop() //4
                .placeholder(R.drawable.no_image_avaible) //5
                .error(R.drawable.broken_image) //6
                .fallback(R.drawable.no_image_avaible) //7
                .into(itemView.ivCover) //8


                view.tvStoreName.setText(store.attributes.name)
            view.tvStoreTagLine.setText(store.attributes.tagline)
            itemView.setOnClickListener {
                action.onItemClick(store, adapterPosition)
//            btnShow.setOnClickListener {
//                click.onButtonClick(product)
//            }
            }
        }
    }
}