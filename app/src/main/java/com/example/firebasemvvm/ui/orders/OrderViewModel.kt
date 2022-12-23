package com.example.firebasemvvm.ui.orders

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.firebasemvvm.data.model.cart.Cart
import com.example.firebasemvvm.data.model.product.Products
import com.example.firebasemvvm.data.model.total.Total
import com.example.firebasemvvm.utils.Network
import com.example.firebasemvvm.utils.Products.PRODUCTS
import com.example.firebasemvvm.utils.SessionManager
import com.example.firebasemvvm.utils.cart.CART
import com.example.firebasemvvm.utils.cart.TOTAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(application: Application,
                                         val auth: FirebaseAuth,
                                         val database : FirebaseFirestore,
                                         val storage : StorageReference
)
    : AndroidViewModel(application){

    /** Error Msg */
    private var errorMsg = MutableLiveData<Network<String>>()
    fun getErrorMsg() = errorMsg

    /** Save orders of current user */
    val productList = MutableLiveData<List<Products>>()

    val totalCollection = database.collection(TOTAL)




    fun setProductIds(productIds: List<String>) {
        val products = mutableListOf<Products>()
        for (productId in productIds) {
            val productCollection = database.collection(PRODUCTS)
            productCollection.document(productId).get().addOnSuccessListener { productSnapshot ->
                //val productId = productSnapshot.id
                val productName = productSnapshot.getString("productName")
                val productImageUrl = productSnapshot.getString("productImageUrl")
                val productPrice = productSnapshot.getDouble("productPrice")
                val productIde = productSnapshot.getString("productId")
                val product = Products(productIde, productName,productImageUrl,productPrice.toString())
                //Log.d("Product Details", "ID: ${productIde.toString()}, Name: $productName, Price: ${productPrice.toString()}")

                products.add(product)
            }
        }

        productList.value = products
    }

    fun getProductIds() {
        totalCollection.document(SessionManager.sessionToken).get().addOnSuccessListener { documentSnapshot ->
            val productIds = documentSnapshot.get("productId") as List<String>
            setProductIds(productIds)
        }
    }


}