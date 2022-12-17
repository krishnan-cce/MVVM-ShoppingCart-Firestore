package com.example.firebasemvvm.ui.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.firebasemvvm.R
import com.example.firebasemvvm.data.model.product.Products
import com.example.firebasemvvm.databinding.ActivityMyproductsBinding

import com.example.firebasemvvm.ui.products.AllProductsAdapter
import com.example.firebasemvvm.ui.products.OnItemsClickListener
import com.example.firebasemvvm.ui.products.ProductViewModel
import com.example.firebasemvvm.utils.addToCart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProductsActivity: AppCompatActivity() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var recAdapter: AllProductsAdapter
    private lateinit var binding: ActivityMyproductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        productViewModel.getAllProducts()
        setUpRecyclerView()

        productViewModel.allProducts.observe(this, Observer { response ->
            recAdapter.setData(response as List<Products>)

        })
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_myproducts)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        binding.lifecycleOwner = this
        binding.chatListFgVM = productViewModel

    }

    fun setUpRecyclerView(){
        recAdapter = AllProductsAdapter(OnItemsClickListener { item: Products, _: Int ->
            addToCart(
                product = item,
                viewModel = productViewModel,
                positiveClick = {

                },
                negativeClick = {

                }
            )

        })
        binding.rvAllProducts.apply {
            adapter = recAdapter
        }
    }

}