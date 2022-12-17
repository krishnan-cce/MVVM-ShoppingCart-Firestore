package com.example.firebasemvvm.data.model.cart

data class Cart(
    var cartId: String? = null,
    var userId: String? = null,
    var productId: String? = null,
    var productImg:String? = null,
    var productName: String? = null,
    var stockDiffrence: Double? = null,
    var productStock: Double? = null,
    var productQty: Double? = null,
    var productTotalPrice: Double? = null,
    var productPrice: Double? = null,
)
