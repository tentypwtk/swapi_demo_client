package com.grapplo.swapidemo.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.grapplo.swapidemo.databinding.SearchLayoutBinding
import kotlinx.android.synthetic.main.search_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()

    private val adapter by lazy {
        SearchResultAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        SearchLayoutBinding
            .inflate(inflater, container, false).let {
                it.lifecycleOwner = this
                it.viewModel = this@SearchFragment.viewModel
                it.adapter = this@SearchFragment.adapter
                it.root
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SearchFragment", "We're live!")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.adapter = adapter

        viewModel.result.observe(viewLifecycleOwner, Observer { results ->
            adapter.items = results
        })
    }

}