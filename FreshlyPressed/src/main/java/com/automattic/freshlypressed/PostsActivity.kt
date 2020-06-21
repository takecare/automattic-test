package com.automattic.freshlypressed

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class PostsActivity : FragmentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.posts_activity)
    }
}
