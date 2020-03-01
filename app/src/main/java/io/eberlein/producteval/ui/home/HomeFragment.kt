package io.eberlein.producteval.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.eberlein.producteval.R
import io.eberlein.producteval.objects.Category
import io.eberlein.producteval.objects.Product

class HomeFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val cc = root.findViewById<TextView>(R.id.tv_category_count)
        val pc = root.findViewById<TextView>(R.id.tv_product_count)
        cc.text = Category().getCount().toString()
        pc.text = Product().getCount().toString()
        return root
    }
}
