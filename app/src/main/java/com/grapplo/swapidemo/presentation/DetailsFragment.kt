package com.grapplo.swapidemo.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.grapplo.swapidemo.databinding.DetailsLayoutBinding
import com.grapplo.swapidemo.domain.SwEntity

class DetailsFragment : Fragment() {

    private lateinit var dataBinding: DetailsLayoutBinding

    private val backHandler = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = DetailsLayoutBinding.inflate(inflater, container, false).let {
        this@DetailsFragment.dataBinding = it
        it.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backHandler)
        arguments?.getParcelable<SwEntity>("@string/argument_details")
            ?.let { it.name to it.description }
            ?.let { (title, descr) ->
                dataBinding.title = title
                dataBinding.description = descr
                dataBinding.invalidateAll()
            }
    }
}