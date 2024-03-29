package com.example.thecoffee.order.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.model.Topping
import com.example.thecoffee.order.repository.ProductRepository

class ProductViewModel (application: Application) : AndroidViewModel(application) {
    private var repository: ProductRepository
    private var _categoryList = MutableLiveData<ArrayList<Category>>()
    private var _drinkList = MutableLiveData<ArrayList<Drink>>()
    private var _toppingList = MutableLiveData<ArrayList<Topping>>()
    private val _loadingDrinkResult: MutableLiveData<Boolean>
    private val _loadingCategoryResult: MutableLiveData<Boolean>
    private val selectedProduct: MutableLiveData<String>
    val loadingDrinkResult: MutableLiveData<Boolean>
        get() = _loadingDrinkResult

    val loadingCategoryResult: MutableLiveData<Boolean>
        get() = _loadingCategoryResult

    val getCategoryList : MutableLiveData<ArrayList<Category>>
        get() = _categoryList

    val getDrinkList : MutableLiveData<ArrayList<Drink>>
        get() = _drinkList

    val getToppingList : MutableLiveData<ArrayList<Topping>>
        get() = _toppingList

    init {
        repository = ProductRepository(application)
        _categoryList = repository.getCategoryList
        _drinkList = repository.getDrinkList
        _toppingList = repository.getToppingList
        _loadingDrinkResult = repository.loadingDrinkResult
        _loadingCategoryResult = repository.loadingCategoryResult
        selectedProduct = MutableLiveData<String>()
    }

    fun getDataCategoryList() {
        repository.getDataCategoryList()
    }

    fun getDataDrinkList(){
        repository.getAllDataDrink()
    }

    fun getDataToppingList(){
        repository.getDataTopping()
    }

    fun getDataDrinkBySale(){
        repository.getDataDrinkBySale()
    }



}