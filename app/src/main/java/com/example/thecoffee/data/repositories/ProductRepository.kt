package com.example.thecoffee.data.repositories

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.data.models.Category
import com.example.thecoffee.data.models.Drink
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductRepository(_application: Application) {
    private var application: Application
    private var categoryList: MutableLiveData<ArrayList<Category>>
    private var drinkList: MutableLiveData<ArrayList<Drink>>
    private val _loadingDrinkResult: MutableLiveData<Boolean>
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        application = _application
        categoryList = MutableLiveData<ArrayList<Category>>()
        drinkList = MutableLiveData<ArrayList<Drink>>()
        _loadingDrinkResult = MutableLiveData<Boolean>()
    }

    val getCategoryList: MutableLiveData<ArrayList<Category>>
        get() = categoryList

    val getDrinkList: MutableLiveData<ArrayList<Drink>>
        get() = drinkList

    val loadingDrinkResult: MutableLiveData<Boolean>
        get() = _loadingDrinkResult

    fun getDataCategoryList(){
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
                }
            }.addOnFailureListener { error ->
                Log.d("getDataCategoryList", "Error getDataCategoryList: $error")
            }
    }

    //    suspend fun getDataCategoryList(){
//        withContext(Dispatchers.IO){
//            db.collection("Categories").get()
//                .addOnCompleteListener { task ->
//                    if(task.isSuccessful && task.result != null){
//                        val list = ArrayList<Category>()
//                        for(document in task.result){
//                            val id = document.id
//                            val name = document.getString("name")
//                            val image = document.getString("image")
//                            list.add(Category(id, name, image))
//                        }
//                        categoryList.postValue(list)
//                    }
//                }.addOnFailureListener { error ->
//                    Log.d("getDataCategoryList", "Error getDataCategoryList: $error")
//                }
//        }
//    }
//    suspend fun getDataCategoryList(): List<Category> = withContext(Dispatchers.IO) {
//        val querySnapshot = try {
//            db.collection("Categories").get().await()
//        } catch (e: Exception) {
//            // Ghi lại lỗi
//            Log.d("getDataCategoryList", "Error fetching categories: $e")
//            throw e // Ném lại ngoại lệ để xử lý ở phạm vi gọi
//        }
//
//        val list = ArrayList<Category>()
//        for (document in querySnapshot.documents) {
//            val id = document.id
//            val name = document.getString("name") ?: ""
//            val image = document.getString("image") ?: ""
//            list.add(Category(id, name, image))
//        }
//
//        return@withContext list
//    }

    fun getDataDrink(categoryID: String) {
        _loadingDrinkResult.postValue(true)
        db.collection("Drinks")
            .whereEqualTo("categoryId", categoryID)
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
                    }
                    drinkList.postValue(list)
                }
                _loadingDrinkResult.postValue(false)
            }
    }


    //    fun getDataDrink(categoryID: String) {
//        Log.e("id", categoryID)
//        val drinkRef = db.collection("Drinks")
//        drinkRef.whereEqualTo("categoryId", categoryID)
//            .get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful && task.result != null) {
//                    val list = ArrayList<Drink>()
//                    for (document in task.result) {
//                        val id = document.id
//                        val name = document.getString("name")
//                        val desc = document.getString("desc")
//                        val image = document.getString("image")
//                        val price = document.getString("price")!!.toInt()
//                        val discount = document.getString("discount")!!.toInt()
//                        val categoryId = document.getString("categoryId")
//                        list.add(Drink(id, name, desc, image, price, discount, categoryId))
//                    }
//                    drinkList.postValue(list)
//                }
//            }
//    }
//    suspend fun getDataDrink(): ArrayList<Drink> {
//        val list = ArrayList<Drink>()
//        val drinkRef = db.collection("Drinks")
////    drinkRef.whereEqualTo("categoryId", categoryID)
//            .get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful && task.result != null) {
//
//                    for (document in task.result) {
//                        val id = document.id
//                        val name = document.getString("name")
//                        val desc = document.getString("desc")
//                        val image = document.getString("image")
//                        val price = document.get("price").toString().toInt()
//                        val discount = document.get("discount").toString().toInt()
//                        val categoryId = document.getString("categoryId")
//                        list.add(Drink(id, name, desc, image, price, discount, categoryId))
//                    }
//                    drinkList.postValue(list)
//                }
//            }
//        return  list
//    }


}