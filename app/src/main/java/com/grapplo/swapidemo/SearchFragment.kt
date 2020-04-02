package com.grapplo.swapidemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

class SearchFragment : Fragment(R.layout.search_layout) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SearchFragment", "We're live!")
    }

}