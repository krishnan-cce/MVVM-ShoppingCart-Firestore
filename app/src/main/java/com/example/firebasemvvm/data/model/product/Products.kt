package com.example.firebasemvvm.data.model.product

data class Products(
    var userId: String? = null,
    var productId: String? = null,
    var productName: String? = null,
    var productDescription: String? = null,
    var productImageUrl: String? = null,
    var productStock: Double? = null,
    var productPrice: Double? = null
)
