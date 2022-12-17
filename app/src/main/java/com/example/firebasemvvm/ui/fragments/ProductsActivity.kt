package com.example.firebasemvvm.ui.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasemvvm.R
import com.example.firebasemvvm.data.model.product.Products
import com.example.firebasemvvm.databinding.ActivityHomeBinding
import com.example.firebasemvvm.databinding.ActivityProductsBinding

import com.example.firebasemvvm.ui.cart.CartActivity
import com.example.firebasemvvm.ui.home.HomeViewModel
import com.example.firebasemvvm.ui.products.*
import com.example.firebasemvvm.utils.openActivity
import com.example.firebasemvvm.utils.showAlert
import com.example.firebasemvvm.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsActivity: AppCompatActivity()  {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var recAdapter: ProductsAdapter
    private lateinit var binding: ActivityProductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        clickEvents()
        setUpRecyclerView()


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val myProduct = recAdapter.productList[position]
                showAlert(
                    title = "Are you sure...!",
                    titleDesc = "Delete " + myProduct.productName,
                    negativeClick = {
                        productViewModel.deleteProduct(myProduct.productId.toString())
                        toast("${myProduct.productName} deleted Succesfully ")
                    },
                    positiveClick = {
                        setUpProducts()
                    }

                )
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvProducts)
        }


        setUpProducts()

    }

    private fun setUpProducts(){
        productViewModel.userProducts.observe(this, Observer { response ->
            recAdapter.setData(response as List<Products>)

        })
    }


    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_products)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        binding.lifecycleOwner = this
        binding.chatListFgVM = productViewModel
    }

    private fun clickEvents() {
        productViewModel.getClickAdd().observe(this) {
            when (it) {
                1 -> openActivity<AddProductActivity>{ }
                2 -> openActivity<CartActivity>{ }
            }
        }
    }

    fun setUpRecyclerView(){
        recAdapter = ProductsAdapter(OnItemClickListener { item: Products, _: Int ->

        },
            OnDeleteClickListener { item:Products,_:Int ->

            })
        binding.rvProducts.apply {
            adapter = recAdapter
        }
    }
}