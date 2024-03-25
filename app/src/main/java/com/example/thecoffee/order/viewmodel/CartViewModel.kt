package com.example.thecoffee.order.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.thecoffee.order.model.Cart
import com.example.thecoffee.order.repository.CartRepository

class CartViewModel (application: Application) : AndroidViewModel(application) {
    private var repository: CartRepository
    init {
        repository = CartRepository(application)
    }

    fun addToCart(cart: Cart){
        repository.addToCart(cart)
    }

}