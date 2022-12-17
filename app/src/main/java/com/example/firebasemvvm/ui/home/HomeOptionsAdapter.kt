package com.example.firebasemvvm.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasemvvm.data.model.home.HomeOptions
import com.example.firebasemvvm.databinding.LayoutHomeOptionsBinding


class HomeOptionsAdapter(private val clickListener: HomeOptionsClickListener) :
    RecyclerView.Adapter<HomeOptionsAdapter.ViewHolder>() {

    private val allOrders: ArrayList<HomeOptions> = ArrayList()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    private fun getItem(position: Int): HomeOptions {
        return allOrders[position]
    }

    override fun getItemCount(): Int {
        return allOrders.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: ArrayList<HomeOptions>) {
        allOrders.clear()
        allOrders.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder private constructor(private val binding: LayoutHomeOptionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeOptions, clickListener: HomeOptionsClickListener) {
            binding.apply {
                imgHomeOptions.setImageResource(item.icon)
                tvOptionName.text = item.name
                rootLayout.setOnClickListener {
                    clickListener.onClick(item)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutHomeOptionsBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


class HomeOptionsClickListener(val clickListener: (data: HomeOptions) -> Unit) {
    fun onClick(data: HomeOptions) = clickListener(data)
}
