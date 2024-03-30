package com.example.thecoffee.order.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.example.thecoffee.order.model.Cart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CartRepository(_application: Application) {
    private var application: Application
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    init {
        application = _application
    }


    // add cart to firestore database
    fun addToCart(cart: Cart){
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val cartRef = db.collection("Carts").document(userId)
            cartRef.get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val document = task.result

                    val cartList: List<Map<String, Any>> = mutableListOf()
                    val cartItem = hashMapOf(
                        "name" to cart.drinkName!!,
                        "size" to cart.drinkSize!!,
                        "topping" to cart.drinkTopping!!,
                        "note" to cart.note!!,
                        "quantity" to cart.quantity!!,
                        "price" to cart.totalPrice!!
                    )
                    (cartList as MutableList).add(cartItem)

                    if(document != null && document.exists()){
                        // cart cua user da ton tai -> update
                        val cartData = document.get("cart") as? List<Map<String, Any>>
                        (cartData as MutableList).add(cartItem)

                        val updateCart = hashMapOf(
                            "cart" to cartData!!
                        )
                        cartRef.set(updateCart, SetOptions.merge()).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    application,
                                    "update cart successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                                Log.e("Firestore", "Error update cart", it.exception)
                            }
                        }
                    } else {
                        val data =  hashMapOf<String, List<Map<String, Any>>>(
                            "cart" to cartList
                        )
                        cartRef.set(data).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    application,
                                    "add cart successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                                Log.e("Firestore", "Error setting cart", it.exception)
                            }
                        }
                    }
                }
            }
        }
    }
}