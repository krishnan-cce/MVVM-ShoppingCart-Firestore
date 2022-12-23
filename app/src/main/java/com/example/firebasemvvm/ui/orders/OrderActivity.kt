package com.example.firebasemvvm.ui.orders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasemvvm.R
import com.example.firebasemvvm.data.model.cart.Cart
import com.example.firebasemvvm.data.model.product.Products
import com.example.firebasemvvm.data.model.total.Item
import com.example.firebasemvvm.databinding.ActivityCartBinding
import com.example.firebasemvvm.databinding.ActivityOrderBinding
import com.example.firebasemvvm.ui.cart.CartViewModel
import com.example.firebasemvvm.ui.products.ProductViewModel
import com.example.firebasemvvm.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var viewModel: OrderViewModel
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
       // rv()
        viewModel.getProductIds()

        rv()


        viewModel.productList.observe(this, Observer { products ->
            orderAdapter.setProducts(products)


        })



    }


    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        binding.lifecycleOwner = this
        binding.orderVM = viewModel
    }


    fun rv(){

        binding.rvProducts.apply {
            orderAdapter = OrderAdapter(emptyList())
            layoutManager =  LinearLayoutManager(applicationContext)
            }

        }

    }


