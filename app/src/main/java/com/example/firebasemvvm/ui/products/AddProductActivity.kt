package com.example.firebasemvvm.ui.products

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.firebasemvvm.R
import com.example.firebasemvvm.databinding.ActivityAddProductBinding
import com.example.firebasemvvm.utils.observeNetwork
import com.example.firebasemvvm.utils.pickImage
import com.example.firebasemvvm.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBinding()
        clickEvents()
        observeData()


    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        binding.productsVM = viewModel
        binding.lifecycleOwner = this
    }
    private fun observeData(){
        viewModel.getErrorMsg().observeNetwork(this,
            onSuccess = { response ->

            }, onError = { response ->
                toast(response.toString())
            }, onLoading = {
                // Show the progress bar
            })
    }



    fun clickEvents() {
        viewModel.getImageClick().observe(this) {
            when (it) {
                1 -> {

                    this.pickImage(
                        onImagePicked = {imageUri}
                    )
//                    val intent = Intent()
//                    intent.action = Intent.ACTION_GET_CONTENT
//                    intent.type = "image/*"
//                    val chooserIntent: Intent = Intent.createChooser(intent, "Pick Image")
//                    resultLauncher.launch(chooserIntent)
                }

            }
        }
    }
//    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
////        if (result.resultCode == Activity.RESULT_OK) {
////            val data: Intent? = result.data
////            this.imageUri = data?.data!!
////            binding.ivProductImg.setImageURI(imageUri)
////            viewModel.uploadProductImage(imageUri)
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data: Intent? = result.data
//            this.imageUri = data?.data!!
//            binding.ivProductImg.setImageURI(imageUri)
//            viewModel.uploadProductImage(imageUri)
//
//
//        }
//
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            this.imageUri = data?.data!!
            binding.ivProductImg.setImageURI(imageUri)
            viewModel.uploadProductImage(imageUri)

        }
    }


}


