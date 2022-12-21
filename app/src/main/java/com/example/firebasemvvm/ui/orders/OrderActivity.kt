package com.example.firebasemvvm.ui.orders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebasemvvm.R
import com.example.firebasemvvm.databinding.ActivityCartBinding
import com.example.firebasemvvm.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}