package com.example.merco242.domain.model

data class Reservation(
    var id: String = "",
    var userId: String = "",
    var storeId: String = "",
    var storeName: String = "",
    var timestamp: Long = System.currentTimeMillis()
)
