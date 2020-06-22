package com.automattic.freshlypressed.presentation

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.automattic.freshlypressed.R

class NewActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
    }
}
