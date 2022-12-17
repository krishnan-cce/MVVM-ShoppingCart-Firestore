package com.example.firebasemvvm.ui.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasemvvm.data.model.cart.Cart
import com.example.firebasemvvm.data.model.total.Total
import com.example.firebasemvvm.databinding.ActivityCartBinding
import com.example.firebasemvvm.di.FirebaseModule
import com.example.firebasemvvm.di.FirebaseModule.toast
import com.example.firebasemvvm.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter



    var total: Double = 0.0
    var subTotal: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cartViewModel.getCart()

        setupRvCart()
        observeMessage()
        setUpCartDisplay()



        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val cartProduct = cartAdapter.cartList[position]
                showAlert(
                    title = "Are you sure...!",
                    titleDesc = "Delete " + cartProduct.productName + " From Cart?",
                    negativeClick = {
                        cartViewModel.deleteFromCart(cartProduct)
                        toast("${cartProduct.productName} deleted Succesfully ")
                    },
                    positiveClick = {
                        setUpCartDisplay()
                    }

                )
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvCart)
        }



        binding.cartTotal.checkoutBtn.setOnClickListener {
            val total = Total(
                userId = "",
                totalId = "",
                productId = null,
                total = total,
                subTotal = subTotal
            )
            cartViewModel.addToTotal(total)

        }



    }

    fun setUpCartDisplay(){
        cartViewModel.cartProducts.observe(this, Observer { response ->
            cartAdapter.setData(response as List<Cart>)
            for(item in response){
                val availableQuantity = item.productQty!!.toInt()
                if (availableQuantity > 0) {
                    val price = item.productTotalPrice!!.toDouble()
                    total += price
                    subTotal =  0.12 * price  + total //Including 12%vGST
                    binding.cartTotal.tvTotal.text = "TOTAL : " + "$" + total.toString()
                    binding.cartTotal.tvSubTotal.text = "SUB TOTAL : " + "$" + subTotal
                }

            }

        })
    }

    fun setupRvCart(){
        cartAdapter = CartAdapter(OnAddClickListener { item: Cart, _: Int ->
//            val qty = item.productQty.toString().toDouble()
//            val finalQty = qty +1
//            item.productQty = finalQty
//            cartViewModel.addData(item)
        },
            OnSubClickListener{ item:Cart,_:Int ->
//                val qty = item.productQty.toString().toDouble()
//                val finalQty = qty -1
//                item.productQty = finalQty
//                cartViewModel.subData(item)
            },
            onItemSelectListener { item:Cart,_:Int ->
                editCart(
                    user = SessionManager.userName,
                    product = item,
                    viewModel = cartViewModel,
                    positiveClick = {

                    },
                    negativeClick = {

                    }
                )
            })

        binding.rvCart.apply {
            adapter = cartAdapter

        }




    }

    fun observeMessage(){
        cartViewModel.getErrorMsg().observe(this,Observer{response ->
            when(response) {
                is Network.Success -> {
                    response.data?.let { msg ->
                        toast(msg)

                    }
                }
                is Network.Error -> {
                    response.message?.let { message ->
                        //toast(message)



//                        showCustomSnackbar(
//                            view = binding.root,
//                            duration = 1500,
//                            actionText = message
//                        )

                    }
                }
                is Network.Loading -> {
                }
                else -> {}
            }

        })
    }


}