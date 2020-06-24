package com.automattic.freshlypressed.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.automattic.freshlypressed.R
import com.automattic.freshlypressed.data.DateMapperImpl
import com.automattic.freshlypressed.data.PostMapperImpl
import com.automattic.freshlypressed.data.PostsService
import com.automattic.freshlypressed.data.WordpressPostsRepository
import com.automattic.freshlypressed.databinding.*
import com.automattic.freshlypressed.domain.Post
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class PostsFragment : Fragment() {

    private lateinit var binding: PostsFragmentBinding

    private val okHttpClient = OkHttpClient() // TODO "inject" @RUI
    private val postService = Retrofit.Builder()
        .baseUrl("https://public-api.wordpress.com/rest/v1.1/sites/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(PostsService::class.java)
    private val dateMapper = DateMapperImpl()
    private val postMapper = PostMapperImpl(dateMapper)
    private val postsRepository = WordpressPostsRepository(postService, postMapper)
    private val viewModelFactory = PostsViewModelFactory(postsRepository)

    private val viewModel: PostsViewModel by viewModels {
        GenericSavedStateViewModelFactory(viewModelFactory, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PostsFragmentBinding.inflate(layoutInflater, container, false)
        //
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadData()

        viewModel.posts.observe(viewLifecycleOwner) { data ->
            with(binding.postsRecyclerView) {
                layoutManager = LinearLayoutManager(context)
                adapter = PostsRecyclerAdapter(data) {
                    // navigate to post
                }
            }
        }
    }
}
