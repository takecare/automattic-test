package com.automattic.freshlypressed.presentation

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.automattic.freshlypressed.PostUtils.printDate
import com.automattic.freshlypressed.R
import com.automattic.freshlypressed.databinding.ItemHeaderBinding
import com.automattic.freshlypressed.databinding.ItemPostBinding
import com.automattic.freshlypressed.domain.Post
import com.bumptech.glide.Glide

abstract class PostViewHolder(
    val listener: (Post) -> Unit,
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(post: Post)
}

class ItemViewHolder(
    private val binding: ItemPostBinding,
    listener: (Post) -> Unit,
    view: View
) : PostViewHolder(listener, view) {

    override fun bind(post: Post) {
        with(binding) {
            title.text = post.readableTitle()
            author.text = itemView.resources.getString(R.string.author, post.author)
            subscribers.text = itemView.resources.getString(R.string.subscriber_count, -1) // TODO @RUI
            excerpt.text = post.readableExcerpt()
            Glide.with(itemView.context)
                .load(post.imageUrl)
                .centerCrop()
                .into(image)
        }
        itemView.setOnClickListener { listener(post) }
    }
}

class HeaderViewHolder(
    private val binding: ItemHeaderBinding,
    listener: (Post) -> Unit,
    view: View
) : PostViewHolder(listener, view) {

    override fun bind(post: Post) {
        binding.header.text = printDate(post.date) // TODO replace postutils @RUI

        with(binding.item) {
            title.text = post.readableTitle()
            author.text = itemView.resources.getString(R.string.author, post.author)
            subscribers.text = itemView.resources.getString(R.string.subscriber_count, -1) // TODO @RUI
            excerpt.text = post.readableExcerpt()
            Glide.with(itemView.context)
                .load(post.imageUrl)
                .centerCrop()
                .into(image)
        }

        itemView.setOnClickListener { listener(post) }
    }
}

private fun Post.readableExcerpt() = HtmlCompat.fromHtml(excerpt, HtmlCompat.FROM_HTML_MODE_LEGACY)
private fun Post.readableTitle() = HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)

enum class PostViewType {
    ITEM,
    HEADER
}
