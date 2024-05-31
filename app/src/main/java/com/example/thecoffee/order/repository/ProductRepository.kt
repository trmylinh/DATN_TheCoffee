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

    private val messageDeleteDrink: MutableLiveData<String> = MutableLiveData<String>()
    private val messageCreateDrink: MutableLiveData<String> = MutableLiveData<String>()
    private val messageUpdateDrink: MutableLiveData<String> = MutableLiveData<String>()

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

    val loadingUpdateData: MutableLiveData<Boolean>
        get() = _loadingUpdatedData

    val getMessageDeleteDrink: MutableLiveData<String>
        get() = messageDeleteDrink

    val getMessageCreateDrink: MutableLiveData<String>
        get() = messageCreateDrink

    val getMessageUpdateDrink: MutableLiveData<String>
        get() = messageUpdateDrink

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
                        val isOutOfStock = document.getBoolean("outOfStock")
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
                                drinkId, name, desc, image, price, discount, categoryId, isOutOfStock,
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
                        val isOutOfStock = document.getBoolean("outOfStock")

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
                                drinkId, name, desc, image, price, discount, categoryId, isOutOfStock,
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
                        val isOutOfStock = document.getBoolean("outOfStock")

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
                                drinkId, name, desc, image, price, discount, categoryId, isOutOfStock,
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
                        .addOnSuccessListener {
                            Log.d("Update", "Drink successfully Update!")
                            messageUpdateDrink.postValue("Cập nhật sản phẩm thành công!!!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Update", "Lỗi cập nhật sản phẩm!!!", e)
                            messageUpdateDrink.postValue("Error Update Drink: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Update", "Error Drink voucher", e)
            }
            .addOnCompleteListener {
                _loadingUpdatedData.postValue(false)
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
                            Log.d("Delete", "Drink successfully deleted!")
                            messageDeleteDrink.postValue("Xóa sản phẩm thành công!!!")
                        }
                        .addOnFailureListener {e ->
                            Log.w("Delete", "Error deleting drink", e)
                            messageDeleteDrink.postValue("Lỗi xóa sản phẩm: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Delete", "Error deleting document", e)
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
                        Log.d("Firestore", "Document added!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error adding document", e)
                    }
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Error download url image", e)
            }
                .addOnCompleteListener {
                    _loadingAddCategoryResult.postValue(false)
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
                    0,
                    drink.categoryId,
                    false,
                    drink.size,
                    drink.topping
                ))
                    .addOnSuccessListener {
                        messageCreateDrink.postValue("Tạo sản phẩm mới thành công!!!")
                        Log.d("Firestore", "drink added!")
                    }
                    .addOnFailureListener { e ->
                        messageCreateDrink.postValue("Lỗi tạo sản phẩm mới: ${e.message}")
                        Log.e("Firestore", "Error adding dribk", e)
                    }
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Error download url image", e)
            }.addOnCompleteListener {
                _loadingAddDrinkResult.postValue(false)
            }
        }


    }


}