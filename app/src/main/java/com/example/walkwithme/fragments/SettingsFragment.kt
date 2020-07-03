package com.example.walkwithme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.walkwithme.R
import com.example.walkwithme.adapter.CategoryAdapter
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
        val numberOfCategories = 6
        val names = arrayListOf("Cafe", "Church", "Fountain", "Museum", "Park", "Supermarket")
        val images = arrayListOf(
            ContextCompat.getDrawable(requireContext(), R.drawable.category_cafe)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.category_church)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.category_fountain)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.category_museum)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.category_park)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.category_supermarket)!!
        )

        CategoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        val dataList = ArrayList<CategoryCard>()

        for (i in 0 until numberOfCategories) {
            dataList.add(
                CategoryCard(
                    1, names[i], images[i]
                )
            )
        }

        val cardAdapter = CategoryAdapter(dataList)
        CategoryRecyclerView.adapter = cardAdapter
    }

}