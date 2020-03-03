package io.eberlein.producteval.ui.products

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
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
import splitties.experimental.InternalSplittiesApi
import splitties.toast.toast
import splitties.views.onClick
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


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
class ProductsFragment(private val db: DB, private val category: Category) : Fragment(), BaseAdapter.ViewHolder.Host<Product> {
    private lateinit var rvProducts: RecyclerView
    private lateinit var rvProductsAdapter: ProductsAdapter
    private lateinit var addBtn: FloatingActionButton

    private lateinit var model: ProductsViewModel

    private fun getImageDirectory(directoryName: String = "images"): File{
        val d = File(context?.filesDir, directoryName)
        if(!d.exists()) d.mkdir()
        return d
    }

    private fun saveBitmap(product: Product, bmp: Bitmap){ // todo actually use
        product.image = bmp.sha256()
        bmp.save(File(getImageDirectory(), product.image!!))
        db.product().update(product)
    }

    private fun loadBitmap(product: Product): Bitmap? {
        if(product.image == null) return null
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
            db.product().update(item)
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
                        db.product().update(item)
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
            val p = Product(r.contents, r.formatName)
            context?.let { createProductDialog(it, p) }
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
        model = viewModels<ProductsViewModel> { ProductsViewModelFactory(db, category.cid) }.value
        model.getProducts().observe(viewLifecycleOwner, Observer { products ->
            rvProductsAdapter.add(products)
        })
        return r
    }

    override fun onItemClicked(item: Product) {
        context?.let { createProductDialog(it, item) }
    }
}