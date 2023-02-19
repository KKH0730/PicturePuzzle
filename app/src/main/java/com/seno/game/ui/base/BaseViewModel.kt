package com.seno.game.ui.base

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    protected val isLoading get() = _isLoading.asStateFlow()

    fun showLoading(isShow: Boolean) {
        _isLoading.value = isShow
    }
}