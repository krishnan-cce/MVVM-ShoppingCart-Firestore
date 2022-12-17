package com.example.firebasemvvm.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.firebasemvvm.R
import com.example.firebasemvvm.databinding.ActivityHomeBinding
import com.example.firebasemvvm.ui.auth.AuthenticationViewModel
import com.example.firebasemvvm.ui.auth.LoginActivity
import com.example.firebasemvvm.ui.cart.CartActivity
import com.example.firebasemvvm.ui.fragments.MyProductsActivity
import com.example.firebasemvvm.ui.fragments.ProductsActivity
import com.example.firebasemvvm.ui.products.AddProductActivity
import com.example.firebasemvvm.utils.SessionManager
import com.example.firebasemvvm.utils.openActivity
import com.example.firebasemvvm.utils.showSimpleAlert
import com.example.firebasemvvm.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeOptionsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupRecycler()
        setupObservers()
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

    }

    private fun setupRecycler() {
        adapter = HomeOptionsAdapter(HomeOptionsClickListener {
            when (it.name) {
                "Products" -> openActivity<MyProductsActivity> { }
                "My Products" -> openActivity<ProductsActivity> { }
                "Add Product" -> openActivity<AddProductActivity> { }
                "My Cart" -> openActivity<CartActivity>{}
                "Profile" -> toast("Coming soon...")
                "Update Stock" -> toast("Coming soon...")
                "My Orders" -> toast("Coming soon...")
                "Production Issue" -> toast("Coming soon...")
                "Production Receipt" -> toast("Coming soon...")
                "Sales Order" -> toast("Coming soon...")
            }
        })
        binding.optionsRecycler.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.getHomeList().observe(this) {
            adapter.submitList(it)
        }
        viewModel.getClickOption().observe(this) {
            when (it) {
                1 -> {
                    showSimpleAlert(
                        title = "Logout",
                        msg = "Are you sure you want to logout?",
                        btnMsg = "Logout",
                        btnNegative = "Cancel",
                        onPositiveButtonClick = {
                            SessionManager.clear()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        },
                        onNegativeButtonClick = {}
                    )
                }
            }
        }
    }

}