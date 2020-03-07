package io.eberlein.producteval.ui.products

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import io.eberlein.producteval.R
import io.eberlein.producteval.adapters.BaseAdapter
import io.eberlein.producteval.adapters.ProductsAdapter
import io.eberlein.producteval.objects.*
import io.eberlein.producteval.viewmodels.ProductsViewModel
import io.eberlein.producteval.viewmodels.ProductsViewModelFactory
import io.fotoapparat.Fotoapparat
import io.fotoapparat.view.CameraView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import splitties.experimental.InternalSplittiesApi
import splitties.toast.toast
import splitties.views.onClick
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest


fun ByteArray.hash(algorithm: String): String {
    return MessageDigest.getInstance(algorithm).digest(this).fold("", {
            str, it -> str + "%02x".format(it)
    })
}

fun ByteArray.sha256(): String {
    return this.hash("SHA-256")
}

fun Bitmap.sha256(compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
                  quality: Int = 100): String {
    val os = ByteArrayOutputStream()
    this.compress(compressFormat, quality, os)
    return os.toByteArray().sha256()
}

fun Bitmap.save(file: File,
                compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
                quality: Int = 100){
    val o = FileOutputStream(file)
    this.compress(compressFormat, quality, o)
    o.flush()
    o.close()
}

@InternalSplittiesApi
class ProductsFragment(private val dao: ProductDao, private val category: Category) : Fragment(), BaseAdapter.ViewHolder.Host<Product> {
    private lateinit var rvProducts: RecyclerView
    private lateinit var rvProductsAdapter: ProductsAdapter
    private lateinit var addBtn: FloatingActionButton

    private lateinit var model: ProductsViewModel

    private fun getImageDirectory(directoryName: String = "images"): File{
        val d = File(context?.filesDir, directoryName)
        if(!d.exists()) d.mkdir()
        return d
    }

    private fun updateProduct(product: Product) = GlobalScope.launch(Dispatchers.Default){
        Log.d(tag, "updating product")
        dao.insert(product)
        Log.d(tag, "updated product")
    }

    private fun saveBitmap(product: Product, bmp: Bitmap){ // todo fix; this shits taking hella long
        Log.d(tag, "saving bitmap")
        product.image = bmp.sha256()
        bmp.save(File(getImageDirectory(), product.image!!))
        updateProduct(product)
        Log.d(tag, "saved bitmap")
    }

    private fun loadBitmap(product: Product): Bitmap? {
        if(product.image == null) return null
        Log.d(tag, "loading bitmap")
        return BitmapFactory.decodeStream(FileInputStream(File(product.image!!)))
    }

    private fun createProductDialog(ctx: Context, item: Product){
        val dialog = Dialog(ctx)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_product)
        val iv = dialog.findViewById<ImageView>(R.id.iv)
        val n = dialog.findViewById<EditText>(R.id.et_name)
        val ct = dialog.findViewById<EditText>(R.id.et_code_type)
        val c = dialog.findViewById<EditText>(R.id.et_code)
        val r = dialog.findViewById<SeekBar>(R.id.sb_rating)
        val d = dialog.findViewById<EditText>(R.id.et_description)
        val ok = dialog.findViewById<Button>(R.id.btn_ok)
        val cancel = dialog.findViewById<Button>(R.id.btn_cancel)
        ok.onClick {
            item.name = n.text.toString()
            item.rating = r.progress
            item.description = d.text.toString()
            updateProduct(item)
            rvProductsAdapter.add(item)
            dialog.dismiss()
        }
        cancel.onClick {
            dialog.dismiss()
        }
        if(item.image != null) {
            iv.setImageBitmap(loadBitmap(item))
            // iv.rotation = item.imageRotation
        }
        iv.onClick {
            val cameraDialog = Dialog(ctx)
            cameraDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            cameraDialog.setContentView(R.layout.dialog_camera)
            val cv = cameraDialog.findViewById<CameraView>(R.id.cv)
            val tp = cameraDialog.findViewById<FloatingActionButton>(R.id.btnTakePicture)
            val fa = Fotoapparat(ctx, view = cv)
            cameraDialog.setOnDismissListener { fa.stop() }
            tp.onClick {
                fa.takePicture().toBitmap().whenAvailable {
                    bmp ->
                    if (bmp != null) {
                        iv.setImageBitmap(bmp.bitmap)
                        iv.rotation = (-bmp.rotationDegrees).toFloat()
                        saveBitmap(item, bmp.bitmap)
                        item.imageRotation = iv.rotation
                        updateProduct(item)
                        cameraDialog.dismiss()
                    }
                }
            }
            fa.start()
            cameraDialog.show()
        }
        n.setText(item.name)
        ct.setText(item.codeType)
        c.setText(item.code)
        r.progress = item.rating
        d.setText(item.description)
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val r: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(r.contents == null) toast(R.string.scan_cancelled)
        else {
            GlobalScope.launch {
                val p = dao.getByCode(r.contents)
                if(p != null){ activity?.runOnUiThread { context?.let { createProductDialog(it, p) } } }
                else { activity?.runOnUiThread { context?.let {
                    createProductDialog(it, Product(category.cid, r.contents, r.formatName))
                }} }
            }
        }
    }

    private fun setupRecycler(){
        rvProductsAdapter = ProductsAdapter(this)
        rvProducts.adapter = rvProductsAdapter
        rvProducts.layoutManager = LinearLayoutManager(context)
        rvProducts.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val r = inflater.inflate(R.layout.fragment_products, container, false)
        rvProducts = r.findViewById(R.id.rvProducts)
        setupRecycler()
        addBtn = r.findViewById(R.id.btnAddProduct)
        addBtn.onClick { IntentIntegrator.forSupportFragment(this).initiateScan() }
        model = viewModels<ProductsViewModel> { ProductsViewModelFactory(dao, category.cid) }.value
        model.getProducts().observe(viewLifecycleOwner, Observer { products ->
            rvProductsAdapter.set(products)
        })
        return r
    }

    override fun onItemClicked(item: Product) {
        context?.let { createProductDialog(it, item) }
    }

    override fun onItemBtnOneClicked(item: Product) {
        GlobalScope.launch(Dispatchers.Default) { dao.delete(item) }
        rvProductsAdapter.remove(item)
    }
}