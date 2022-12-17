package com.example.firebasemvvm.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firebasemvvm.data.model.auth.Users
import com.example.firebasemvvm.data.model.product.Products
import com.example.firebasemvvm.utils.AuthConstants.USERS
import com.example.firebasemvvm.utils.Network
import com.example.firebasemvvm.utils.SessionManager
import com.example.firebasemvvm.utils.SessionManager.Companion.isLogin
import com.example.firebasemvvm.utils.SessionManager.Companion.saveSessionToken
import com.example.firebasemvvm.utils.isValidEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(application: Application,
                                                  val auth: FirebaseAuth,
                                                  val database : FirebaseFirestore
                                         )
    : AndroidViewModel(application){

    /** Detail Fields for Register*/
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val userName = MutableLiveData<String>()
    val bio = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    /** Detail Fields for Login*/
    val logemail = MutableLiveData<String>()
    val logpassword = MutableLiveData<String>()

    /** Click Event for Register */
    private var clickOption = MutableLiveData<Int>()
    fun getClickOptions(): LiveData<Int> = clickOption

    /** Click Event for Login */
    private var loginClick = MutableLiveData<Int>()
    fun getClickLogin(): LiveData<Int> = loginClick

    /** Error Msg */
    private var errorMsg = MutableLiveData<Network<String>>()
    fun getErrorMsg() = errorMsg

    /** CHeck if User is SignedIn */
    var activeUser = MutableLiveData<Boolean>()


    init {
        val currentUser = auth.currentUser
        currentUser?.uid
        isSignedIn()
    }

    fun isSignedIn(){
        if (auth.currentUser != null){
            isLogin = true
            activeUser.postValue(true)
        }else {
            isLogin = false
            activeUser.postValue(false)
        }
    }


    fun registerClicked(option: Int) {

        clickOption.value = option

        val name = name.value?.trim() ?: ""
        val userName = userName.value?.trim() ?: ""
        val email = email.value?.trim() ?: ""
        val password = password.value?.trim() ?: ""


        when {
            name.isEmpty() -> {
                errorMsg.postValue(Network.Error("Enter Name"))
            }
            userName.length < 2 -> {
                errorMsg.postValue(Network.Error("Enter Username"))
            }
            email.isEmpty() -> {
                errorMsg.postValue(Network.Error("Enter Email"))
            }
            !email.isValidEmail() -> {
                errorMsg.postValue(Network.Error("Enter valid Email"))
            }
            password.isEmpty() -> {
                errorMsg.postValue(Network.Error("Enter password"))
            }
            password.length < 6 -> {
                errorMsg.postValue(Network.Error("Enter valid password"))
            }

            else -> {
                val setData = Users(
                    userId = "",
                    email = email,
                    name = name,
                    username = userName,
                    password = password,
                    bio = "Hey There !",
                )
                registerUser(setData)

            }
        }
    }

    private fun registerUser(userData : Users){
        errorMsg.postValue(Network.Loading())
        val usersDb = database.collection(USERS)

        usersDb.whereEqualTo("username",userData.username).get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    errorMsg.postValue(Network.Error("Username Already Exists"))
                }else{
                    auth.createUserWithEmailAndPassword(userData.email!!,userData.password!!)
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful){
                                userData.userId = auth.currentUser!!.uid
                                SessionManager.clear()
                                saveSessionToken(userData)  //save current User details to session manager
                                val userDoc = usersDb.document(userData.userId.toString())
                                userDoc.set(userData)

                                activeUser.postValue(true)
                                errorMsg.postValue(Network.Success("Registration Successfull"))
                            }else{
                                errorMsg.postValue(Network.Error("An Error ${task.exception?.message} "))
                            }
                        }
                        .addOnFailureListener { execption ->
                            errorMsg.postValue(Network.Error("Registration Failed ${execption.message}"))
                        }
                     }
                }

    }


    fun logInClicked(option: Int){
        loginClick.value = option

        val email = logemail.value?.trim() ?: ""
        val password = logpassword.value?.trim() ?: ""
        when {
            email.isEmpty() -> {
                errorMsg.postValue(Network.Error("Enter Email"))
            }
            !email.isValidEmail() -> {
                errorMsg.postValue(Network.Error("Enter valid Email"))
            }
            password.isEmpty() -> {
                errorMsg.postValue(Network.Error("Enter password"))
            }
            password.length < 6 -> {
                errorMsg.postValue(Network.Error("Enter valid password"))
            } else -> {
                val setData = Users(
                    email = email,
                    password = password
                )
                onLogIn(setData)

            }
        }
    }

    private fun onLogIn(userData : Users){
        auth.signInWithEmailAndPassword(userData.email!!, userData.password!!)
            .addOnCompleteListener  { task ->
                if (task.isSuccessful){

                    SessionManager.clear()
                    userData.userId = auth.currentUser!!.uid
                    //saveSessionToken(userData)



                    activeUser.postValue(true)
                    errorMsg.postValue(Network.Success("Login Successfull"))
                }else{
                    errorMsg.postValue(Network.Error("Login Error"))
                }
            }
            .addOnFailureListener { execption ->
                errorMsg.postValue(Network.Error("Registration Failed ${execption.message}"))
            }
    }


}