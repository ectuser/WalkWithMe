package com.example.walkwithme.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        val numberOfCategories = 6
        val names = arrayListOf("Cafe", "Church", "Fountain", "Museum", "Park", "Supermarket")
        val images = ArrayList<Drawable>()

        for (i in 0 until numberOfCategories) {
            images.add(ContextCompat.getDrawable(requireContext(), R.drawable.category_0)!!)
        }

        val categoryView = CategoryRecyclerView
        categoryView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        val dataList = ArrayList<CategoryCard>()

        for (i in 0 until numberOfCategories) {
            dataList.add(
                CategoryCard(
                    i + 1, names[i], images[i]
                )
            )
        }

        val cardAdapter = CategoryCardAdapter(dataList)
        categoryView.adapter = cardAdapter
    }

}