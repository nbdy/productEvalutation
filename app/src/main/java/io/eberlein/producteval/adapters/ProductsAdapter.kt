package io.eberlein.producteval.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.eberlein.producteval.R
import io.eberlein.producteval.objects.Product
import splitties.experimental.InternalSplittiesApi

@InternalSplittiesApi
class ProductsAdapter @InternalSplittiesApi constructor(host: ViewHolder.Host<Product>) : BaseAdapter<Product>(host) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Product> {
        return VH(parent.context, layoutManager, host)
    }

    override fun add(item: Product) {
        var r = false
        var d: Int? = null
        for(i: Product in items){
            if(item.pid == i.pid) {
                d = items.indexOf(item)
                items[d] = item
                r = true
            }
        }
        if(!r) {
            items.add(item)
            d = items.indexOf(item)
        }
        if(d != null) notifyItemChanged(d)
    }

    class VH(ctx: Context, layoutManager: RecyclerView.LayoutManager, host: Host<Product>) : ViewHolder<Product>(ctx,
        layoutManager, host
    ){
        init {
            itemView.btnOne.setText(R.string.delete)
        }

        @InternalSplittiesApi
        override fun ListItem.onBind() {
            firstLine.text = data.name
            secondLine.text = data.rating.toString()
        }
    }
}