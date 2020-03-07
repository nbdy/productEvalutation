package io.eberlein.producteval.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.eberlein.producteval.objects.DB
import io.eberlein.producteval.objects.Product
import io.eberlein.producteval.objects.ProductDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ProductsViewModelFactory(private val dao: ProductDao, private val categoryId: Long):
        ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProductsViewModel(dao, categoryId) as T
    }
}

class ProductsViewModel(private val dao: ProductDao, private val categoryId: Long) : ViewModel() {
    private val products: MutableLiveData<List<Product>> by lazy {
        MutableLiveData<List<Product>>().also {
            loadProducts()
        }
    }

    fun getProducts(): LiveData<List<Product>> {
        return products
    }

    fun delete(product: Product) {
        dao.delete(product)
    }

    private fun loadProducts(){
        GlobalScope.launch {
            products.postValue(dao.getProductsOfCategory(categoryId))
        }
    }
}