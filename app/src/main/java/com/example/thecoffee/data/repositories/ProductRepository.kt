package com.example.thecoffee.data.repositories

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.data.models.Category
import com.example.thecoffee.data.models.Drink
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductRepository(_application: Application) {
    private var application: Application
    private var categoryList: MutableLiveData<ArrayList<Category>>
    private var drinkList : MutableLiveData<ArrayList<Drink>>
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        application = _application
        categoryList = MutableLiveData<ArrayList<Category>>()
        drinkList = MutableLiveData<ArrayList<Drink>>()
    }

    val getCategoryList : MutableLiveData<ArrayList<Category>>
        get() = categoryList

    val getDrinkList : MutableLiveData<ArrayList<Drink>>
        get() = drinkList

    fun getDataCategoryList(){
        db.collection("Categories").get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful && task.result != null){
                    val list = ArrayList<Category>()
                    for(document in task.result){
                        val id = document.id
                        val name = document.getString("name")
                        list.add(Category(id, name))
                    }
                    categoryList.postValue(list)
                }
            }.addOnFailureListener { error ->
                Log.d("getDataCategoryList", "Error getDataCategoryList: $error")
            }
    }

    fun getDataDrink(categoryID: String){
        val drinkRef = db.collection("Drinks")
        drinkRef.whereEqualTo("categoryId", categoryID)
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful && task.result != null){
                    val list = ArrayList<Drink>()
                    for(document in task.result){
                        val id = document.id
                        val name = document.getString("name")
                        val desc = document.getString("desc")
                        val image = document.getString("image")
                        val price = document.getString("price")!!.toInt()
                        val discount = document.getString("discount")!!.toInt()
                        val categoryId = document.getString("categoryId")
                        list.add(Drink(id, name, desc, image, price, discount, categoryId))
                    }
                    drinkList.postValue(list)
                }
            }
    }
}