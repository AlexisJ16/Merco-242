package com.example.merco242.domain.model

data class Store(
    var id: String = "",
    var name: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var description: String = "",
    var products: List<Product> = emptyList() // Asociar productos con la tienda
)
