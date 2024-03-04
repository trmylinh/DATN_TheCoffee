package com.example.thecoffee.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import androidx.lifecycle.viewModelScope
import com.example.thecoffee.R
import com.example.thecoffee.data.models.Category
import com.example.thecoffee.data.models.Drink
import com.example.thecoffee.data.repositories.ProductRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductViewModel (application: Application) : AndroidViewModel(application) {
    private var repository: ProductRepository
    private var _categoryList = MutableLiveData<ArrayList<Category>>()
    private var _drinkList = MutableLiveData<ArrayList<Drink>>()
//    private val _loadingCategoryResult = MutableLiveData<Boolean>()
    private val _loadingDrinkResult: MutableLiveData<Boolean>
//    val loadingCategoryResult: MutableLiveData<Boolean>
//        get() = _loadingCategoryResult
    val loadingDrinkResult: MutableLiveData<Boolean>
        get() = _loadingDrinkResult

    val getCategoryList : MutableLiveData<ArrayList<Category>>
        get() = _categoryList

    val getDrinkList : MutableLiveData<ArrayList<Drink>>
        get() = _drinkList

    init {
        repository = ProductRepository(application)
        _categoryList = repository.getCategoryList
        _drinkList = repository.getDrinkList
        _loadingDrinkResult = repository.loadingDrinkResult
    }

    fun getDataCategoryList() {
        repository.getDataCategoryList()
    }

    fun getDataDrinkList(categoryId: String){
        repository.getDataDrink(categoryId)
    }

}