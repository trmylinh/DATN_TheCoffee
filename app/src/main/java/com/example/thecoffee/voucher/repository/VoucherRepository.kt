package com.example.thecoffee.voucher.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.voucher.model.Voucher
import com.google.firebase.firestore.FirebaseFirestore

class VoucherRepository(_application: Application) {
    private var application: Application = _application
    private var voucherList: MutableLiveData<ArrayList<Voucher>> =
        MutableLiveData<ArrayList<Voucher>>()
    private val _loadingVoucherResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val messageCreateVoucher: MutableLiveData<String> = MutableLiveData<String>()
    private val messageDeleteVoucher: MutableLiveData<String> = MutableLiveData<String>()
    private val messageUpdateVoucher: MutableLiveData<String> = MutableLiveData<String>()
    private val _loadingDeleteVoucher: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingCreateVoucher: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _loadingUpdateVoucher: MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val getVoucherList: MutableLiveData<ArrayList<Voucher>>
        get() = voucherList

    val loadingVoucherResult: MutableLiveData<Boolean>
        get() = _loadingVoucherResult

    val getMessageCreateVoucher: MutableLiveData<String>
        get() = messageCreateVoucher

    val getMessageDeleteVoucher: MutableLiveData<String>
        get() = messageDeleteVoucher

    val getMessageUpdateVoucher: MutableLiveData<String>
        get() = messageUpdateVoucher

    val loadingDeleteVoucher: MutableLiveData<Boolean>
        get() = _loadingDeleteVoucher

    val loadingCreateVoucher: MutableLiveData<Boolean>
        get() = _loadingCreateVoucher

    val loadingUpdateVoucher: MutableLiveData<Boolean>
        get() = _loadingUpdateVoucher

    fun getVoucherList() {
        _loadingVoucherResult.postValue(true)
        db.collection("Vouchers").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val list = ArrayList<Voucher>()
                    for (document in task.result) {
                        val voucherId = document.getString("voucherId")
                        val unit = document.getString("unit")
                        val type = document.getString("type")
                        val supportIdItems = document.get("supportIdItems") as List<String>
                        val startDate = document.getString("start_date")
                        val endDate = document.getString("end_date")
                        val name = document.getString("name")
                        val discount = document.get("discount").toString().toInt()

                        list.add(
                            Voucher(
                                voucherId,
                                unit,
                                type,
                                supportIdItems,
                                startDate,
                                endDate,
                                name,
                                discount,
                            )
                        )
                    }
                    voucherList.postValue(list)
                    _loadingVoucherResult.postValue(false)
                }
            }

    }

    fun deleteVoucher(voucherId: String) {
        _loadingDeleteVoucher.postValue(true)
        db.collection("Vouchers").whereEqualTo("voucherId", voucherId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("Vouchers").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("Delete", "Xóa ưu đãi thành công!!!")
                            messageDeleteVoucher.postValue("Voucher successfully deleted!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Delete", "Error deleting voucher", e)
                            messageDeleteVoucher.postValue("Lỗi xóa ưu đãi: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Delete", "Error deleting voucher", e)
            }
            .addOnCompleteListener {
                _loadingDeleteVoucher.postValue(false)
            }

    }

    fun createVoucher(voucher: Voucher) {
        _loadingCreateVoucher.postValue(true)
        db.collection("Vouchers").add(voucher)
            .addOnSuccessListener {
                messageCreateVoucher.postValue("Tạo ưu đãi mới thành công!!!")
                Log.d("Firestore", "Voucher added!")
            }
            .addOnFailureListener { e ->
                messageCreateVoucher.postValue("Lỗi tạo ưu đãi: ${e.message}")
                Log.e("Firestore", "Error adding voucher", e)
            }
            .addOnCompleteListener {
                _loadingCreateVoucher.postValue(false)
            }
    }

    fun updateVoucher(voucherId: String, newVoucher: Voucher){
        _loadingUpdateVoucher.postValue(true)
        db.collection("Vouchers").whereEqualTo("voucherId", voucherId)
            .get()
            .addOnSuccessListener { documents->
                for (document in documents) {
                    db.collection("Vouchers").document(document.id).set(newVoucher)
                        .addOnSuccessListener {
                            Log.d("Update", "Voucher successfully Update!")
                            messageUpdateVoucher.postValue("Cập nhật ưu đãi thành công!!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Update", "Error Update voucher", e)
                            messageUpdateVoucher.postValue("Lỗi cập nhật ưu đãi: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Update", "Error Update voucher", e)
            }
            .addOnCompleteListener {
                _loadingUpdateVoucher.postValue(false)
            }
    }
}