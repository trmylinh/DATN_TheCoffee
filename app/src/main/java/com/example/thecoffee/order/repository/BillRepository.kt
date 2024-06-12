package com.example.thecoffee.order.repository

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.invalidateGroupsWithKey
import androidx.lifecycle.MutableLiveData
import androidx.transition.Transition.MatchOrder
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.model.Cart
import com.example.thecoffee.order.model.Drink
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Arrays
import kotlin.reflect.typeOf

class BillRepository(_application: Application) {
    private var application: Application = _application
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _loadingResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingBillsUserResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingBillsResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingBillUserByIdResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingUpdateStatusBillResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var billsUser: MutableLiveData<ArrayList<Bill>> = MutableLiveData<ArrayList<Bill>>()
    private var bills: MutableLiveData<ArrayList<Bill>> =
        MutableLiveData<ArrayList<Bill>>() // admin
    private var billUserById: MutableLiveData<Bill> = MutableLiveData<Bill>()

    private val messageUpdateBill: MutableLiveData<String> = MutableLiveData<String>()

    val loadingResult: MutableLiveData<Boolean>
        get() = _loadingResult

    val loadingBillsUserResult: MutableLiveData<Boolean>
        get() = _loadingBillsUserResult

    val loadingBillsResult: MutableLiveData<Boolean>
        get() = _loadingBillsResult

    val loadingBillUserByIdResult: MutableLiveData<Boolean>
        get() = _loadingBillUserByIdResult

    val loadingUpdateStatusBillResult: MutableLiveData<Boolean>
        get() = _loadingUpdateStatusBillResult

    val getBillsUser: MutableLiveData<ArrayList<Bill>>
        get() = billsUser

    val getBills: MutableLiveData<ArrayList<Bill>>
        get() = bills

    val getBillUserById: MutableLiveData<Bill>
        get() = billUserById

    val getMessageUpdateBill: MutableLiveData<String>
        get() = messageUpdateBill

