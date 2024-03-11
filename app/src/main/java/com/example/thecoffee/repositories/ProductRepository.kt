package com.example.thecoffee.repositories

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.models.Category
import com.example.thecoffee.models.Drink
import com.google.firebase.firestore.FirebaseFirestore

class ProductRepository(_application: Application) {
    private var application: Application
    private var categoryList: MutableLiveData<ArrayList<Category>>
    private var drinkList: MutableLiveData<ArrayList<Drink>>
    private val _loadingDrinkResult: MutableLiveData<Boolean>
    private val _loadingCategoryResult: MutableLiveData<Boolean>
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        application = _application
        categoryList = MutableLiveData<ArrayList<Category>>()
        drinkList = MutableLiveData<ArrayList<Drink>>()
        _loadingDrinkResult = MutableLiveData<Boolean>()
        _loadingCategoryResult = MutableLiveData<Boolean>()
    }

    val getCategoryList: MutableLiveData<ArrayList<Category>>
        get() = categoryList

    val getDrinkList: MutableLiveData<ArrayList<Drink>>
        get() = drinkList

    val loadingDrinkResult: MutableLiveData<Boolean>
        get() = _loadingDrinkResult
    val loadingCategoryResult: MutableLiveData<Boolean>
        get() = _loadingCategoryResult

    fun getDataCategoryList(){
        _loadingCategoryResult.postValue(true)
        db.collection("Categories").get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful && task.result != null){
                    val list = ArrayList<Category>()
                    for(document in task.result){
                        val id = document.id
                        val name = document.getString("name")
                        val image = document.getString("image")
                        list.add(Category(id, name, image))
                    }
                    categoryList.postValue(list)

                    _loadingCategoryResult.postValue(false)
                }
            }.addOnFailureListener { error ->
                Log.d("getDataCategoryList", "Error getDataCategoryList: $error")
            }
    }

    fun getDataDrink() {
        _loadingDrinkResult.postValue(true)
        db.collection("Drinks")
//            .whereEqualTo("categoryId", categoryID)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val list = ArrayList<Drink>()
                    for (document in task.result) {
                        val id = document.id
                        val name = document.getString("name")
                        val desc = document.getString("desc")
                        val image = document.getString("image")
                        val price = document.get("price").toString().toInt()
                        val discount = document.get("discount").toString().toInt()
                        val categoryId = document.getString("categoryId")
                        list.add(Drink(id, name, desc, image, price, discount, categoryId))
                        Log.e("list", list.toString())
                    }
                    drinkList.postValue(list)

                    _loadingDrinkResult.postValue(false)
                }
            }
    }




}