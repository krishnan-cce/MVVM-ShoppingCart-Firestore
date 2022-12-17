package com.example.firebasemvvm.ui.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasemvvm.data.model.cart.Cart
import com.example.firebasemvvm.databinding.CartRowLayoutBinding

import com.example.firebasemvvm.utils.setImageUrl


class CartAdapter (private val clickListener: OnAddClickListener ,
                   private val onSubClickListener: OnSubClickListener ,
                   private val onItemSelectListener: onItemSelectListener)

    : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    var cartList = ArrayList<Cart>()



    class ViewHolder private constructor(private val binding: CartRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Cart, clickListener: OnAddClickListener, onItemSelectListener : onItemSelectListener,
                 onSubClickListener :OnSubClickListener, position: Int ) {
            binding.apply {
                tvCartpdtName.text = item.productName
                tvCartQty.text = item.productQty.toString()
                ivCartImage.setImageUrl(item.productImg.toString())
                tvCartproductPrice.text = "$ " + item.productTotalPrice

                val cartProducts = ArrayList<String>()
                cartProducts.add(item.productId.toString())

                ivAdd.setOnClickListener {
                    clickListener.onItemAddCLick(item, position)

                }
                itemView.setOnClickListener{
                    onItemSelectListener.onItemSelectListener(item,position)
                }
                ivSub.setOnClickListener {
                    onSubClickListener.onItemSubCLick(item,position)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CartRowLayoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener,onItemSelectListener,onSubClickListener, position)
    }
    private fun getItem(position: Int): Cart {
        return cartList[position]
    }




    override fun getItemCount(): Int {
        return cartList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Cart>) {
        cartList.clear()
        cartList.addAll(list)
        notifyDataSetChanged()


    }




}

class OnAddClickListener(val clickListener: (data: Cart, pos: Int) -> Unit) {
    fun onItemAddCLick(data: Cart, pos: Int) = clickListener(data, pos)
}
class OnSubClickListener(val subClickListener: (data: Cart, pos: Int) -> Unit) {
    fun onItemSubCLick(data: Cart, pos: Int) = subClickListener(data, pos)
}

class onItemSelectListener(val ItemSelectListener: (data: Cart, pos: Int) -> Unit) {
    fun onItemSelectListener(data: Cart, pos: Int) = ItemSelectListener(data, pos)
}


