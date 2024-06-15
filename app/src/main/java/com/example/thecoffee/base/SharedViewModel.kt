package com.example.thecoffee.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _sharedData = MutableLiveData<List<String>>()
    val sharedData: MutableLiveData<List<String>> get() = _sharedData

    init {
        _sharedData.value = mutableListOf()
    }

    fun addData(newData: String) {
        val currentData = _sharedData.value.orEmpty().toMutableList()
        currentData.add(newData)
        if (_sharedData.value != currentData) {
            _sharedData.value = currentData
        }
    }

    fun clearData() {
        _sharedData.value = emptyList()
    }

}