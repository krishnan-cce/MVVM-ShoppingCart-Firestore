package com.example.firebasemvvm.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.example.firebasemvvm.R
import com.example.firebasemvvm.databinding.ActivityLoginBinding
import com.example.firebasemvvm.ui.home.HomeActivity
import com.example.firebasemvvm.utils.Network
import com.example.firebasemvvm.utils.openActivity
import com.example.firebasemvvm.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBinding()
        setupObservers()

    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)

        binding.loginVM = viewModel
        binding.lifecycleOwner = this
    }

    private fun setupObservers() {
        viewModel.getErrorMsg().observe(this, Observer { response ->

            when (response) {
                is Network.Error -> {
                    response.message?.let { message ->
                        toast(message)
                    }
                }
                is Network.Loading -> {
                    response.message?.let { message -> toast("Loading $message") }

                }
                is Network.Success -> {
                    clickEvents()
                }
            }

        })
    }
    fun clickEvents() {
        viewModel.getClickLogin().observe(this) {
            when (it) {
                1 -> {
                    toast("User LogedIn Successfully")
                    openActivity<HomeActivity> {  }
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.activeUser.observe(this, Observer { isSignedInUser ->
            if (isSignedInUser) {
                openActivity<HomeActivity> {  }
            } else {
                //toast("User not Loged")
            }
        })

    }
}