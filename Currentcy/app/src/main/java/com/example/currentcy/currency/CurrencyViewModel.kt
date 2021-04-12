package com.example.currentcy.currency

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class CurrencyViewModel(application: Application) : AndroidViewModel(application) {

    private val _calculateRates = MutableLiveData<Boolean>()
    val calculateRates: LiveData<Boolean>
        get() = _calculateRates

    private val _editList = MutableLiveData<Boolean>()
    val editList: LiveData<Boolean>
        get() = _editList

    init {

    }

    fun onCalculate() {
        _calculateRates.value = true
    }

    fun onCalculateReset() {
        _calculateRates.value = false
    }

    fun onEditList() {
        _editList.value = true
    }

    fun onEditListReset() {
        _editList.value = false
    }
}