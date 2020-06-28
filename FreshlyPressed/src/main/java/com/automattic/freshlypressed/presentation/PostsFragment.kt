package com.automattic.freshlypressed.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.automattic.freshlypressed.R
import com.automattic.freshlypressed.data.*
import com.automattic.freshlypressed.databinding.FragmentPostsBinding
import com.automattic.freshlypressed.domain.Post
import com.google.android.material.snackbar.Snackbar
import okhttp3.OkHttpClient

class PostsFragment : Fragment() {

    private lateinit var binding: FragmentPostsBinding

    private val okHttpClient = OkHttpClient()
    private val postService = PostsService.createService(okHttpClient)
    private val siteService = SiteService.createService(okHttpClient)
    private val dateMapper = DateMapperImpl()
    private val postMapper = PostMapperImpl(dateMapper)
    private val postsRepository = WordpressPostsRepository(postService, postMapper)
    private val siteMapper = SiteMapperImpl()
    private val siteRepository = WordpressSiteRepository(siteService, siteMapper)
    private val viewModelFactory = PostsViewModelFactory(postsRepository, siteRepository)

    private val viewModel: PostsViewModel by viewModels {
        GenericSavedStateViewModelFactory(viewModelFactory, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPostsBinding.inflate(layoutInflater, container, false)
        binding.swipeRefresh.setOnRefreshListener { refreshData() }
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        with(binding.postsRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = PostsRecyclerAdapter(
                { post -> viewModel.postClicked(post) },
                { viewModel.loadCount(it) }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshData()

        viewModel.posts.observe(viewLifecycleOwner) { data ->
            displayData(data)
            stopLoading()
        }

        viewModel.effects.observe(viewLifecycleOwner) { effect ->
            effect.consume { payload ->
                when (payload) {
                    is PostEffects.NavigateToPost -> navigateToPost(payload)
                    is PostEffects.NetworkError -> {
                        Snackbar.make(requireView(), getString(R.string.error_message), Snackbar.LENGTH_SHORT).show()
                        stopLoading()
                    }
                }
            }
        }
    }

    private fun refreshData() {
        binding.swipeRefresh.isRefreshing = true
        viewModel.loadData()
    }

    private fun displayData(data: List<Post>) {
        val adapter = binding.postsRecyclerView.adapter as PostsRecyclerAdapter
        adapter.data = data
        adapter.notifyDataSetChanged()
    }

    private fun navigateToPost(payload: PostEffects.NavigateToPost) {
        val browseIntent = Intent()
        browseIntent.action = Intent.ACTION_VIEW
        browseIntent.data = Uri.parse(payload.url)
        activity?.let { startActivity(browseIntent) }
    }

    private fun stopLoading() {
        binding.swipeRefresh.isRefreshing = false
    }
}
