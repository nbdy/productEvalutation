package io.eberlein.producteval.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.eberlein.producteval.objects.DB
import io.eberlein.producteval.objects.Product
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ProductsViewModelFactory(private val db: DB, private val categoryId: Long):
        ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProductsViewModel(db, categoryId) as T
    }
}

class ProductsViewModel(private val db: DB, private val categoryId: Long) : ViewModel() {
    private val products: MutableLiveData<List<Product>> by lazy {
        MutableLiveData<List<Product>>().also {
            loadProducts()
        }
    }

    fun getProducts(): LiveData<List<Product>> {
        return products
    }

    private fun loadProducts(){
        GlobalScope.launch {
            val t: MutableList<Product> = ArrayList()
            db.category().getCategoryWithProducts(categoryId).forEach { cwp ->
                t.addAll(cwp.products)
            }
            products.postValue(t)
        }
    }
}