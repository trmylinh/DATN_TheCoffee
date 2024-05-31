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
    private val _loadingCreateVoucher: MutableLiveData<Boolean> = repository.loadingCreateVoucher
    private val _loadingUpdateVoucher: MutableLiveData<Boolean> = repository.loadingUpdateVoucher
    private val _messageCreateVoucher: MutableLiveData<String> = repository.getMessageCreateVoucher
    private val _messageDeleteVoucher: MutableLiveData<String> = repository.getMessageDeleteVoucher
    private val _messageUpdateVoucher: MutableLiveData<String> = repository.getMessageUpdateVoucher

    val getVoucherList: MutableLiveData<ArrayList<Voucher>>
        get() = _voucherList

    val loadingVoucherResult: MutableLiveData<Boolean>
        get() = _loadingVoucherResult

    val loadingDeleteVoucher: MutableLiveData<Boolean>
        get() = _loadingDeleteVoucher

    val loadingCreateVoucher: MutableLiveData<Boolean>
        get() = _loadingCreateVoucher

    val loadingUpdateVoucher: MutableLiveData<Boolean>
        get() = _loadingUpdateVoucher

    val getMessageCreateVoucher: MutableLiveData<String>
        get() = _messageCreateVoucher

    val getMessageDeleteVoucher: MutableLiveData<String>
        get() = _messageDeleteVoucher

    val getMessageUpdateVoucher: MutableLiveData<String>
        get() = _messageUpdateVoucher


    fun getVoucherList(){
        repository.getVoucherList()
    }

    fun deleteVoucher(voucherId: String){
        repository.deleteVoucher(voucherId)
    }

    fun createVoucher(voucher: Voucher){
        repository.createVoucher(voucher)
    }

    fun updateVoucher(voucherId: String, newVoucher: Voucher){
        repository.updateVoucher(voucherId, newVoucher)
    }
}