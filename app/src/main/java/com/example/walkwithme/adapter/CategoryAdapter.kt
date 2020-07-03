package com.example.walkwithme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.walkwithme.R
import com.example.walkwithme.model.category.CategoryCard
import kotlinx.android.synthetic.main.category_card.view.*

class CategoryAdapter(private val categoryCardList: ArrayList<CategoryCard>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.category_card, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return categoryCardList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.number.text = categoryCardList[p1].number.toString()
        p0.name.text = categoryCardList[p1].name
        p0.image.setImageDrawable(categoryCardList[p1].image)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val number: TextView = itemView.Number
        val name: TextView = itemView.Name
        var image: ImageView = itemView.Image
    }

}