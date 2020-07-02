package com.example.walkwithme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.walkwithme.R
import kotlinx.android.synthetic.main.fragment_stats.*

class StatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_stats, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val periodItems = arrayListOf("Today", "Last week", "Last month", "Last year", "All time")
        PeriodSpinner.adapter = ArrayAdapter(requireContext(), R.layout.period_item, periodItems)
    }

}