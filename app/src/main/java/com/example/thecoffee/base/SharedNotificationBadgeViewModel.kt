package com.example.thecoffee.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedNotificationBadgeViewModel : ViewModel() {
    private val _notificationBadge = MutableLiveData<Int>()
    val notificationBadge: MutableLiveData<Int> get() = _notificationBadge

//    init {
//        notificationBadge.postValue(0)
//    }

    fun incrementBadge() {
        val currentBadge = _notificationBadge.value ?: 0
        _notificationBadge.postValue(currentBadge + 1)
    }

}