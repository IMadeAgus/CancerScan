package com.dicoding.asclepius.view.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.databinding.FragmentNewsBinding
import com.dicoding.asclepius.helper.Result

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter

    private val viewModel by viewModels<NewsViewModel> {
        NewsViewModelFactory.getInstance(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsAdapter = NewsAdapter()
        setupRecyclerView()
        viewModel.getNews().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    showError(false)
                }
                is Result.Success -> {
                    showLoading(false)
                    showError(false)
                    newsAdapter.submitList(result.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    showError(true, result.error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(isError: Boolean, message: String = "") {
        binding.apply {
            errorLayout.visibility = if (isError) View.VISIBLE else View.GONE
            tvError.text = message
            rvNews.visibility = if (isError) View.GONE else View.VISIBLE
        }
    }
    private fun setupRecyclerView() {
        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}