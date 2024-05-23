package com.example.thecoffee.order.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.order.model.Bill
import com.example.thecoffee.order.model.Cart
import com.example.thecoffee.order.repository.BillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BillViewModel (application: Application) : AndroidViewModel(application) {
    private var repository: BillRepository = BillRepository(application)
    private val _loadingResult: MutableLiveData<Boolean> = repository.loadingResult
    private val _loadingBillsUserResult: MutableLiveData<Boolean> = repository.loadingBillsUserResult
    private val _loadingBillsResult: MutableLiveData<Boolean> = repository.loadingBillsResult
    private val _loadingBillUserByIdResult: MutableLiveData<Boolean> = repository.loadingBillUserByIdResult
    private val _loadingUpdateStatusBillResult: MutableLiveData<Boolean> = repository.loadingUpdateStatusBillResult
    private var billsUser: MutableLiveData<ArrayList<Bill>> = repository.getBillsUser
    private var bills: MutableLiveData<ArrayList<Bill>> = repository.getBills
    private var billUserById: MutableLiveData<Bill> = repository.getBillUserById

    private val _messageUpdateBill: MutableLiveData<String> = repository.getMessageUpdateBill

    val loadingResult: MutableLiveData<Boolean>
    get() = _loadingResult

    val loadingBillsUserResult: MutableLiveData<Boolean>
        get() = _loadingBillsUserResult

    val loadingBillsResult: MutableLiveData<Boolean>
        get() = _loadingBillsResult

    val loadingUpdateStatusBillResult: MutableLiveData<Boolean>
        get() = _loadingUpdateStatusBillResult

    val getBillsUser: MutableLiveData<ArrayList<Bill>>
        get() = billsUser

    val getBills: MutableLiveData<ArrayList<Bill>>
        get() = bills

    val getMessageUpdateBill: MutableLiveData<String>
        get() = _messageUpdateBill

    fun addToCart(cart: Cart){
        repository.addToCart(cart)
    }

    fun order(bill: Bill) {
        repository.order(bill)
    }

    fun getAllBillsUser(){
        repository.getAllBillsByUser()
    }

    fun getBillUserById(id: String){
        repository.getBillUserById(id)
    }

    fun getAllBills(){
        repository.getAllBills()
    }

    fun updateStatusBillUser(userId: String, idBill: String, status: Long){
        repository.updateStatusBillUser(userId, idBill, status)
    }

}