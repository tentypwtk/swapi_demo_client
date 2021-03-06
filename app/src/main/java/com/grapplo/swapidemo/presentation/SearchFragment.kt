package com.grapplo.swapidemo.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.grapplo.swapidemo.databinding.SearchLayoutBinding
import com.grapplo.swapidemo.domain.SearchResult
import com.grapplo.swapidemo.domain.SwEntity
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
                it.root
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SearchFragment", "We're live!")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subject_picker.attachEnumAdapter<SearchViewModel.SearchMode> { subject ->
            viewModel.subject.postValue(subject)
        }

        recycler_view.adapter = adapter
        adapter.onItemSelected = { _, selectedResult ->
            findNavController().navigate(SearchFragmentDirections.openDetails(selectedResult.item))
        }

        viewModel.result.observe(viewLifecycleOwner, Observer { results ->
            adapter.items = results
        })
    }

    private inline fun <reified T : Enum<T>> Spinner.attachEnumAdapter(
        crossinline callback: (T) -> Unit = {}
    ): SpinnerAdapter {
        return object :
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1) {
            init {
                addAll(enumValues<T>().map { it.name.toLowerCase().capitalize() }.toList())
                this@attachEnumAdapter.adapter = this
                this.notifyDataSetChanged()
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        enumValues<T>()[position].apply(callback)
                    }
                }
            }


        }
    }


}