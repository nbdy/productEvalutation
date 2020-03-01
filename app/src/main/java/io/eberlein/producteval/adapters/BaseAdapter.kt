package io.eberlein.producteval.adapters

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.eberlein.producteval.R
import splitties.dimensions.dip
import splitties.experimental.InternalSplittiesApi
import splitties.typesaferecyclerview.ItemViewHolder
import splitties.views.dsl.core.*
import splitties.views.onClick
import splitties.views.recyclerview.verticalLayoutManager
import splitties.views.selectable.SelectableLinearLayout

@InternalSplittiesApi
class ListItem(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int = 0, disableDefaultTint: Boolean):
    SelectableLinearLayout(ctx, attrs, defStyleAttr) {

    constructor(ctx: Context): this(ctx, null, disableDefaultTint = false)
    constructor(ctx: Context, attrs: AttributeSet?): this(ctx, attrs, disableDefaultTint = false)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int): this(
        ctx, attrs, defStyleAttr, disableDefaultTint = false
    )
    constructor(ctx: Context, disableDefaultTint: Boolean): this(
        ctx, null, disableDefaultTint = disableDefaultTint
    )

    @InternalSplittiesApi
    val content = inflate<SelectableLinearLayout>(R.layout.vh_multiline) {}

    @InternalSplittiesApi
    val firstLine = content.findViewById<TextView>(R.id.tv_first_line)
    @InternalSplittiesApi
    val secondLine = content.findViewById<TextView>(R.id.tv_second_line)
    @InternalSplittiesApi
    val btnOne = content.findViewById<Button>(R.id.btn_one)
    @InternalSplittiesApi
    val btnTwo = content.findViewById<Button>(R.id.btn_two)

    init {
        add(content, lParams {
            width = matchParent
            height = dip(64)
        })
    }
}

@InternalSplittiesApi
abstract class BaseAdapter<T : Any> @InternalSplittiesApi constructor(val host: ViewHolder.Host<T>) : RecyclerView.Adapter<BaseAdapter.ViewHolder<T>>() {
    val layoutManager = verticalLayoutManager()
    private val items: MutableList<T> = ArrayList()

    @InternalSplittiesApi
    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun add(item: T){
        if(!items.contains(item)) {
            items.add(item)
            notifyItemChanged(items.lastIndex)
        }
    }

    fun add(items: List<T>){
        for(item: T in items) add(item)
    }

    fun remove(item: T){
        items.remove(item)
    }

    fun remove(items: List<T>){
        for(item: T in items) remove(item)
    }

    @InternalSplittiesApi
    abstract class ViewHolder<T : Any>(ctx: Context, layoutManager: RecyclerView.LayoutManager,
                                       host: Host<T>): ItemViewHolder<T, ListItem, ViewHolder.Host<T>>(host, ListItem(ctx)){

        init {
            itemView.onClick { host.onItemClicked(data) }
        }

        interface Host<T> {
            fun onItemClicked(item: T)
        }
    }
}

