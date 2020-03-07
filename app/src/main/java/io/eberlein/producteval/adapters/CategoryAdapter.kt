package io.eberlein.producteval.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.eberlein.producteval.R
import io.eberlein.producteval.objects.Category
import splitties.experimental.InternalSplittiesApi


@InternalSplittiesApi
class CategoryAdapter @InternalSplittiesApi constructor(host: ViewHolder.Host<Category>) : BaseAdapter<Category>(host) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Category> {
        return VH(parent.context, layoutManager, host)
    }

    override fun add(item: Category) {
        var r = false
        var d: Int? = null
        Log.d("catadapternew", item.cid.toString())
        for(i: Category in items){
            Log.d("catadapterexist", i.cid.toString())
            if(item.cid == i.cid) {
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

    class VH(ctx: Context, layoutManager: RecyclerView.LayoutManager, host: Host<Category>) : ViewHolder<Category>(ctx,
        layoutManager, host
    ){
        init {
            itemView.btnOne.setText(R.string.delete)
            itemView.btnOne.setBackgroundColor(Color.RED)
            itemView.btnTwo.setText(R.string.edit)
        }

        @InternalSplittiesApi
        override fun ListItem.onBind() {
            firstLine.text = data.name
        }
    }
}