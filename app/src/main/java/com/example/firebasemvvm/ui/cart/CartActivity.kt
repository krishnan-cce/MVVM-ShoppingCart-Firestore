package com.example.firebasemvvm.ui.cart

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasemvvm.data.model.cart.Cart
import com.example.firebasemvvm.data.model.total.Total
import com.example.firebasemvvm.databinding.ActivityCartBinding
import com.example.firebasemvvm.ui.auth.LoginActivity
import com.example.firebasemvvm.ui.fragments.MyProductsActivity

import com.example.firebasemvvm.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter




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



        binding.tvCheckOut.setOnClickListener {
            val total = Total(
                userId = "",
                totalId = "",
                productId = cartAdapter.getProductIds(),
                total = 0.0,
                subTotal = subTotal
            )
            cartViewModel.addToTotal(total)

        }

        cartViewModel.getClickOptions().observe(this) {
            when (it) {
                //1 -> finish()
                1 -> {
                    openActivity<MyProductsActivity> { }
                    toast("Clicked")
                }

            }
        }

    }


    fun setUpCartDisplay(){
        cartViewModel.cartProducts.observe(this, Observer { response ->
            cartAdapter.setData(response as List<Cart>)
            cartAdapter.notifyDataSetChanged()


            if(cartAdapter.itemCount > 0){
                binding.constraintCart.show(true)
                binding.animationView.hide(true)
                binding.tvEmptyMsg.hide(true)
                binding.btnAction.hide(true)
            }

            var totalPrice = 0.00
                for (i in 0 until cartAdapter.itemCount) {
                    val item = cartAdapter.getItem(i)
                    if (item.productTotalPrice != null) {
                        totalPrice += item.productTotalPrice!!
                        //checkOutProducts = item.productId
                        subTotal =  (0.12 * totalPrice)  + totalPrice //Including 12%vGST
                        binding.tvTotal.text = "Price : " + "$" + totalPrice
                        binding.tvTotalItems.text = cartAdapter.itemCount.toString() + " items "
                    }
                }


        })

        if(cartAdapter.itemCount == 0){
            binding.constraintCart.hide(true)
            binding.animationView.show(true)
            binding.tvEmptyMsg.show(true)

        }

    }

    fun setupRvCart(){
        cartAdapter = CartAdapter(OnAddClickListener { item: Cart, _: Int ->
        },
            OnSubClickListener{ item:Cart,_:Int ->
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
            layoutManager =  LinearLayoutManager(applicationContext)



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
                        toast(message)
                        //showCustomSnackbar(view = binding.root, duration = 1500, actionText = message)
                    }
                }
                is Network.Loading -> {
                }
                else -> {}
            }

        })
    }


}