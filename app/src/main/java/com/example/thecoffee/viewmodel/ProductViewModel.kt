package com.example.thecoffee.viewmodel

import android.util.Log
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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductViewModel : ViewModel() {
    private var db = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()

    var categoryItems = MutableLiveData<List<Category>>()
    var drinkItems = MutableLiveData<List<Drink>>()

    suspend fun fetchCategoryItemsFromFirestore(): CompletableDeferred<List<Category>> {
        val deferred = CompletableDeferred<List<Category>>()
        db.collection("Categories").get()
            .addOnSuccessListener {
                val categories = it.documents.map { item ->
                    Log.e("name", item.data?.get("name").toString())
                    Category(
                        item.data?.get("id").toString(),
                        item.data?.get("name").toString(),
                        R.drawable.img
                    )
                }
                Log.e("categories", categories[0].name)
                categoryItems.value = categories
                deferred.complete(categories)
            }
            .addOnFailureListener {
                Log.e("products view model - category list", it.message.toString())
            }
        return deferred
    }

    suspend fun loadAllCategoryItems(): LiveData<List<Category>> {
        return withContext(Dispatchers.IO) {
            val deferred = fetchCategoryItemsFromFirestore()
            deferred.await()
            categoryItems
        }
    }

    fun fetchDrinkItemsFromFirestore(): CompletableDeferred<List<Drink>> {
        val deferred = CompletableDeferred<List<Drink>>()
        db.collection("Drinks").get()
            .addOnSuccessListener {
                val drinks = it.documents.map { item ->
                    Drink(
                        item.data?.get("id").toString(),
                        item.data?.get("name").toString(),
                        item.data?.get("desc").toString(),
                        R.drawable.img,
                        item.data?.get("price").toString().toInt(),
                        item.data?.get("discount").toString().toInt(),
                        item.data?.get("categoryId").toString()
                    )
                }
                drinkItems.value = drinks
                deferred.complete(drinks)
            }
            .addOnFailureListener {
                Log.e("products view model - drink list", it.message.toString())
            }
        return deferred
    }

    suspend fun loadAllDrinkItems(): MutableLiveData<List<Drink>> {
        return withContext(Dispatchers.IO) {
            val deferred = fetchDrinkItemsFromFirestore()
            deferred.await()
            drinkItems
        }
    }


}