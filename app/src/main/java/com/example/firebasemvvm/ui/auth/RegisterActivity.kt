package com.example.firebasemvvm.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.example.firebasemvvm.R
import com.example.firebasemvvm.databinding.ActivityRegisterBinding
import com.example.firebasemvvm.ui.home.HomeActivity

import com.example.firebasemvvm.utils.Network
import com.example.firebasemvvm.utils.SessionManager.Companion.isLogin
import com.example.firebasemvvm.utils.openActivity
import com.example.firebasemvvm.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBinding()
        setupObservers()
        //clickEvents()
        binding.button4.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        viewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]
        binding.authenticationViewModel = viewModel
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
        viewModel.getClickOptions().observe(this) {
            when (it) {
                1 -> {
                    openActivity<HomeActivity>()
                    toast("User Registered Successfully")
                }

            }
        }
    }


    override fun onStart() {
        super.onStart()

        if (isLogin){
            openActivity<HomeActivity>()

        }

//        viewModel.activeUser.observe(this, Observer { isSignedInUser ->
//            if (isSignedInUser) {
//
//                val intent = Intent(this, MainActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//                finish()
//            } else {
//                //toast("User not Signed")
//            }
//        })

    }
}