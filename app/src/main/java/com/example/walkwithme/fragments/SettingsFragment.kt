package com.example.walkwithme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.walkwithme.R
import com.example.walkwithme.adapter.CategoryCardAdapter
import com.example.walkwithme.model.category.CategoryCard
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val categoryView = CategoryRecyclerView
        categoryView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        val dataList = arrayListOf(CategoryCard("First"), CategoryCard("Second"), CategoryCard("First"), CategoryCard("Second"), CategoryCard("First"), CategoryCard("Second"), CategoryCard("First"), CategoryCard("Second"),CategoryCard("First"), CategoryCard("Second"), CategoryCard("First"), CategoryCard("Second"),CategoryCard("First"), CategoryCard("Second"))
        val cardAdapter = CategoryCardAdapter(dataList)
        categoryView.adapter = cardAdapter
    }

}