package com.example.icesiapp242.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icesiapp242.domain.model.Seller
import com.example.icesiapp242.repository.SellerRepository
import com.example.icesiapp242.repository.SellerRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SellerViewModel(
    private val sellerRepository: SellerRepository = SellerRepositoryImpl()
) : ViewModel() {

    private val _seller = MutableLiveData<Seller?>()
    val seller: LiveData<Seller?> get() = _seller

    fun getCurrentSeller() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSeller = sellerRepository.getCurrentSeller()
            withContext(Dispatchers.Main) {
                _seller.value = currentSeller
            }
        }
    }

    fun createSeller(seller: Seller) {
        viewModelScope.launch(Dispatchers.IO) {
            sellerRepository.createSeller(seller)
        }
    }
}
