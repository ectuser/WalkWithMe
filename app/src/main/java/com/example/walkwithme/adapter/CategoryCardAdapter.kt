package com.example.walkwithme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.walkwithme.R
import com.example.walkwithme.model.category.CategoryCard

class CategoryCardAdapter(private val categoryCardList: ArrayList<CategoryCard>) :
    RecyclerView.Adapter<CategoryCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.card, p0, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return categoryCardList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.name.text = categoryCardList[p1].name
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById<TextView>(R.id.Name)
    }

}