    // add cart to firestore database
    fun addToCart(cart: Cart){
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val cartRef = db.collection("Carts").document(userId)
            cartRef.get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val document = task.result

                    val cartList: List<Map<String, Any>> = mutableListOf()
                    val cartItem = hashMapOf(
                        "name" to cart.drinkName!!,
                        "size" to cart.drinkSize!!,
                        "topping" to cart.drinkTopping!!,
                        "note" to cart.note!!,
                        "quantity" to cart.quantity!!,
                        "price" to cart.totalPrice!!
                    )
                    (cartList as MutableList).add(cartItem)

                    if(document != null && document.exists()){
                        // cart cua user da ton tai -> update
                        val cartData = document.get("cart") as? List<Map<String, Any>>
                        (cartData as MutableList).add(cartItem)

                        val updateCart = hashMapOf(
                            "cart" to cartData!!
                        )
                        cartRef.set(updateCart, SetOptions.merge()).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    application,
                                    "update cart successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                                Log.e("Firestore", "Error update cart", it.exception)
                            }
                        }
                    } else {
                        val data =  hashMapOf<String, List<Map<String, Any>>>(
                            "cart" to cartList
                        )
                        cartRef.set(data).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    application,
                                    "add cart successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                                Log.e("Firestore", "Error setting cart", it.exception)
                            }
                        }
                    }
                }
            }
        }
    }

    fun order(bill: Bill){
        _loadingResult.postValue(true)
        val userId = auth.currentUser?.uid
        val billRef = db.collection("Bills").document(userId!!)
        billRef.get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val document = task.result
                val bills: List<Map<String, Any>> = mutableListOf()

                val itemBill = hashMapOf(
                    "billId" to bill.billId!!,
                    "userId" to bill.userId!!,
                    "address" to bill.address!!,
                    "drinks" to bill.drinks!!,
                    "status" to bill.status!!,
                    "userReceiver" to bill.userReceiver!!,
                    "phoneReceiver" to bill.phoneReceiver!!,
                    "shipFee" to bill.shipFee!!,
                    "time" to bill.time!!
                )

                (bills as MutableList).add(itemBill)

                if(document != null && document.exists()){
                    // cart cua user da ton tai -> update
                    val billData = document.get("bill") as? List<Map<String, Any>>
                    (billData as MutableList).add(itemBill)

                    val updateBill = hashMapOf(
                        "bill" to billData!!
                    )
                    billRef.set(updateBill, SetOptions.merge()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                application,
                                "update bill successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                            Log.e("Firestore", "Error update bill", it.exception)
                        }
                    }
                } else {
                    val data =  hashMapOf<String, List<Map<String, Any>>>(
                        "bill" to bills
                    )
                    billRef.set(data).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                application,
                                "add bill successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            _loadingResult.postValue(false)
                        } else {
                            Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                            Log.e("Firestore", "Error setting bill", it.exception)
                        }
                    }
                }
            }
            _loadingResult.postValue(false)
        }

    }

    // tat ca bill cua user
    fun getAllBillsByUser(){
        _loadingBillsUserResult.postValue(true)
        db.collection("Bills").document(auth.currentUser!!.uid)
            .get().addOnCompleteListener { task ->
                if(task.isSuccessful && task.result != null){
                    val bill = ArrayList<Bill>()
                    val data = task.result.data?.get("bill") as List<Map<*, *>>?
                    data?.forEach{ element ->
                        val billId = element["billId"] as String
                        val userId = element["userId"] as String
                        val address = element["address"] as String
                        val status = element["status"] as Long
                        val shipFee = element["shipFee"] as Long
                        val time = element["time"] as String
                        val drinks = element["drinks"]  as List<*>
                        val userReceiver = element["userReceiver"] as String
                        val phoneReceiver = element["phoneReceiver"] as String

                        val drink = mutableListOf<Cart>()
                        drinks.forEach { element ->
                            val item = element as Map<*,*>
                            val quantity = item["quantity"] as Long
                            val drinkName = item["drinkName"] as String
                            val totalPrice = item["totalPrice"] as Long
                            val note = item["note"] as String
                            val drinkSize = item["drinkSize"] as String
                            val drinkTopping = item["drinkTopping"] as List<String>

                            drink.add(Cart(totalPrice, quantity, drinkName, drinkSize, drinkTopping, note))
                        }
                        bill.add(Bill(billId, userId, address, drink, status, userReceiver, phoneReceiver, shipFee, time))
                    }

                    billsUser.postValue(bill)
                    _loadingBillsUserResult.postValue(false)
                }
            }
    }

    // bill cua  user theo tung idBill
    fun getBillUserById(id: String){
        _loadingBillUserByIdResult.postValue(true)
        db.collection("Bills").document(auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful && task.isSuccessful){
                    val data = task.result.data?.get("bill") as List<Map<*, *>>
                    var bill = Bill()
                    data.forEach { element->
                        if(element["billId"] == id){
                            val userId = element["userId"] as String
                            val address = element["address"] as String
                            val status = element["status"] as Long
                            val shipFee = element["shipFee"] as Long
                            val time = element["time"] as String
                            val drinks = element["drinks"]  as List<*>
                            val userReceiver = element["userReceiver"] as String
                            val phoneReceiver = element["phoneReceiver"] as String

                            val drink = mutableListOf<Cart>()
                            drinks.forEach { element ->
                                val item = element as Map<*,*>
                                val quantity = item["quantity"] as Long
                                val drinkName = item["drinkName"] as String
                                val totalPrice = item["totalPrice"] as Long
                                val note = item["note"] as String
                                val drinkSize = item["drinkSize"] as String
                                val drinkTopping = item["drinkTopping"] as List<String>

                                drink.add(Cart(totalPrice, quantity, drinkName, drinkSize, drinkTopping, note))
                            }
                            bill = Bill(id, userId, address, drink, status,userReceiver, phoneReceiver, shipFee, time)
                        }
                    }
                    billUserById.postValue(bill)
                    _loadingBillUserByIdResult.postValue(false)
                }
            }
    }


    // tat ca bill cua cua hang -> admin
    fun getAllBills() {
        _loadingBillsResult.postValue(true)
        db.collection("Bills").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val bill = ArrayList<Bill>()
                    for (document in task.result) {
                        val data = document.get("bill") as List<Map<*, *>>

                        data.forEach { element ->
                            val billId = element["billId"] as String
                            val userId = element["userId"] as String
                            val address = element["address"] as String
                            val status = element["status"] as Long
                            val shipFee = element["shipFee"] as Long
                            val time = element["time"] as String
                            val drinks = element["drinks"] as List<*>
                            val userReceiver = element["userReceiver"] as String
                            val phoneReceiver = element["phoneReceiver"] as String

                            val drink = mutableListOf<Cart>()
                            drinks.forEach { element ->
                                val item = element as Map<*, *>
                                val quantity = item["quantity"] as Long
                                val drinkName = item["drinkName"] as String
                                val totalPrice = item["totalPrice"] as Long
                                val note = item["note"] as String
                                val drinkSize = item["drinkSize"] as String
                                val drinkTopping = item["drinkTopping"] as List<String>

                                drink.add(
                                    Cart(
                                        totalPrice,
                                        quantity,
                                        drinkName,
                                        drinkSize,
                                        drinkTopping,
                                        note
                                    )
                                )
                            }
                            bill.add(Bill(billId, userId, address, drink, status, userReceiver, phoneReceiver,shipFee, time))
                        }
                    }

                    bills.postValue(bill)
                    _loadingBillsResult.postValue(false)
                }
            }
    }


    // admin update status bill
    // lay ra xem bill cua user nao, cap nhat status tuong ung
    fun updateStatusBillUser(userId: String, idBill: String, statusBill: Long){
        _loadingUpdateStatusBillResult.postValue(true)
        val billRef = db.collection("Bills").document(userId)
        billRef.get().addOnCompleteListener { task ->
                if(task.isSuccessful && task.isSuccessful){
                    val data = task.result.data?.get("bill") as List<Map<*, *>>
                    val updates = ArrayList<Bill>()
                    var bill: Bill
                    for(index in data.indices){
                        val element = data[index]
                        val address = element["address"] as String
                        val billId = element["billId"] as String
                        val shipFee = element["shipFee"] as Long
                        val status = element["status"] as Long
                        val time = element["time"] as String
                        val drinks = element["drinks"]  as List<*>
                        val userReceiver = element["userReceiver"] as String
                        val phoneReceiver = element["phoneReceiver"] as String

                        val drink = mutableListOf<Cart>()
                        drinks.forEach { element ->
                            val item = element as Map<*,*>
                            val quantity = item["quantity"] as Long
                            val drinkName = item["drinkName"] as String
                            val totalPrice = item["totalPrice"] as Long
                            val note = item["note"] as String
                            val drinkSize = item["drinkSize"] as String
                            val drinkTopping = item["drinkTopping"] as List<String>

                            drink.add(Cart(totalPrice, quantity, drinkName, drinkSize, drinkTopping, note))
                        }
                        bill = if(element["billId"] == idBill){
                            Bill(idBill, userId, address, drink, statusBill, userReceiver, phoneReceiver,shipFee, time)
                        } else {
                            Bill(billId, userId, address, drink, status,  userReceiver, phoneReceiver, shipFee, time)
                        }
                        updates.add(bill)
                    }

                    billRef.update("bill", updates).addOnCompleteListener {
                        if (it.isSuccessful) {
                            messageUpdateBill.postValue("Cập nhật trạng thái đơn hàng thành công!!")
                        } else {
                            messageUpdateBill.postValue("Lỗi cập nhật trạng thái đơn hàng: ${it.exception}")
                            Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                            Log.e("Firestore", "Error setting bill", it.exception)
                        }
                        _loadingUpdateStatusBillResult.postValue(false)
                    }
                }
            }
    }
}