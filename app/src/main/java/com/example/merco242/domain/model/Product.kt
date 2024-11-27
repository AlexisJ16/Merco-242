package com.example.merco242.domain.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val categoryId: String = "",
    val imageUrl: String = ""
)
