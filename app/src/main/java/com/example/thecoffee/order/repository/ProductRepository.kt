package com.example.thecoffee.order.repository

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.order.model.Category
import com.example.thecoffee.order.model.Drink
import com.example.thecoffee.order.model.Size
import com.example.thecoffee.order.model.Topping
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProductRepository(_application: Application) {
    private var application: Application = _application
    private var categoryList: MutableLiveData<ArrayList<Category>> =
        MutableLiveData<ArrayList<Category>>()
    private var drinkList: MutableLiveData<ArrayList<Drink>> = MutableLiveData<ArrayList<Drink>>()
    private var drinkListBySale: MutableLiveData<ArrayList<Drink>> = MutableLiveData<ArrayList<Drink>>()
    private var drinkListByCategory: MutableLiveData<ArrayList<Drink>> = MutableLiveData<ArrayList<Drink>>()
    private var toppingList: MutableLiveData<ArrayList<Topping>> =
        MutableLiveData<ArrayList<Topping>>()
    private val _loadingDrinkResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingDrinkByCategoryResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingAddDrinkResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingDrinkResultBySale: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingCategoryResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingAddCategoryResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingUpdatedData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingDeleteData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var storageRef: StorageReference = FirebaseStorage.getInstance().reference

    val getCategoryList: MutableLiveData<ArrayList<Category>>
        get() = categoryList

    val getDrinkList: MutableLiveData<ArrayList<Drink>>
        get() = drinkList

    val getDrinkListBySale: MutableLiveData<ArrayList<Drink>>
        get() = drinkListBySale

    val getDrinkListByCategory: MutableLiveData<ArrayList<Drink>>
        get() = drinkListByCategory

    val getToppingList: MutableLiveData<ArrayList<Topping>>
        get() = toppingList


    val loadingDrinkResult: MutableLiveData<Boolean>
        get() = _loadingDrinkResult

    val loadingDrinkByCategoryResult: MutableLiveData<Boolean>
        get() = _loadingDrinkByCategoryResult

    val loadingAddDrinkResult: MutableLiveData<Boolean>
        get() = _loadingAddDrinkResult

    val loadingDrinkResultBySale: MutableLiveData<Boolean>
        get() = _loadingDrinkResultBySale
    val loadingCategoryResult: MutableLiveData<Boolean>
        get() = _loadingCategoryResult

    val loadingAddCategoryResult: MutableLiveData<Boolean>
        get() = _loadingAddCategoryResult

    val loadingUpdatedData: MutableLiveData<Boolean>
        get() = _loadingUpdatedData

    val loadingDeleteData: MutableLiveData<Boolean>
        get() = _loadingDeleteData

    fun getDataCategoryList() {
        _loadingCategoryResult.postValue(true)
        db.collection("Categories").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val list = ArrayList<Category>()
                    for(document in task.result){
                        val categoryId = document.getString("categoryId")
                        val name = document.getString("name")
                        val image = document.getString("image")
                        list.add(Category(categoryId, name, image))
                    }
                    categoryList.postValue(list)
                }
                _loadingCategoryResult.postValue(false)
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
                        val drinkId = document.getString("drinkId")
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
                            for (item in sizeList) {
                                val name = item["name"].toString()
                                val price = item["price"] as? Long
                                Log.e("itemsize", (name to price).toString())
                                size.add(Size(name, price))
                            }
                        }

                        if (toppingList != null) {
                            for (item in toppingList) {
                                val name = item["name"].toString()
                                val price = item["price"] as? Long
                                Log.e("itemsize", (name to price).toString())
                                topping.add(Topping(name, price))
                            }
                        }

                        list.add(
                            Drink(
                                drinkId, name, desc, image, price, discount, categoryId,
                                size = if (sizeList == null) null else size,
                                topping = if (toppingList == null) null else topping
                            )
                        )
                    }
                    drinkList.postValue(list)
                }
                _loadingDrinkResult.postValue(false)
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
                        val drinkId = document.getString("drinkId")
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
                            for (item in sizeList) {
                                val name = item["name"] as? String
                                val price = item["price"] as? Long
                                size.add(Size(name, price))
                            }
                        }

                        if (toppingList != null) {
                            for (item in toppingList) {
                                val name = item["name"] as? String
                                val price = item["price"] as? Long
                                topping.add(Topping(name, price))
                            }
                        }
                        list.add(
                            Drink(
                                drinkId, name, desc, image, price, discount, categoryId,
                                size = if (sizeList == null) null else size,
                                topping = if (toppingList == null) null else topping
                            )
                        )
                    }
                    drinkListBySale.postValue(list)

                }
                _loadingDrinkResultBySale.postValue(false)
            }
    }

    fun getDrinkByCategory(categoryID: String){
        _loadingDrinkByCategoryResult.postValue(false)
        db.collection("Drinks")
            .whereEqualTo("categoryId", categoryID)
            .get()
            .addOnCompleteListener {task ->
                if (task.isSuccessful && task.result != null) {
                    val list = ArrayList<Drink>()
                    for (document in task.result) {
                        val drinkId = document.getString("drinkId")
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
                            for (item in sizeList) {
                                val name = item["name"] as? String
                                val price = item["price"] as? Long
                                size.add(Size(name, price))
                            }
                        }

                        if (toppingList != null) {
                            for (item in toppingList) {
                                val name = item["name"] as? String
                                val price = item["price"] as? Long
                                topping.add(Topping(name, price))
                            }
                        }
                        list.add(
                            Drink(
                                drinkId, name, desc, image, price, discount, categoryId,
                                size = if (sizeList == null) null else size,
                                topping = if (toppingList == null) null else topping
                            )
                        )
                    }
                    drinkListByCategory.postValue(list)

                }
                _loadingDrinkByCategoryResult.postValue(true)
            }
    }

    fun updateDataDrink(idDrink: String, newItem: Drink) {
        _loadingUpdatedData.postValue(true)
        db.collection("Drinks")
            .whereEqualTo("drinkId", idDrink)
            .get()
            .addOnSuccessListener { documents->
                for (document in documents) {
                    db.collection("Drinks").document(document.id).set(newItem)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    application,
                                    "update data successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                                Log.e("Firestore", "Error update data", it.exception)
                            }
                            _loadingUpdatedData.postValue(false)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.d("Query", "Error getting documents: ", e)
            }
    }

    fun deleteDataDrink(idDrink: String) {
        _loadingDeleteData.postValue(true)
        db.collection("Drinks")
            .whereEqualTo("drinkId", idDrink)
            .get()
            .addOnSuccessListener {documents ->
                for (document in documents) {
                    db.collection("Drinks").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("Delete", "DocumentSnapshot successfully deleted!")
                        }
                        .addOnFailureListener {e ->
                            Log.w("Delete", "Error deleting document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(application, "Error deleting document!", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                _loadingDeleteData.postValue(false)
            }
    }

    // create new category
    fun createCategory(category: Category) {
        _loadingAddCategoryResult.postValue(true)
        val imageRef = storageRef.child("category_image/${category.name}.png")
        imageRef.putFile(category.image!!.toUri()).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                db.collection("Categories").add(
                    Category(
                        category.categoryId,
                        category.name,
                        uri.toString()
                    )
                )
                    .addOnSuccessListener {
                        Toast.makeText(application, "Document added!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error adding document", e)
                    }
                    .addOnCompleteListener {
                        _loadingAddCategoryResult.postValue(false)
                    }
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Error download url image", e)
            }
        }
    }

    fun createDrink(drink: Drink) {
        _loadingAddDrinkResult.postValue(true)
        val imageRef = storageRef.child("product_image/${drink.name}.png")
        imageRef.putFile(drink.image!!.toUri()).addOnSuccessListener{taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener{uri ->
                db.collection("Drinks").add(Drink(
                    drink.drinkId,
                    drink.name,
                    drink.desc,
                    uri.toString(),
                    drink.price,
                    drink.discount,
                    drink.categoryId,
                    drink.size,
                    drink.topping
                ))
                    .addOnSuccessListener {
                        Toast.makeText(application, "Document added!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error adding document", e)
                    }
                    .addOnCompleteListener {
                        _loadingAddDrinkResult.postValue(false)
                    }
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Error download url image", e)
            }
        }


    }


}