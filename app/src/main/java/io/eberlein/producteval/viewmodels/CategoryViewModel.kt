package io.eberlein.producteval.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.eberlein.producteval.objects.Category
import io.eberlein.producteval.objects.DB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CategoryViewModelFactory(private val db: DB) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CategoryViewModel(db) as T
    }
}

class CategoryViewModel(private val db: DB) : ViewModel() {
    private val categories: MutableLiveData<List<Category>> by lazy {
        MutableLiveData<List<Category>>().also {
            loadCategories()
        }
    }

    fun getCategories(): LiveData<List<Category>> {
        return categories
    }

    private fun loadCategories(){
        GlobalScope.launch {
            categories.postValue(db.category().getAll())
        }
    }
}