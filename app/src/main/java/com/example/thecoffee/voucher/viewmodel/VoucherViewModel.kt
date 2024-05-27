package com.example.thecoffee.voucher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.voucher.model.Voucher
import com.example.thecoffee.voucher.repository.VoucherRepository

class VoucherViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: VoucherRepository = VoucherRepository(application)
    private var _voucherList: MutableLiveData<ArrayList<Voucher>> = repository.getVoucherList
    private val _loadingVoucherResult: MutableLiveData<Boolean> = repository.loadingVoucherResult
    private val _loadingDeleteVoucher: MutableLiveData<Boolean> = repository.loadingDeleteVoucher
    private val _messageCreateVoucher: MutableLiveData<String> = repository.getMessageCreateVoucher
    private val _messageDeleteVoucher: MutableLiveData<String> = repository.getMessageDeleteVoucher

    val getVoucherList: MutableLiveData<ArrayList<Voucher>>
        get() = _voucherList

    val loadingVoucherResult: MutableLiveData<Boolean>
        get() = _loadingVoucherResult

    val loadingDeleteVoucher: MutableLiveData<Boolean>
        get() = _loadingDeleteVoucher

    val getMessageCreateVoucher: MutableLiveData<String>
        get() = _messageCreateVoucher

    val getMessageDeleteVoucher: MutableLiveData<String>
        get() = _messageDeleteVoucher


    fun getVoucherList(){
        repository.getVoucherList()
    }

    fun deleteVoucher(voucherId: String){
        repository.deleteVoucher(voucherId)
    }
}