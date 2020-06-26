package com.automattic.freshlypressed.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.automattic.freshlypressed.R
import com.automattic.freshlypressed.data.*
import com.automattic.freshlypressed.databinding.PostsFragmentBinding
import com.google.android.material.snackbar.Snackbar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class PostsFragment : Fragment() {

    private lateinit var binding: PostsFragmentBinding

    private val okHttpClient = OkHttpClient() // TODO "inject" @RUI
    private val postService = PostsService.createService(okHttpClient)
    private val siteService = SiteService.createService(okHttpClient)
    private val dateMapper = DateMapperImpl()
    private val postMapper = PostMapperImpl(dateMapper)
    private val postsRepository = WordpressPostsRepository(postService, postMapper)
    private val siteRepository = WordpressSiteRepository(siteService)
    private val viewModelFactory = PostsViewModelFactory(postsRepository, siteRepository)

    private val viewModel: PostsViewModel by viewModels {
        GenericSavedStateViewModelFactory(viewModelFactory, this)
    }

    private var position: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PostsFragmentBinding.inflate(layoutInflater, container, false)
        binding.swipeRefresh.setOnRefreshListener { refreshData() }
        with(binding.postsRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = PostsRecyclerAdapter(
                { post -> viewModel.postClicked(post) },
                { viewModel.loadCount(it) }
            )
        }
        return binding.root
    }

    private fun refreshData() {
        binding.swipeRefresh.isRefreshing = true
        viewModel.loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshData()

        viewModel.position.observe(viewLifecycleOwner, { position = it })

        viewModel.posts.observe(viewLifecycleOwner) { data ->
            val adapter = binding.postsRecyclerView.adapter as PostsRecyclerAdapter
            adapter.data = data
            adapter.notifyDataSetChanged()
            binding.swipeRefresh.isRefreshing = false
            // binding.postsRecyclerView.scrollToPosition(position)
        }

        viewModel.effects.observe(viewLifecycleOwner) { effect ->
            effect.consume { payload ->
                when (payload) {
                    is PostEffects.NavigateToPost -> {
                        val browseIntent = Intent()
                        browseIntent.action = Intent.ACTION_VIEW
                        browseIntent.data = Uri.parse(payload.url)
                        activity?.let { startActivity(browseIntent) }
                    }
                    is PostEffects.NetworkError -> {
                        Snackbar.make(requireView(), getString(R.string.error_message), Snackbar.LENGTH_SHORT).show()
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val position = (binding.postsRecyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0
        viewModel.save(position)
    }
}
