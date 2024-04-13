package com.example.thecoffee.order.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.model.Size
import com.example.thecoffee.order.model.Topping
import com.google.firebase.firestore.FirebaseFirestore

class ProductRepository(_application: Application) {
    private var application: Application
    private var categoryList: MutableLiveData<ArrayList<Category>>
    private var drinkList: MutableLiveData<ArrayList<Drink>>
    private var toppingList: MutableLiveData<ArrayList<Topping>>
    private val _loadingDrinkResult: MutableLiveData<Boolean>
    private val _loadingDrinkResultBySale: MutableLiveData<Boolean>
    private val _loadingCategoryResult: MutableLiveData<Boolean>
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        application = _application
        categoryList = MutableLiveData<ArrayList<Category>>()
        drinkList = MutableLiveData<ArrayList<Drink>>()
        toppingList = MutableLiveData<ArrayList<Topping>>()
        _loadingDrinkResult = MutableLiveData<Boolean>()
        _loadingCategoryResult = MutableLiveData<Boolean>()
        _loadingDrinkResultBySale = MutableLiveData<Boolean>()
    }

    val getCategoryList: MutableLiveData<ArrayList<Category>>
        get() = categoryList

    val getDrinkList: MutableLiveData<ArrayList<Drink>>
        get() = drinkList

    val getToppingList: MutableLiveData<ArrayList<Topping>>
        get() = toppingList


    val loadingDrinkResult: MutableLiveData<Boolean>
        get() = _loadingDrinkResult

    val loadingDrinkResultBySale: MutableLiveData<Boolean>
        get() = _loadingDrinkResultBySale
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

    fun getAllDataDrink() {
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
                        val sizeList = document.get("size") as List<Map<String, Number>>?
                        val toppingList = document.get("topping") as List<Map<String, Number>>?

                        val size = mutableListOf<Size>()
                        val topping = mutableListOf<Topping>()
                        if (sizeList != null) {
                            Log.e("sizeList", sizeList.toString())
                            for(item in sizeList){
                                val name = item["name"].toString()
                                val price = item["price"] as? Long
                                Log.e("itemsize", (name to price).toString())
                                size.add(Size(name, price))
                            }
                        }

                        if (toppingList != null){
                            for(item in toppingList){
                                val name = item["name"].toString()
                                val price = item["price"] as? Long
                                Log.e("itemsize", (name to price).toString())
                                topping.add(Topping(name, price))
                            }
                        }

                        list.add(
                            Drink(id, name, desc, image, price, discount,categoryId,
                                size = if(sizeList == null) null else size,
                                topping = if (toppingList == null) null else topping))
                    }
                    drinkList.postValue(list)

                    _loadingDrinkResult.postValue(false)
                }
            }
    }

    fun getDataDrinkBySale() {
        _loadingDrinkResultBySale.postValue(true)
        db.collection("Drinks")
            .whereGreaterThan("discount", 0)
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
                        val sizeList = document.get("size") as List<Map<String, Number>>?
                        val toppingList = document.get("topping") as List<Map<String, Number>>?

                        val size = mutableListOf<Size>()
                        val topping = mutableListOf<Topping>()
                        if (sizeList != null) {
                            for(item in sizeList){
                                val name = item["name"] as? String
                                val price = item["price"] as? Long
                                size.add(Size(name, price))
                            }
                        }

                        if (toppingList != null){
                            for(item in toppingList){
                                val name = item["name"] as? String
                                val price = item["price"] as? Long
                                topping.add(Topping(name, price))
                            }
                        }
                        list.add(Drink(id, name, desc, image, price, discount,categoryId,
                            size = if(sizeList == null) null else size,
                            topping = if (toppingList == null) null else topping))
                    }
                    drinkList.postValue(list)
                    _loadingDrinkResultBySale.postValue(false)
                }

            }
    }

    fun getDataTopping(){
//        db.collection("Toppings").get()
//            .addOnCompleteListener { task ->
//                if(task.isSuccessful && task.result != null){
//                    val list = ArrayList<Topping>()
//                    for(document in task.result){
//                        val id = document.id
//                        val name = document.getString("name")
//                        val price = document.get("price").toString().toInt()
//                        list.add(Topping(id, name, price))
//                    }
//                    toppingList.postValue(list)
//                }
//            }.addOnFailureListener { error ->
//                Log.d("getDataTopping", "Error getDataTopping: $error")
//            }
    }






}