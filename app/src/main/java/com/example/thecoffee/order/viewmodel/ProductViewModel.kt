package com.example.thecoffee.order.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.model.Topping
import com.example.thecoffee.order.repository.ProductRepository

class ProductViewModel (application: Application) : AndroidViewModel(application) {
    private var repository: ProductRepository = ProductRepository(application)
    private var _categoryList = MutableLiveData<ArrayList<Category>>()
    private var _drinkList = MutableLiveData<ArrayList<Drink>>()
    private var _drinkListBySale: MutableLiveData<ArrayList<Drink>> = MutableLiveData<ArrayList<Drink>>()
    private var _drinkListByCategory: MutableLiveData<ArrayList<Drink>> = MutableLiveData<ArrayList<Drink>>()
    private var _toppingList = MutableLiveData<ArrayList<Topping>>()
    private val _loadingDrinkResult: MutableLiveData<Boolean> = repository.loadingDrinkResult
    private val _loadingDrinkByCategoryResult: MutableLiveData<Boolean> = repository.loadingDrinkByCategoryResult
    private val _loadingAddDrinkResult: MutableLiveData<Boolean> = repository.loadingAddDrinkResult
    private val _loadingCategoryResult: MutableLiveData<Boolean> = repository.loadingCategoryResult
    private val _loadingAddCategoryResult: MutableLiveData<Boolean> = repository.loadingAddCategoryResult
    private val selectedProduct: MutableLiveData<String>

    private val _loadingUpdatedData: MutableLiveData<Boolean> = repository.loadingUpdatedData
    private val _loadingDeleteData: MutableLiveData<Boolean> = repository.loadingDeleteData

    private val _messageDeleteDrink: MutableLiveData<String> = repository.getMessageDeleteDrink
    private val _messageCreateDrink: MutableLiveData<String> = repository.getMessageCreateDrink

    val loadingDeleteData: MutableLiveData<Boolean>
        get() =  _loadingDeleteData

    val loadingUpdatedData: MutableLiveData<Boolean>
        get() =  _loadingUpdatedData

    val loadingDrinkResult: MutableLiveData<Boolean>
        get() = _loadingDrinkResult

    val loadingDrinkByCategoryResult: MutableLiveData<Boolean>
        get() = _loadingDrinkByCategoryResult

    val loadingAddDrinkResult: MutableLiveData<Boolean>
        get() = _loadingAddDrinkResult

    val loadingCategoryResult: MutableLiveData<Boolean>
        get() = _loadingCategoryResult

    val loadingAddCategoryResult: MutableLiveData<Boolean>
        get() = _loadingAddCategoryResult

    val getCategoryList : MutableLiveData<ArrayList<Category>>
        get() = _categoryList

    val getDrinkList : MutableLiveData<ArrayList<Drink>>
        get() = _drinkList

    val getDrinkListBySale: MutableLiveData<ArrayList<Drink>>
        get() = _drinkListBySale

    val getDrinkListByCategory: MutableLiveData<ArrayList<Drink>>
        get() = _drinkListByCategory

    val getToppingList : MutableLiveData<ArrayList<Topping>>
        get() = _toppingList

    val getMessageDeleteDrink: MutableLiveData<String>
        get() = _messageDeleteDrink

    val getMessageCreateDrink: MutableLiveData<String>
        get() = _messageCreateDrink

    init {
        _categoryList = repository.getCategoryList
        _drinkList = repository.getDrinkList
        _drinkListBySale = repository.getDrinkListBySale
        _drinkListByCategory = repository.getDrinkListByCategory
        _toppingList = repository.getToppingList
        selectedProduct = MutableLiveData<String>()
    }

    fun getDataCategoryList() {
        repository.getDataCategoryList()
    }

    fun getDataDrinkList(){
        repository.getAllDataDrink()
    }


    fun getDataDrinkBySale(){
        repository.getDataDrinkBySale()
    }

    fun updateDataDrink(idDrink: String, newItem: Drink){
        repository.updateDataDrink(idDrink, newItem)
    }

    fun deleteDataDrink(idDrink: String){
        repository.deleteDataDrink(idDrink)
    }

    fun createCategory(category: Category){
        repository.createCategory(category)
    }

    fun createDrink(drink: Drink){
        repository.createDrink(drink)
    }

    fun getDrinkByCategory(idCategory: String){
        repository.getDrinkByCategory(idCategory)
    }



}