package com.automattic.freshlypressed.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.automattic.freshlypressed.data.PostsApi
import com.automattic.freshlypressed.databinding.PostListFragmentHeaderItemBinding
import com.automattic.freshlypressed.databinding.PostListFragmentItemBinding
import com.automattic.freshlypressed.databinding.PostsFragmentBinding
import com.automattic.freshlypressed.domain.Post
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient

class PostsFragment : Fragment() {

    private lateinit var binding: PostsFragmentBinding

    private val okHttpClient = OkHttpClient() // TODO "inject" @RUI
    private val postsRepository = PostsApi(okHttpClient)
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
            // TODO display @RUI
        }
    }
}
