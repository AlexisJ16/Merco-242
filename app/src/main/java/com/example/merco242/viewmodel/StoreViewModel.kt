package com.example.merco242.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merco242.domain.model.Store
import com.example.merco242.repository.StoreRepository
import com.example.merco242.repository.StoreRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoreViewModel(
    private val storeRepository: StoreRepository = StoreRepositoryImpl()
) : ViewModel() {
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> get() = _stores

    fun fetchStores() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = storeRepository.getStores()
            _stores.value = result
        }
    }
}
