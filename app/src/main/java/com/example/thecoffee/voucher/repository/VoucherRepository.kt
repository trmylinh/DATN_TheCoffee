package com.example.thecoffee.voucher.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.voucher.model.Voucher
import com.google.firebase.firestore.FirebaseFirestore

class VoucherRepository(_application: Application) {
    private var application: Application = _application
    private var voucherList: MutableLiveData<ArrayList<Voucher>> = MutableLiveData<ArrayList<Voucher>>()
    private val _loadingVoucherResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val messageCreateVoucher: MutableLiveData<String> = MutableLiveData<String>()
    private val messageDeleteVoucher: MutableLiveData<String> = MutableLiveData<String>()

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val getVoucherList: MutableLiveData<ArrayList<Voucher>>
        get() = voucherList

    val loadingVoucherResult: MutableLiveData<Boolean>
        get() = _loadingVoucherResult

    val getMessageCreateVoucher: MutableLiveData<String>
        get() = messageCreateVoucher

    val getMessageDeleteVoucher: MutableLiveData<String>
        get() = messageDeleteVoucher

    fun getVoucherList(){
        _loadingVoucherResult.postValue(true)
        db.collection("Vouchers").get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful && task.result != null){
                    val list = ArrayList<Voucher>()
                    for(document in task.result){
                        val voucherId = document.getString("voucherId")
                        val unit = document.getString("unit")
                        val type = document.getString("type")
                        val supportIdItems = document.get("supportIdItems") as List<String>
                        val startDate = document.getString("start_date")
                        val endDate = document.getString("end_date")
                        val name = document.getString("name")
                        val discount = document.get("discount").toString().toInt()
                        val amount = document.get("amount").toString().toInt()
                        val expired = document.getBoolean("expired")

                        list.add(Voucher(voucherId, unit, type, supportIdItems, startDate, endDate, name, discount, amount, expired))
                    }
                    voucherList.postValue(list)
                    _loadingVoucherResult.postValue(false)
                }
            }

    }
}