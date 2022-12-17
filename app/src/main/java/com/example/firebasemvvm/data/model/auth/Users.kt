package com.example.firebasemvvm.data.model.auth

data class Users (
    var userId: String? = null,
    var email: String? = null,
    var name: String? = null,
    var username: String? = null,
    var imageUrl: String? = null,
    var bio: String? = null,
    var password: String? = null
)