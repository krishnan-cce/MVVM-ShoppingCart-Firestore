package com.example.firebasemvvm.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebasemvvm.R
import com.example.firebasemvvm.data.model.auth.Users
import com.example.firebasemvvm.data.model.home.HomeOptions
import com.example.firebasemvvm.utils.AuthConstants
import com.example.firebasemvvm.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(application: Application,
                                        val auth: FirebaseAuth,
                                        val database : FirebaseFirestore,
                                        val storage : StorageReference
)
    : AndroidViewModel(application){

    private var homeList = MutableLiveData<ArrayList<HomeOptions>>()
    private var clickOption = MutableLiveData<Int>()

    init {
        getUser()
        getList()
    }

    fun onClicked(option: Int) {
        clickOption.value = option
        auth.signOut()

    }

    private fun getList() {
        val list: ArrayList<HomeOptions> = ArrayList()
        list.add(HomeOptions(R.drawable.ic_sales, "Products"))
        list.add(HomeOptions(R.drawable.ic_sales_order, "My Products"))
        list.add(HomeOptions(R.drawable.ic_grn, "Add Product"))
        list.add(HomeOptions(R.drawable.ic_exchange, "My Cart"))
        list.add(HomeOptions(R.drawable.ic_stock_issue, "Profile"))
        list.add(HomeOptions(R.drawable.ic_stock_receipt, "Update Stock"))
        list.add(HomeOptions(R.drawable.ic_schedule, "My Orders"))
        list.add(HomeOptions(R.drawable.ic_production_issue, "Production Issue"))
        list.add(HomeOptions(R.drawable.ic_production_receipt, "Production Receipt"))
        list.add(HomeOptions(R.drawable.ic_sales_order, "Sales Order"))
        homeList.value = list
    }


    private fun getUser(){
        database.collection(AuthConstants.USERS).document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val user : Users? = it.toObject<Users>(Users::class.java)
                val userData = Users(
                    userId = auth.currentUser!!.uid,
                    email= user!!.email,
                    name=user.name,
                    username=user.username,
                    bio=user.bio
                )

                SessionManager.saveSessionToken(userData)
            }
            .addOnFailureListener {  }
    }


    fun getHomeList(): LiveData<ArrayList<HomeOptions>> = homeList
    fun getClickOption(): LiveData<Int> = clickOption
}