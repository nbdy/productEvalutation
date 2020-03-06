package io.eberlein.producteval.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.eberlein.producteval.objects.Category
import io.eberlein.producteval.objects.CategoryDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CategoryViewModelFactory(private val dao: CategoryDao) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CategoryViewModel(dao) as T
    }
}

class CategoryViewModel(private val dao: CategoryDao) : ViewModel() {
    private val categories: MutableLiveData<List<Category>> by lazy {
        MutableLiveData<List<Category>>().also {
            loadCategories()
        }
    }

    fun insert(category: Category) {
        dao.insert(category)
    }

    fun delete(category: Category) {
        dao.delete(category)
    }

    fun getCategories(): LiveData<List<Category>> {
        return categories
    }

    private fun loadCategories(){
        GlobalScope.launch {
            categories.postValue(dao.getAll())
        }
    }
}