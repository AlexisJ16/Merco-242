package com.example.merco242.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merco242.domain.model.Category
import com.example.merco242.domain.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SellerViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Categorías
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories

    // Productos
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    init {
        fetchCategories()
        fetchProducts()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val result = db.collection("categories").get().await()
                val categoryList = result.documents.mapNotNull { it.toObject(Category::class.java) }
                _categories.value = categoryList
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val result = db.collection("products").get().await()
                val productList = result.documents.mapNotNull { it.toObject(Product::class.java) }
                _products.value = productList
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }

    fun addCategory(category: Category, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                db.collection("categories").document(category.id).set(category).await()
                fetchCategories()
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun addProduct(product: Product, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Verificar que la categoría existe
                val categoryExists = db.collection("categories").document(product.categoryId).get().await()
                if (!categoryExists.exists()) {
                    onComplete(false)
                    return@launch
                }

                // Agregar el producto a Firebase
                db.collection("products").document(product.id).set(product).await()

                // Actualizar la lista local de productos
                fetchProducts()
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

}
