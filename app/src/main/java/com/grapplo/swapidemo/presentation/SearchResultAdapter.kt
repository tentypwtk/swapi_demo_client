package com.grapplo.swapidemo.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grapplo.swapidemo.R
import com.grapplo.swapidemo.domain.SearchResult
import com.grapplo.swapidemo.domain.SwEntity

class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.VH>() {

    var onItemSelected: ((Int, SearchResult<SwEntity>) -> Unit)? = null

    var items: List<SearchResult<SwEntity>> = emptyList()
        set(value) {
            field = value.sortedBy { it.name }
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.search_result_layout, parent, false)
            .let { VH(it) }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    inner class VH(val view: View) : RecyclerView.ViewHolder(view) {
        private val labelLeft by lazy {
            view.findViewById<TextView>(R.id.left_label)
        }
        private val labelRight by lazy {
            view.findViewById<TextView>(R.id.right_label)
        }

        fun bind(searchResult: SearchResult<SwEntity>) {
            view.setOnClickListener {
                onItemSelected?.invoke(
                    items.indexOf(searchResult),
                    searchResult
                )
            }
            labelLeft.text = searchResult.name
            labelRight.text =
                when {
                    searchResult.size != null -> "%d %s".format(
                        searchResult.size,
                        searchResult.sizeUnit
                    )
                    else -> view.context.getString(R.string.unknown_value)
                }
        }
    }
}