package com.example.thecoffee.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thecoffee.models.Drink

class SharedViewModel: ViewModel() {
    private val selectedProduct = MutableLiveData<String>()

    val getSeletedProduct: MutableLiveData<String> get() = selectedProduct

    fun setSelectProduct(product: Drink){
        selectedProduct.value = product.name.toString()
    }

}