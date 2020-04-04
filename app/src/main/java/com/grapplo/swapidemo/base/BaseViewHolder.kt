package com.grapplo.swapidemo.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Base view holder class parametrized with
 * @M - model
 * @L - click listener
 */
abstract class BaseViewHolder<M>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun onBind(item: M, callback: (M, Int) -> Unit) {
        itemView.setOnClickListener {
            callback.invoke(item, adapterPosition)
        }
        bind(item)
    }

    abstract fun bind(item: M)
}