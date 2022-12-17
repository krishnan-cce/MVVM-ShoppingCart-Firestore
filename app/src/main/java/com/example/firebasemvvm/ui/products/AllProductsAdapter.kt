package com.example.firebasemvvm.ui.products

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasemvvm.data.model.product.Products
import com.example.firebasemvvm.databinding.AllProductsLayoutBinding
import com.example.firebasemvvm.utils.setImageUrl

class AllProductsAdapter(private val clickListener: OnItemsClickListener)

    : RecyclerView.Adapter<AllProductsAdapter.ViewHolder>()  {
    private var movieList = ArrayList<Products>()

    class ViewHolder private constructor(private val binding: AllProductsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Products, clickListener: OnItemsClickListener, position: Int ) {
            binding.apply {

                tvAllPdtName.text = item.productName
                ivAllPdtImg.setImageUrl(item.productImageUrl.toString())
                tvAllPdtPrice.text = "$ " + item.productPrice

                itemView.setOnClickListener {
                    clickListener.onItemEditCLick(item, position)
                }

            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AllProductsLayoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, position)
    }
    private fun getItem(position: Int): Products {
        return movieList[position]
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Products>) {
        movieList.clear()
        movieList.addAll(list)
        notifyDataSetChanged()
    }


}

class OnItemsClickListener(val clickListener: (data: Products, pos: Int) -> Unit) {
    fun onItemEditCLick(data: Products, pos: Int) = clickListener(data, pos)
}