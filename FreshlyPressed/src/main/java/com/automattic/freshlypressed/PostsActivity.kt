package com.automattic.freshlypressed

import android.app.Activity
import android.os.Bundle

class PostsActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.posts_activity)
    }
}
