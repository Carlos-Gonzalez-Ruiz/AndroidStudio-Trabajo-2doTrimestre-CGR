package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.news

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 9
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NewsArticleFragment("")
            1 -> NewsArticleFragment("general")
            2 -> NewsArticleFragment("politics")
            3 -> NewsArticleFragment("entertainment")
            4 -> NewsArticleFragment("science")
            5 -> NewsArticleFragment("sports")
            6 -> NewsArticleFragment("technology")
            7 -> NewsArticleFragment("business")
            8 -> NewsArticleFragment("health")
            else -> NewsArticleFragment("")
        }
    }
}