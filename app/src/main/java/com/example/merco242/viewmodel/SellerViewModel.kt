package com.example.merco242.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merco242.domain.model.Category
import com.example.merco242.domain.model.Product
import com.example.merco242.domain.model.Store
import com.example.merco242.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SellerViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Categorías
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories

    // Productos
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    // Tiendas
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> get() = _stores

    // Usuario
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    init {
        fetchCategories()
        fetchProducts()
        fetchUser()
        fetchStores()
    }

    // Obtener las categorías
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

    // Obtener los productos
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

    // Crear tienda
    fun createStore(store: Store, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                db.collection("stores").document(store.id).set(store).await()
                fetchStores() // Actualizar la lista local
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    // Actualizar tienda
    fun updateStore(store: Store, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                db.collection("stores").document(store.id).set(store).await()
                fetchStores() // Actualizar la lista local
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    // Obtener tiendas creadas por el usuario actual
    fun fetchStores() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val result = db.collection("stores")
                        .whereEqualTo(
                            "ownerId",
                            currentUser.uid
                        ) // Tiendas creadas por este usuario
                        .get()
                        .await()
                    val storeList = result.documents.mapNotNull { it.toObject(Store::class.java) }
                    _stores.value = storeList
                } else {
                    _stores.value = emptyList()
                }
            } catch (e: Exception) {
                _stores.value = emptyList()
            }
        }
    }

    // Agregar una categoría
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

    // Agregar un producto
    fun addProduct(product: Product, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Verificar que la categoría existe
                val categoryExists =
                    db.collection("categories").document(product.categoryId).get().await()
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

    // Actualizar el nombre de una categoría
    fun updateCategoryName(categoryId: String, newName: String) {
        viewModelScope.launch {
            try {
                db.collection("categories").document(categoryId).update("name", newName).await()
                fetchCategories()
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }

    // Eliminar un producto
    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                db.collection("products").document(productId).delete().await()
                fetchProducts()
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }

    // Eliminar una categoría y sus productos asociados
    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                // Eliminar los productos relacionados
                val productsToDelete = _products.value.filter { it.categoryId == categoryId }
                productsToDelete.forEach { deleteProduct(it.id) }

                // Eliminar la categoría
                db.collection("categories").document(categoryId).delete().await()
                fetchCategories()
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }

    fun fetchUser() {
        viewModelScope.launch {
            try {
                val firebaseUser = auth.currentUser
                firebaseUser?.let {
                    val userDocument = db.collection("users").document(it.uid).get().await()
                    _user.value = userDocument.toObject(User::class.java)
                } ?: run {
                    _user.value = null
                }
            } catch (e: Exception) {
                _user.value = null // Manejo de errores, usuario no encontrado
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun deleteUser() {
        viewModelScope.launch {
            try {
                val firebaseUser = auth.currentUser
                firebaseUser?.let {
                    db.collection("users").document(it.uid).delete()
                        .await() // Eliminar de Firestore
                    it.delete().await() // Eliminar de Firebase Authentication
                }
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }
}
