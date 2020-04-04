package com.grapplo.swapidemo.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grapplo.swapidemo.R
import com.grapplo.swapidemo.domain.SearchResult

class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.VH>() {

    var items: List<SearchResult> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.search_result_layout, parent, false)
            .let { VH(it) }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    class VH(val view: View) : RecyclerView.ViewHolder(view) {
        private val labelLeft by lazy {
            view.findViewById<TextView>(R.id.left_label)
        }
        private val labelRight by lazy {
            view.findViewById<TextView>(R.id.right_label)
        }

        fun bind(searchResult: SearchResult) {
            labelLeft.text = searchResult.name
            labelRight.text =
                searchResult.size?.toString() ?: view.context.getString(R.string.unknown_value)
        }
    }
}