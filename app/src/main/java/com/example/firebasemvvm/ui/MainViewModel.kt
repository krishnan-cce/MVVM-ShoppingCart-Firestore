package com.example.firebasemvvm.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firebasemvvm.data.model.auth.Users
import com.example.firebasemvvm.utils.AuthConstants.USERS
import com.example.firebasemvvm.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel@Inject constructor(application: Application,
                                       val auth: FirebaseAuth,
                                       val database : FirebaseFirestore
)
    : AndroidViewModel(application){

    /** Read current user details*/
    val userName = MutableLiveData<String>()
    val userId = MutableLiveData<String>()

    /** Click Event for signOut */
    private var logoutClick = MutableLiveData<Int>()
    fun getClickLogout(): LiveData<Int> = logoutClick


    init {

    }


    fun onLogOut(option: Int){
        //option: Int
        logoutClick.value = option
        auth.signOut()
        //activeUser.postValue(false)
    }

    fun getUserDetails(){
        database.collection(USERS).document(SessionManager.sessionToken).get()
            .addOnSuccessListener {
                val user : Users? = it.toObject<Users>(Users::class.java)
                user!!.userId =userId.value
            }
    }

}