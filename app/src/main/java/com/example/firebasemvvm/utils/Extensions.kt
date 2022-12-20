package com.example.firebasemvvm.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.firebasemvvm.R
import com.example.firebasemvvm.data.model.cart.Cart
import com.example.firebasemvvm.data.model.product.Products
import com.example.firebasemvvm.ui.cart.CartViewModel
import com.example.firebasemvvm.ui.products.ProductViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText


fun String.isValidEmail() =
    isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun isValidPassword(password: String): Boolean {
    if (password.length < 6) return false
    if (password.firstOrNull { it.isDigit() } == null) return false
    if (password.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null) return false
    if (password.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null) return false
    if (password.firstOrNull { !it.isLetterOrDigit() } == null) return false

    return true
}


fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun ImageView.setImageUrl(url: String) {
    Glide.with(this.context)
        .load(url)
        .centerCrop()
        .into(this)
}
inline fun <reified T : Activity> Context.openActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
}

fun isInternetAvailable(): Boolean {
    val result: Boolean
    val connectivityManager = SessionManager.context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    result = when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }

    return result
}

fun Context.showAlert(
    title: String = "",
    titleDesc:String = "",
    negativeClick: () -> Unit,
    positiveClick: () -> Unit
) {
    if (this is Activity && !this.isFinishing) {
        val productDialog = Dialog(this, R.style.PopupDialogCustom)
        productDialog.setContentView(R.layout.layout_alert_dialog_title)
        productDialog.setCancelable(false)

        val tvTitle: TextView = productDialog.findViewById(R.id.tv_title)
        val tvTitleDesc: TextView = productDialog.findViewById(R.id.tv_title_desc)
        val btnDelete: Button = productDialog.findViewById(R.id.btn_alert_yes)
        val btnNo: Button = productDialog.findViewById(R.id.btn_alert_no)

        tvTitle.text = title
        tvTitleDesc.text = titleDesc

        btnDelete.setOnClickListener {
            negativeClick.invoke()
            productDialog.dismiss()
        }
        btnNo.setOnClickListener {
            positiveClick.invoke()
            productDialog.dismiss()
        }

        productDialog.show()
    }

}
fun View.show(condition: Boolean? = true) {
    if (condition == true) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun View.hide(condition: Boolean? = true) {
    if (condition == true) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}

fun Context.addToCart(
    product: Products,
    viewModel:ProductViewModel,
    positiveClick: () -> Unit,
    negativeClick: () -> Unit
) {
    val productDialog: Dialog?
    if (this is Activity && !this.isFinishing) {
        productDialog = Dialog(this, R.style.PopupDialogCustom)
        productDialog.setContentView(R.layout.add_to_cart_layout)
        productDialog.setCanceledOnTouchOutside(true)
        productDialog.setCancelable(false)
        productDialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val tvTitle: TextView = productDialog.findViewById(R.id.tv_title)
        val tvTotal: TextView = productDialog.findViewById(R.id.tv_total_price)
        val tvPdtName: TextView = productDialog.findViewById(R.id.tv_pdt_cart_name)
        val tvPdtSTock: TextView = productDialog.findViewById(R.id.tv_pdt_total_stock)
        val tvPdtPrice: TextView = productDialog.findViewById(R.id.tv_cart_pdt_price)
        val itemImage: ImageView = productDialog.findViewById(R.id.iv_cart_pdt)
        val tvPdtQty: TextInputEditText = productDialog.findViewById(R.id.et_cart_pdt_qty)
        val btnCancel: Button = productDialog.findViewById(R.id.btn_alert_no)
        val btnAdd: Button = productDialog.findViewById(R.id.btn_add_to_cart)

        tvTitle.text = "Add to Cart"
        itemImage.setImageUrl(product.productImageUrl.toString())
        tvPdtName.text = product.productName.toString()
        tvPdtSTock.text = "Stock Available : " + product.productStock.toString()
        tvPdtPrice.text = "Price : " + "$" + product.productPrice.toString()

        tvPdtQty.addTextChangedListener {
            if (!it!!.length.equals(0)){
                val typedQty = it.toString().toDouble()
                val livePrice = typedQty * product.productPrice!!.toDouble()
                tvTotal.show(true)
                if (typedQty > product.productStock!!.toDouble()){
                    tvTotal.text = "No Enough Stock Available!"
                }else{
                    tvTotal.text = "Total : $ " + livePrice.toString()
                }

            }else{
                tvTotal.hide(true)
            }

        }

        btnAdd.setOnClickListener {
            positiveClick.invoke()
            val quantity = tvPdtQty.text.toString().toDoubleOrNull()
            val stock = product.productStock.toString().toDoubleOrNull()!!
            var finalQuantity :Double = 0.00


            if (quantity != null) {
                if (quantity > stock || quantity == 0.0){
                    toast("Not enough Stock Available")

                }else{
                    finalQuantity = quantity.toString().toDoubleOrNull()!!
                    val cart = Cart(
                        productName = product.productName.toString(),
                        productQty = finalQuantity,
                        productImg = product.productImageUrl.toString(),
                        productTotalPrice = tvPdtQty.text.toString().toDoubleOrNull()?.times(product.productPrice!!),
                        productId = product.productId.toString()

                    )
                    viewModel.saveToCart(cart)
                }
            }else{
                toast("Please enter Quantity")
            }

            productDialog.dismiss()
        }
        btnCancel.setOnClickListener {
            negativeClick.invoke()
            productDialog.dismiss()
        }


        productDialog.show()
    }
}
fun Context.showCustomSnackbar(
    view: View,
    duration: Int = Snackbar.LENGTH_INDEFINITE,
    actionText: String? = null,
    //action: () -> Unit = {}
) {
    if (this is Activity && !this.isFinishing) {
        val snackbar = Snackbar.make(view, "", duration)
        val layout = snackbar.view as Snackbar.SnackbarLayout
        val snackView = layoutInflater.inflate(R.layout.snackbar_layout, layout, false)
        layout.addView(snackView, 0)

        val tvErrorTxt: TextView = layout.findViewById(R.id.tv_error_txt)

        tvErrorTxt.text = actionText

        snackbar.show()
    }

}




fun Context.showSimpleAlert(
        title: String,
        msg: String,
        btnMsg: String,
        btnNegative: String,
        onPositiveButtonClick: () -> Unit,
        onNegativeButtonClick: () -> Unit,
    ) {
        val simpleDialog: Dialog?
        if (this is Activity && !this.isFinishing) {
            simpleDialog = Dialog(this, R.style.PopupDialogCustom)
            simpleDialog.setContentView(R.layout.layout_alert)
            simpleDialog.setCanceledOnTouchOutside(false)
            simpleDialog.setCancelable(false)
            simpleDialog.window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val tvTitle: TextView = simpleDialog.findViewById(R.id.tvTitle)
            val tvInfoMsg: TextView = simpleDialog.findViewById(R.id.tvInfoMsg)
            val btnCancel: MaterialButton = simpleDialog.findViewById(R.id.btnCancel)
            val btnOk: MaterialButton = simpleDialog.findViewById(R.id.btnOk)

            tvTitle.text = title
            tvInfoMsg.text = msg
            btnOk.text = btnMsg
            btnCancel.text = btnNegative
            btnCancel.setOnClickListener {
                simpleDialog.dismiss()
                onNegativeButtonClick.invoke()
            }
            btnOk.setOnClickListener {
                simpleDialog.dismiss()
                onPositiveButtonClick.invoke()
            }

            simpleDialog.show()
        }
    }



fun Context.editCart(
    user:String?,
    product: Cart,
    viewModel:CartViewModel,
    positiveClick: () -> Unit,
    negativeClick: () -> Unit
) {
    val productDialog: Dialog?
    if (this is Activity && !this.isFinishing) {
        productDialog = Dialog(this, R.style.PopupDialogCustom)
        productDialog.setContentView(R.layout.edit_cart_layout)
        productDialog.setCanceledOnTouchOutside(true)
        productDialog.setCancelable(false)
        productDialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val itemImage: ImageView = productDialog.findViewById(R.id.iv_cart_pdt)
        val tvPdtName: TextView = productDialog.findViewById(R.id.tv_pdt_cart_name)
        val tvTotalTxt: TextView = productDialog.findViewById(R.id.tv_total_price)
        val tvOwner: TextView = productDialog.findViewById(R.id.tv_owner_name)
        val tvPdtPrice: TextView = productDialog.findViewById(R.id.tv_cart_pdt_price)

        val tvQtyCart: TextView = productDialog.findViewById(R.id.tv_qty_cart)
        val btnAddQty: Button = productDialog.findViewById(R.id.btn_cart_add)
        val btnSubQty: Button = productDialog.findViewById(R.id.btn_cart_sub)
        val btnCancel: Button = productDialog.findViewById(R.id.btn_alert_no)


        itemImage.setImageUrl(product.productImg.toString())
        tvPdtName.text = product.productName.toString()
        tvPdtPrice.text = "Price : $ " + product.productTotalPrice.toString()
        tvOwner.text = user

        tvQtyCart.text = product.productQty.toString()
        product.stockDiffrence = 0.00
        tvTotalTxt.text = "Total : $ " + (product.productQty!! * product.productPrice!!.toDouble()).toString()

        btnAddQty.setOnClickListener {
            val qty = product.productQty.toString().toDouble()
            val finalQty = qty +1
            tvQtyCart.text = finalQty.toString()
            product.productQty = finalQty
            viewModel.addData(product)

            tvTotalTxt.text = "Total : $ " + (finalQty * product.productPrice!!.toDouble()).toString()
        }
        btnSubQty.setOnClickListener {
            val qty = product.productQty.toString().toDouble()
            val finalQty = qty -1
            tvQtyCart.text = finalQty.toString()
            product.productQty = finalQty
            viewModel.subData(product)
            if (finalQty == 0.toDouble()){
                tvTotalTxt.text = "Quantity Cannot be Zero"
            }else
            {
                tvTotalTxt.text = "Total : $ " +(finalQty * product.productPrice!!.toDouble()).toString()
            }

        }


        btnCancel.setOnClickListener {
            negativeClick.invoke()
            productDialog.dismiss()
        }


        productDialog.show()
    }
}

private const val REQUEST_CODE_IMAGE_PICKER = 1

fun Activity.pickImage(onImagePicked: (Uri) -> Unit) {
    ImagePicker.with(this)
        .crop()
        .compress(1024)
        .maxResultSize(1080, 1080)
        .start()
}

//fun Activity.openImagePicker() {
//    val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { result: ActivityResult ->
//        val selectedImage = result.data?.data
//        // do something with the selected image
//    }
//    ImagePicker.with(this)
//        .start()
//}


