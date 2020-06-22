package com.automattic.freshlypressed.presentation

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.automattic.freshlypressed.R

class PostsActivity : FragmentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.posts_activity)
    }
}
