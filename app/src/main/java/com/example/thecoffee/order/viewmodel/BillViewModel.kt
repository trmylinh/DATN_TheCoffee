package com.example.thecoffee.order.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.model.Cart
import com.example.thecoffee.order.repository.BillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BillViewModel (application: Application) : AndroidViewModel(application) {
    private var repository: BillRepository
    private val _loadingResult: MutableLiveData<Boolean>

    init {
        repository = BillRepository(application)
        _loadingResult = repository.loadingResult
    }

    val loadingResult: MutableLiveData<Boolean>
    get() = _loadingResult

    fun addToCart(cart: Cart){
        repository.addToCart(cart)
    }

    fun order(bill: Bill) {
//        _loadingResult.postValue(true)
        repository.order(bill)
//        _loadingResult.postValue(false)
    }

}