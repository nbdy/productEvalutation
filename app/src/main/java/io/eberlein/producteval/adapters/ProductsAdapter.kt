package io.eberlein.producteval.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.eberlein.producteval.objects.Product
import splitties.experimental.InternalSplittiesApi

@InternalSplittiesApi
class ProductsAdapter @InternalSplittiesApi constructor(host: ViewHolder.Host<Product>) : BaseAdapter<Product>(host) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Product> {
        return VH(parent.context, layoutManager, host)
    }

    class VH(ctx: Context, layoutManager: RecyclerView.LayoutManager, host: Host<Product>) : ViewHolder<Product>(ctx,
        layoutManager, host
    ){
        @InternalSplittiesApi
        override fun ListItem.onBind() {
            firstLine.text = data.name
            secondLine.text = data.rating.toString()
        }
    }
}