package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.news

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.carlosgonzalezruiz2dam.trabajo1trimestre.R
import com.carlosgonzalezruiz2dam.trabajo1trimestre.databinding.FragmentNewsArticleBinding
import com.carlosgonzalezruiz2dam.trabajo1trimestre.databinding.FragmentNewsBinding
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.NewsData
import com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.detail.DetailFragment

class NewsArticleFragment(category: String) : Fragment(R.layout.fragment_news_article) {
    private val viewModel: NewsViewModel by viewModels {
        NewsViewModelFactory(
            getString(R.string.country_code),
            NewsData(category),
            getCurrentDate(),
            getString(R.string.api_key))
    }

    private lateinit var binding: FragmentNewsArticleBinding
    private val adapter = NewsAdapter() { newArticle -> viewModel.navigateTo(newArticle) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewsArticleBinding.bind(view).apply {
            recycler.adapter = adapter
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.visibility = if (state.loading) VISIBLE else GONE
            state.news?.let {
                adapter.news = state.news
                adapter.notifyDataSetChanged()
            }

            state.navigateTo?.let {
                findNavController().navigate(
                    R.id.action_newsFragment_to_detailFragment,
                    bundleOf(DetailFragment.EXTRA_NEW to it)
                )
                viewModel.onNavigateDone()
            }
        }
    }

}