package com.example.firebasemvvm.ui.products

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasemvvm.data.model.product.Products
import com.example.firebasemvvm.databinding.LayoutMyproductsBinding
import com.example.firebasemvvm.utils.DiffUtils
import com.example.firebasemvvm.utils.setImageUrl

class ProductsAdapter(private val clickListener: OnItemClickListener ,
                      private val onDeleteListener: OnDeleteClickListener)

    : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    var productList = ArrayList<Products>()

    class ViewHolder private constructor(private val binding: LayoutMyproductsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Products, clickListener: OnItemClickListener , onDeleteListener : OnDeleteClickListener, position: Int ) {
            binding.apply {
               tvPdtName.text = item.productName
               tvPdtStock.text = "Stock : " + item.productStock
                ivProductImage.setImageUrl(item.productImageUrl.toString())
                tvProductPrice.text = "$ " + item.productPrice

                itemView.setOnClickListener {
                    clickListener.onItemEditCLick(item, position)
                }

            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutMyproductsBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener,onDeleteListener, position)
    }
    private fun getItem(position: Int): Products {
        return productList[position]
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Products>) {
        productList.clear()
        productList.addAll(list)
        notifyDataSetChanged()
    }


}

class OnItemClickListener(val clickListener: (data: Products, pos: Int) -> Unit) {
    fun onItemEditCLick(data: Products, pos: Int) = clickListener(data, pos)
}
class OnDeleteClickListener(val deleteClickListener: (data: Products, pos: Int) -> Unit) {
    fun onItemDeleteCLick(data: Products, pos: Int) = deleteClickListener(data, pos)
}