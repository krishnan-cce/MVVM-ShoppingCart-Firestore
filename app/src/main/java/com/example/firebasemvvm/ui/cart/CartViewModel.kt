package com.example.firebasemvvm.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CartViewModel@Inject constructor(application: Application,
                                       val auth: FirebaseAuth,
                                       val database : FirebaseFirestore,
                                       val storage : StorageReference
)
    : AndroidViewModel(application){

    /** Error Msg */
    private var errorMsg = MutableLiveData<Network<String>>()
    fun getErrorMsg() = errorMsg

    /** Detail Fields for product in cart*/
    val productName = MutableLiveData<String>()



    val getItemPrice = MutableLiveData<Double>()

    val productId = ArrayList<String>()         //here we are getting arrayList of cart items id
    var distinct = productId.toSet().toList()   //and here we are selecting by distinct id

    val productDb = database.collection(PRODUCTS)
    /** cart db & total*/
    val cartDb = database.collection(CART)
    val totalDb = database.collection(TOTAL)
    /** Save products from cart of current user */
    val cartProducts = MutableLiveData<List<Cart>>()

    /** Click Event for  add products */
    private var onaddClick = MutableLiveData<Int>()
    fun getClickAdd(): LiveData<Int> = onaddClick

    /** Click Event for  substract products */
    private var onsubClick = MutableLiveData<Int>()
    fun getClickSub(): LiveData<Int> = onsubClick





    fun getCartDetails(){
        cartDb.whereEqualTo("userId",SessionManager.sessionToken).get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    cartProducts.postValue(convertData(it,cartProducts))
                }else{
                    errorMsg.postValue(Network.Error("Oops ! you dont have any products in your cart"))
                }
            }
            .addOnFailureListener {
                errorMsg.postValue(Network.Error("An Error occured ${it.message}"))
            }
    }

    private fun convertData(it: QuerySnapshot?, cartProducts: MutableLiveData<List<Cart>>): List<Cart>? {
        val newCart = mutableListOf<Cart>()
        it?.forEach { doc ->
            val cart : Cart = doc.toObject<Cart>(Cart::class.java)
            newCart.add(cart)
            for (data in newCart){
                productId.add(data.productId.toString())
                productId.distinctBy { data.productId.toString() }
                distinct = productId.toSet().toList()
            }
           // productId.add(cart.productId.toString())

        }
        val sortedPosts = newCart.sortedByDescending {
            it.productName
        }
        cartProducts.value = sortedPosts


        return cartProducts.value
    }



    fun addData(item: Cart) {

        productDb.whereEqualTo("productId",item.productId).get()
            .addOnSuccessListener {

                it.forEach {
                    val product : Products = it.toObject<Products>(Products::class.java)

                    getItemPrice.postValue(product.productPrice.toString().toDouble())
                    val itemPrice = product.productPrice
                    val currentProductStock = product.productStock!!.toDouble()
                    val finalStock = currentProductStock -1

                    product.productStock = finalStock
                    val updatedPrice = itemPrice?.times(finalStock)

                        if (currentProductStock == 0.0){
                            errorMsg.postValue(Network.Error("Oops ! Product Out of Stock"))
                        }else{
                            productDb.document(item.productId.toString()).update("productStock",finalStock)
                            cartDb.document(item.cartId.toString()).update("productQty",item.productQty
                                ,"productTotalPrice",updatedPrice)
                        }

                    }

            }
            .addOnFailureListener {
                errorMsg.postValue(Network.Error(it.message.toString()))
            }


    }
    fun subData(item: Cart) {
        if (item.productQty!! <= 0){
            errorMsg.postValue(Network.Error("Oops ! Quantity Cannot less than 0"))
            item.productQty = 1.0

        }else{
            productDb.whereEqualTo("productId",item.productId).get()
                .addOnSuccessListener {

                    it.forEach {
                        val product : Products = it.toObject<Products>(Products::class.java)

                        getItemPrice.postValue(product.productPrice.toString().toDouble())
                        val itemPrice = product.productPrice
                        val currentProductStock = product.productStock!!.toDouble()
                        val finalStock = currentProductStock + 1

                            val updatedPrice = itemPrice?.times(item.productQty.toString().toDouble())
                            product.productStock = finalStock

                            productDb.document(item.productId.toString()).update("productStock",finalStock)
                            cartDb.document(item.cartId.toString()).update("productQty",item.productQty
                                ,"productTotalPrice",updatedPrice)
                        }


                }
                .addOnFailureListener {

                    errorMsg.postValue(Network.Error(it.message.toString()))
                }
        }

    }
    fun deleteFromCart(item: Cart){
        cartDb.document(item.cartId.toString()).get()
            .addOnSuccessListener {
                val cart : Cart? = it.toObject<Cart>(Cart::class.java)
                val currentStock = cart?.productQty
                val productId = cart?.productId.toString()

                productDb.document(productId).get()
                    .addOnSuccessListener {
                        val product : Products? = it.toObject<Products>(Products::class.java)
                        val currentProductStock = product?.productStock
                        val finalUpdatedStock = currentStock?.plus(currentProductStock!!)
                        productDb.document(productId).update("productStock",finalUpdatedStock)
                            .addOnSuccessListener {
                                cartDb.document(item.cartId.toString()).delete()
                            }

                    }
            }
    }



    fun getCart(){
        cartDb.whereEqualTo("userId",SessionManager.sessionToken)
            .addSnapshotListener{value,error ->
                error?.let {
                    errorMsg.postValue(Network.Error(it.message.toString()))
                    return@addSnapshotListener
                }
                value.let {
                    if (!value?.isEmpty!!){
                        cartProducts.postValue(convertData(it,cartProducts))
                    }else{
                        errorMsg.postValue(Network.Error("Oops ! you dont have any products in your cart"))
                    }
                }
            }
    }



    fun addToTotal(total:Total){
        val db = totalDb.document()
        val docId = db.id
        total.totalId = docId.toString()
        total.userId = SessionManager.sessionToken
        total.productId = distinct as ArrayList<String>

        db.set(total).addOnSuccessListener {

        }
        .addOnFailureListener {
            errorMsg.postValue(Network.Error(it.message.toString()))
        }
    }



}
