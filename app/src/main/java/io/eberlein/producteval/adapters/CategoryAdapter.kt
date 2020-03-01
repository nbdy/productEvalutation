package io.eberlein.producteval.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.eberlein.producteval.objects.Category
import splitties.experimental.InternalSplittiesApi


@InternalSplittiesApi
class CategoryAdapter @InternalSplittiesApi constructor(host: ViewHolder.Host<Category>) : BaseAdapter<Category>(host) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Category> {
        return VH(parent.context, layoutManager, host)
    }

    class VH(ctx: Context, layoutManager: RecyclerView.LayoutManager, host: Host<Category>) : ViewHolder<Category>(ctx,
        layoutManager, host
    ){
        @InternalSplittiesApi
        override fun ListItem.onBind() {
            firstLine.text = data.name
        }
    }
}