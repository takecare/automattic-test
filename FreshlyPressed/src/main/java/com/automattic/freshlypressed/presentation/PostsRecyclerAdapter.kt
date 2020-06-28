package com.automattic.freshlypressed.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.automattic.freshlypressed.databinding.ItemHeaderBinding
import com.automattic.freshlypressed.databinding.ItemPostBinding
import com.automattic.freshlypressed.domain.Post
import java.text.ParseException

class PostsRecyclerAdapter(
    private val clickListener: (Post) -> Unit,
    private val getSubscriberCount: (Post) -> Unit
) : RecyclerView.Adapter<PostViewHolder>() {

    var data: List<Post> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    constructor(data: List<Post>, clickListener: (Post) -> Unit, getSubscriberCount: (Post) -> Unit) : this(clickListener, getSubscriberCount) {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return when (viewType) {
            PostViewType.HEADER.ordinal -> {
                val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding, clickListener, binding.root)
            }
            PostViewType.ITEM.ordinal -> {
                val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding, clickListener, binding.root)
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
                    PostViewType.HEADER.ordinal
                } else {
                    PostViewType.ITEM.ordinal
                }
            }
            else -> throw IllegalStateException("Position cannot be negative.")
        }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = data[position]
        getSubscriberCount(post)
        holder.bind(post)
    }
}

