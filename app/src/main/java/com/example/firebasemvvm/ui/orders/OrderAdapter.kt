package com.example.firebasemvvm.ui.orders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasemvvm.R
import com.example.firebasemvvm.data.model.cart.Cart
import com.example.firebasemvvm.data.model.product.Products
import com.example.firebasemvvm.databinding.CartRowLayoutBinding
import com.example.firebasemvvm.databinding.ItemOrderLayoutBinding
import com.example.firebasemvvm.utils.setImageUrl

class OrderAdapter(private var products: List<Products>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val ivProductImg: ImageView = view.findViewById(R.id.ivProductImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.tvProductName.text = product.productName
        holder.tvProductPrice.text = product.productPrice.toString()
        holder.ivProductImg.setImageUrl(product.productImageUrl.toString())
    }

    override fun getItemCount() = products.size

    fun setProducts(products: List<Products>) {
        this.products = products
        notifyDataSetChanged()
    }
}
