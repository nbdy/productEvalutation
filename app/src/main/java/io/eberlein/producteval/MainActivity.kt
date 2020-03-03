package io.eberlein.producteval

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.eberlein.producteval.adapters.BaseAdapter
import io.eberlein.producteval.adapters.CategoryAdapter
import io.eberlein.producteval.objects.Category
import io.eberlein.producteval.objects.DB
import io.eberlein.producteval.ui.products.ProductsFragment
import io.eberlein.producteval.viewmodels.CategoryViewModel
import io.eberlein.producteval.viewmodels.CategoryViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import splitties.arch.room.roomDb
import splitties.experimental.InternalSplittiesApi
import splitties.fragments.fragmentTransaction

@InternalSplittiesApi
class MainActivity : AppCompatActivity(), BaseAdapter.ViewHolder.Host<Category> {
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var catRV: RecyclerView
    private lateinit var catRVA: CategoryAdapter

    private lateinit var db: DB
    private lateinit var model: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupRecycler()
        setupDB()
        setupViewModel()
        setupNavBar()
    }

    private fun setupViewModel(){
        model = viewModels<CategoryViewModel> { CategoryViewModelFactory(db) }.value
        model.getCategories().observe(this, Observer { cats ->
            catRVA.add(cats)
        })
    }

    private fun setupDB(){
        db = roomDb("prodeval") {
            // fallbackToDestructiveMigration()
        }
    }

    private fun setupNavBar(){
        drawerLayout = findViewById(R.id.drawer_layout)
        if(catRVA.itemCount == 0) drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun setupToolbar(){
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun createCategoryDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_category)
        val et: EditText = dialog.findViewById(R.id.et_category_name)
        val ok: Button = dialog.findViewById(R.id.btn_ok)
        val cancel: Button = dialog.findViewById(R.id.btn_cancel)
        cancel.setOnClickListener { dialog.dismiss() }
        ok.setOnClickListener {
            val c = Category(et.text.toString())
            GlobalScope.launch { db.category().insert(c) }
            catRVA.add(c)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupRecycler(){
        catRV = findViewById(R.id.rvCategories)
        catRV.layoutManager = LinearLayoutManager(this)
        catRVA = CategoryAdapter(this)
        catRV.adapter = catRVA
        catRV.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        catRV.setOnClickListener {}
        val addBtn = findViewById<Button>(R.id.btnAddCategory)
        addBtn.setOnClickListener {createCategoryDialog()}
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onItemClicked(item: Category) {
        fragmentTransaction {
            replace(R.id.nav_host_fragment, ProductsFragment(db, item))
        }
        drawerLayout.closeDrawers()
    }
}
