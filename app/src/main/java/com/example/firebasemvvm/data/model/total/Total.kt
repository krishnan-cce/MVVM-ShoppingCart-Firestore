package com.example.firebasemvvm.data.model.total

import com.example.firebasemvvm.data.model.product.Products


data class Total(
    var totalId: String? = null,
    var userId: String? = null,
    var productId: ArrayList<String>?,
    var total:Double? = null,
    var subTotal:Double? = null
)

sealed class Item {
    data class TotalItem(val total: Total) : Item()
    data class ProductsItem(val products: Products) : Item()
}
