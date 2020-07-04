package com.example.walkwithme

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walkwithme.adapter.CategoryAdapter

interface SettingsViewInterface {

    fun configurateRecyclerView(adapter: CategoryAdapter)
    fun getDrawable(resource: Int): Drawable?

}