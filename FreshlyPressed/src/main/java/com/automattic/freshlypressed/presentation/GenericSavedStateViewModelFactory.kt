package com.automattic.freshlypressed.presentation

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

class GenericSavedStateViewModelFactory<T : ViewModel>(
    private val viewModelFactory: ViewModelFactory<T>,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return if (modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            viewModelFactory.create(handle)
        } else {
            throw IllegalStateException("")
        } as T
    }
}
