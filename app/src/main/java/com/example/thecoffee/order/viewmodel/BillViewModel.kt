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
    private var repository: BillRepository
    private val _loadingResult: MutableLiveData<Boolean>
    private val _loadingBillsUserResult: MutableLiveData<Boolean>
    private val _loadingBillsResult: MutableLiveData<Boolean>
    private val _loadingBillUserByIdResult: MutableLiveData<Boolean>
    private val _loadingUpdateStatusBillResult: MutableLiveData<Boolean>
    private var billsUser: MutableLiveData<ArrayList<Bill>>
    private var bills: MutableLiveData<ArrayList<Bill>>
    private var billUserById: MutableLiveData<Bill>

    init {
        repository = BillRepository(application)
        _loadingResult = repository.loadingResult
        _loadingBillsUserResult = repository.loadingBillsUserResult
        _loadingBillsResult = repository.loadingBillsResult
        _loadingBillUserByIdResult = repository.loadingBillUserByIdResult
        _loadingUpdateStatusBillResult = repository.loadingUpdateStatusBillResult
        billsUser = repository.getBillsUser
        bills = repository.getBills
        billUserById = repository.getBillUserById
    }

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