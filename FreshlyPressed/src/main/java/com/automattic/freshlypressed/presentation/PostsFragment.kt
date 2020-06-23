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
import com.automattic.freshlypressed.data.DateMapperImpl
import com.automattic.freshlypressed.data.PostMapperImpl
import com.automattic.freshlypressed.data.PostsService
import com.automattic.freshlypressed.data.WordpressPostsRepository
import com.automattic.freshlypressed.databinding.PostListFragmentHeaderItemBinding
import com.automattic.freshlypressed.databinding.PostListFragmentItemBinding
import com.automattic.freshlypressed.databinding.PostsFragmentBinding
import com.automattic.freshlypressed.domain.PojoPost
import com.automattic.freshlypressed.domain.Post
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.ParseException

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

abstract class PostViewHolder(
    val listener: (Post) -> Unit,
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(post: Post)
}

class ItemViewHolder(
    private val binding: PostListFragmentItemBinding,
    listener: (Post) -> Unit,
    view: View
) : PostViewHolder(listener, view) {

    override fun bind(post: Post) {
        with(binding) {
            authorName.text = post.author
            subscribersCount.text = "0" // TODO @RUI
            summary.text = post.excerpt

            Glide.with(itemView.context).load(post.imageUrl).into(image)
        }
        itemView.setOnClickListener { listener(post) }
    }
}

class HeaderViewHolder(
    private val binding: PostListFragmentHeaderItemBinding,
    listener: (Post) -> Unit,
    view: View
) : PostViewHolder(listener, view) {

    override fun bind(post: Post) {
        binding.header.text = "DATE" // TODO
        with(binding.item) {
            authorName.text = post.author
            subscribersCount.text = "0" // TODO @RUI
            summary.text = post.excerpt

            Glide.with(itemView.context).load(post.imageUrl).into(image)
        }

        itemView.setOnClickListener { listener(post) }
    }
}

enum class PostViewType {
    ITEM,
    HEADER
}

class PostsRecyclerAdapter(
    private val listener: (Post) -> Unit
) : RecyclerView.Adapter<PostViewHolder>() {

    var data: List<Post> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    constructor(data: List<Post>, listener: (Post) -> Unit) : this(listener) {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return when (viewType) {
            PostViewType.HEADER.ordinal -> {
                val binding = PostListFragmentItemBinding.inflate(LayoutInflater.from(parent.context))
                ItemViewHolder(binding, listener, binding.root)
            }
            PostViewType.ITEM.ordinal -> {
                val binding = PostListFragmentHeaderItemBinding.inflate(LayoutInflater.from(parent.context))
                HeaderViewHolder(binding, listener, binding.root)
            }
            else -> throw Error("Unexpected viewType: $viewType. Known view types are '${PostViewType.values()}'")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun isNewDay(currentItem: Post, previousItem: Post): Boolean {
        try {
            val dayOfPreviousItem = previousItem.date.time - 60000 * 60 * 24
            return dayOfPreviousItem > currentItem.date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    override fun getItemViewType(position: Int) = when {
            position == 0 -> PostViewType.HEADER.ordinal
            position > 0 -> {
                if (isNewDay(data[position], data[position - 1])) {
                    PostViewType.ITEM.ordinal
                } else {
                    PostViewType.HEADER.ordinal
                }
            }
            else -> throw IllegalStateException("Position cannot be negative.")
        }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = data[position]
        holder.bind(post)
//        when (getItemViewType(position)) {
//            PostViewType.HEADER.ordinal -> {
//                holder.bind(post)
//            }
//            PostViewType.ITEM.ordinal -> {
//                holder.bind(post)
//            }
//            else -> throw Error("")
//        }
    }
}
