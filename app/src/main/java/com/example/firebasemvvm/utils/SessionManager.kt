package com.example.firebasemvvm.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.TextUtils
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.firebasemvvm.data.model.auth.Users
import com.example.firebasemvvm.utils.Active.IS_LOGIN
import com.example.firebasemvvm.utils.SessionToken.CURRENT_USER_NAME
import com.example.firebasemvvm.utils.SessionToken.CURRENT_USER_TOKEN
import com.example.firebasemvvm.utils.cart.CART_LIST
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.reflect.Type;

class SessionManager {

    companion object {
        lateinit var sharedPref: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private const val PREF_NAME = "FBmvvm"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        fun initializeContext(context: Context) {
            Companion.context = context
            sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            editor = sharedPref.edit()
        }

        var sessionToken: String
            get() = (sharedPref.getString(CURRENT_USER_TOKEN, "") ?: "")
            set(s) = sharedPref.edit().putString(CURRENT_USER_TOKEN, s).apply()

        var userName: String
            get() = (sharedPref.getString(CURRENT_USER_NAME, "") ?: "")
            set(s) = sharedPref.edit().putString(CURRENT_USER_NAME, s).apply()

        var userEmail: String
            get() = (sharedPref.getString("Email", "") ?: "")
            set(s) = sharedPref.edit().putString("Email", s).apply()


        fun saveSessionToken(item : Users){
            isLogin = true
            sessionToken = item.userId.toString()
            userName = item.name.toString()
            userEmail = item.email.toString()
        }


        var isLogin: Boolean
            get() = sharedPref.getBoolean(IS_LOGIN, false)
            set(shuffle) = sharedPref.edit().putBoolean(IS_LOGIN, shuffle).apply()

        fun clear() {
            editor.clear()
            editor.apply()
        }




        fun saveMap(inputMap: ArrayList<String>, context: Context) {
            val fos: FileOutputStream = context.openFileOutput("map", Context.MODE_PRIVATE)
            val os = ObjectOutputStream(fos)
            os.writeObject(inputMap)
            os.close()
            fos.close()
        }

        fun loadMap(context: Context): ArrayList<String> {
            return try {
                val fos: FileInputStream = context.openFileInput("map")
                val os = ObjectInputStream(fos)
                val map: ArrayList<String> = os.readObject() as ArrayList<String>
                os.close()
                fos.close()
                map
            } catch (e: Exception) {
                arrayListOf()
            }
        }

        fun deleteMap(context: Context): Boolean {
            val file: File = context.getFileStreamPath("map")
            return file.delete()
        }
















        var cartList: ArrayList<String>
            get() {
                val objStrings = getListString(CART_LIST)
                return ArrayList()
            }
            set(value) {
                checkForNullKey(CART_LIST)
                val objStrings = ArrayList<String>()
                for (obj in value) {
                    objStrings.add(Gson().toJson(obj))
                }
                putListString(CART_LIST, objStrings)
            }
        private fun getListString(key: String?): java.util.ArrayList<String> {
            return java.util.ArrayList(
                listOf(
                    *TextUtils.split(
                        sharedPref.getString(key, ""),
                        "‚‗‚"
                    )
                )
            )
        }
        private fun putListString(key: String?, stringList: java.util.ArrayList<String>) {
            checkForNullKey(key)
            val myStringList = stringList.toTypedArray()
            sharedPref.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply()
        }

        private fun checkForNullKey(key: String?) {
            if (key == null) {
                throw NullPointerException()
            }
        }
    }

    }