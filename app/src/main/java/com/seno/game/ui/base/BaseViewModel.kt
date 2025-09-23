package com.seno.game.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {
    private val _toast = MutableSharedFlow<String>()
    val toast get() = _toast.asSharedFlow()

    private val _loading = MutableStateFlow(false)
    val loading get() = _loading.asStateFlow()

    private val _finish = MutableSharedFlow<Unit>()
    val finish get() = _finish.asSharedFlow()

    private val _networkErrorDialog = MutableStateFlow(false)
    val networkErrorDialog get() = _networkErrorDialog.asStateFlow()

    fun finish() = vmScopeJob { _finish.emit(Unit) }

    fun showToast(message: String) = vmScopeJob { _toast.emit(message) }

    fun showLoading()  = _loading.update { true }

    fun hideLoading() = _loading.update { false }

    fun showNetworkErrorDialog(isShow: Boolean) = _networkErrorDialog.update { isShow }

    protected val exceptionHandler = CoroutineExceptionHandler { _, t ->
        t.printStackTrace()
        Timber.e("$javaClass exceptionHandler -> ${t.message}")
        hideLoading()
    }

    fun vmScopeJob(
        defaultHandler: CoroutineExceptionHandler = exceptionHandler,
        actionInExceptionHandler: () -> Unit = {},
        enableLoadingOption: Boolean = false,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val combinedCoroutineContext = CoroutineExceptionHandler { context, t ->
            defaultHandler.handleException(context, t)
            actionInExceptionHandler.invoke()
        } + Dispatchers.IO + SupervisorJob()
        viewModelScope.launch(context = combinedCoroutineContext) {
            if (enableLoadingOption) showLoading()
            block()
            if (enableLoadingOption) hideLoading()
        }
    }

    suspend fun <T> customWithContext(
        coroutineContext: CoroutineContext = Dispatchers.IO,
        actionInExceptionHandler: () -> Unit = {},
        enableLoadingOption: Boolean = false,
        block: suspend CoroutineScope.() -> T
    ): T? {
        return withContext(coroutineContext) {
            try {
                if (enableLoadingOption) showLoading()
                block()
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("$javaClass fallback exception -> ${e.message}")
                hideLoading()
                actionInExceptionHandler()
                null
            } finally {
                if (enableLoadingOption) hideLoading()
            }
        }
    }
}