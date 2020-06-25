package com.automattic.freshlypressed.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

interface ViewModelFactory<T : ViewModel> {
    fun create(handle: SavedStateHandle): T
}
