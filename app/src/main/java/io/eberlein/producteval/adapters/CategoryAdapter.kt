package io.eberlein.producteval.adapters

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.eberlein.producteval.R
import io.eberlein.producteval.objects.Category
import splitties.experimental.InternalSplittiesApi
import splitties.resources.color
import splitties.views.onClick


@InternalSplittiesApi
class CategoryAdapter @InternalSplittiesApi constructor(host: ViewHolder.Host<Category>) : BaseAdapter<Category>(host) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Category> {
        return VH(parent.context, layoutManager, host)
    }

    override fun add(item: Category) {
        var d: Int? = null
        for(c: Category in items){
            if(c.cid == item.cid) {
                d = items.indexOf(c)
                items[d] = item
            }
        }
        if(d == null){
            items.add(item)
            d = items.indexOf(item)
        }
        notifyItemChanged(d)
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