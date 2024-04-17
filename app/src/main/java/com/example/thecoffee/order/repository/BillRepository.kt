package com.example.thecoffee.order.repository

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.invalidateGroupsWithKey
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.model.Cart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class BillRepository(_application: Application) {
    private var application: Application
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _loadingResult: MutableLiveData<Boolean>
    init {
        application = _application
        _loadingResult = MutableLiveData<Boolean>()
    }

    val loadingResult: MutableLiveData<Boolean>
        get() = _loadingResult

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


    fun order(bill: Bill){
        _loadingResult.postValue(true)
        val userId = auth.currentUser?.uid
        val billRef = db.collection("Bills").document(userId!!)
        billRef.get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val document = task.result
                val bills: List<Map<String, Any>> = mutableListOf()

                val itemBill = hashMapOf(
                    "id" to bill.id!!,
                    "userId" to bill.userId!!,
                    "address" to bill.address!!,
                    "drinks" to bill.drinks!!,
                    "status" to bill.status!!,
                    "shipFee" to bill.shipFee!!,
                    "time" to bill.time!!
                )

                (bills as MutableList).add(itemBill)

                if(document != null && document.exists()){
                    // cart cua user da ton tai -> update
                    val billData = document.get("bill") as? List<Map<String, Any>>
                    (billData as MutableList).add(itemBill)

                    val updateBill = hashMapOf(
                        "bill" to billData!!
                    )
                    billRef.set(updateBill, SetOptions.merge()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                application,
                                "update bill successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                            Log.e("Firestore", "Error update bill", it.exception)
                        }
                    }
                } else {
                    val data =  hashMapOf<String, List<Map<String, Any>>>(
                        "bill" to bills
                    )
                    billRef.set(data).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                application,
                                "add bill successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            _loadingResult.postValue(false)
                        } else {
                            Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                            Log.e("Firestore", "Error setting bill", it.exception)
                        }
                    }
                }
            }
            _loadingResult.postValue(false)
        }

    }
}