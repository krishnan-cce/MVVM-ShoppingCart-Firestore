package com.example.firebasemvvm.ui.products

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firebasemvvm.data.model.cart.Cart
import com.example.firebasemvvm.data.model.product.Products
import com.example.firebasemvvm.utils.Network
import com.example.firebasemvvm.utils.Products.PRODUCTS
import com.example.firebasemvvm.utils.SessionManager
import com.example.firebasemvvm.utils.cart.CART
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class ProductViewModel @Inject constructor(application: Application,
                                          val auth: FirebaseAuth,
                                          val database : FirebaseFirestore,
                                          val storage : StorageReference
)
    : AndroidViewModel(application){

    /** progres bar */
    var showProgress = MutableLiveData<Boolean>()

    /** Error Msg */
    private var errorMsg = MutableLiveData<Network<String>>()
    fun getErrorMsg() = errorMsg

    /** Detail Fields for product upload*/
    val productName = MutableLiveData<String>()
    val productDescription = MutableLiveData<String>()
    val productStock = MutableLiveData<String>()
    val productImage = MutableLiveData<String>()
    val productPrice = MutableLiveData<String>()

    /** Click Event for open add products */
    private var onaddClick = MutableLiveData<Int>()
    fun getClickAdd(): LiveData<Int> = onaddClick

    /** Click Event for open add products */
    private var onimageClick = MutableLiveData<Int>()
    fun getImageClick(): LiveData<Int> = onimageClick

    /** Save products of current user */
    val userProducts = MutableLiveData<List<Products>>()

    /** all products of all user */
    val allProducts = MutableLiveData<List<Products>>()

    /** Products DB*/
    private val productDB = database.collection(PRODUCTS)




    init {
        getMyProducts()

    }
    fun saveProductImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        showProgress.value = true
        val storageRef = storage
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("ProductImages/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask
            .addOnSuccessListener {
                showProgress.value = false
                val result = it.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
            }
            .addOnFailureListener { exc ->
                errorMsg.postValue(Network.Error("${exc.message}"))
                showProgress.value = false
            }
    }

    fun imageClick(option: Int){
        onimageClick.value = option
    }

    fun uploadProductImage(uri: Uri){
        saveProductImage(uri) {
            productImage.postValue(it.toString())
        }

    }

    fun saveProduct(){
        val name = productName.value?.trim() ?: ""
        val description = productDescription.value?.trim() ?: ""
        val stock = productStock.value.toString() ?: ""
        val price = productPrice.value?.toString() ?: ""

        when {
            name.isEmpty() -> {
                errorMsg.postValue(Network.Error("Enter Name"))
            }
            description.isEmpty() -> {
                errorMsg.postValue(Network.Error("Enter Username"))
            }
            stock.isEmpty() -> {
                errorMsg.postValue(Network.Error("Enter Stock"))
            }
            price.isEmpty() -> {
                errorMsg.postValue(Network.Error("Enter Price"))
            }
            else ->{

            val setData = Products(
                userId = SessionManager.sessionToken,
                productId = "",
                productName = name,
                productDescription = description,
                productImageUrl = productImage.value.toString(),
                productStock = stock.toDouble(),
                productPrice = price.toDouble()
            )
            addProductToDb(setData)

            }

        }
    }

    private fun addProductToDb(productData : Products) {
        val productDB = database.collection(PRODUCTS).document()
        val productId = productDB.id
        productData.productId = productId

        productDB.set(productData)

            .addOnSuccessListener {

                errorMsg.postValue(Network.Success("Product Added Successfully"))
            }
            .addOnFailureListener {
                errorMsg.postValue(Network.Success("An Error occured ${it.message}"))
            }

    }

    fun addProduct(option: Int){
        onaddClick.value = option

    }


    fun getMyProducts(){

        productDB.whereEqualTo("userId",SessionManager.sessionToken)
            .addSnapshotListener { value, error ->
                error?.let {
                    errorMsg.postValue(Network.Error(it.message.toString()))
                    return@addSnapshotListener
                }
                value?.let {
                    if (!value.isEmpty) {
                        userProducts.postValue(convertData(it,userProducts))
                    }
                }
            }


    }

    private fun convertData(list: QuerySnapshot, userProducts: MutableLiveData<List<Products>>): List<Products>? {
        val newProducts = mutableListOf<Products>()
        list.forEach { doc ->
            val products : Products = doc.toObject<Products>(Products::class.java)

            newProducts.add(products)
        }
        val sortedPosts = newProducts.sortedByDescending { it.productName }
        userProducts.value = sortedPosts

    return userProducts.value
    }


     fun deleteProduct(productId : String){
        productDB.document(productId).delete()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    getMyProducts()
                }
            }
            .addOnFailureListener {

            }
    }

    fun getAllProducts(){

        productDB.whereNotEqualTo("userId",SessionManager.sessionToken).addSnapshotListener { value, error ->
            error?.let {
                errorMsg.postValue(Network.Error(it.message.toString()))
                return@addSnapshotListener
            }
            value?.let {
                if (!value.isEmpty) {
                    allProducts.postValue(convertData(it,allProducts))
                }
            }
        }
    }



    fun saveToCart(cartData: Cart) {
        val cartDB = database.collection(CART).document()
        val cartId = cartDB.id

        cartData.cartId = cartId
        cartData.userId = SessionManager.sessionToken

        productDB.whereEqualTo("productId",cartData.productId).get()
            .addOnSuccessListener {
                val updatedProduct = mutableListOf<Products>()
                it.forEach {
                    val product : Products = it.toObject<Products>(Products::class.java)
                    updatedProduct.add(product)
                    val currentProductStock = product.productStock

                    val updatedProductStock = currentProductStock?.minus(cartData.productQty!!)
                    if (cartData.productQty!! > currentProductStock!!){
                        errorMsg.postValue(Network.Error("Oops ! Current Stock is only $productStock"))
                    }else{
                        cartData.productPrice = product.productPrice
                        cartData.productStock = product.productStock
                        productDB.document(cartData.productId.toString()).update("productStock",updatedProductStock)
                        cartDB.set(cartData)
                    }

                }
            }
            .addOnFailureListener {
                errorMsg.postValue(Network.Error(it.message.toString()))
            }

    }






}