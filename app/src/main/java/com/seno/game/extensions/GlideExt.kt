@file:JvmName("GlideUtils")
package com.seno.game.extensions

import android.app.Application
import android.graphics.Point
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.seno.game.App

private val DEFAULT_OPTIONS = RequestOptions()
    .format(DecodeFormat.PREFER_RGB_565)
    .timeout(1000 * 10)
    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    .skipMemoryCache(false)

private val NO_CACHE_OPTIONS = RequestOptions()
    .format(DecodeFormat.PREFER_RGB_565)
    .timeout(1000 * 10)
    .diskCacheStrategy(DiskCacheStrategy.NONE)
    .skipMemoryCache(true)

private val DISK_OPTIONS = RequestOptions()
    .format(DecodeFormat.PREFER_RGB_565)
    .timeout(1000 * 10)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .skipMemoryCache(false)

fun getRequestManager(): RequestManager =
    Glide.with(App.getInstance()).applyDefaultRequestOptions(DEFAULT_OPTIONS)

fun getNoCacheRequestManager(): RequestManager =
    Glide.with(App.getInstance()).applyDefaultRequestOptions(NO_CACHE_OPTIONS)

fun getDiskCacheRequestManager(): RequestManager =
    Glide.with(App.getInstance()).applyDefaultRequestOptions(DISK_OPTIONS)

fun String.saveDiskCacheData(size: Point?) {
    if (size != null) {
        getDiskCacheRequestManager().downloadOnly().load(this).submit(size.x, size.y)
    } else {
        getDiskCacheRequestManager().downloadOnly().load(this).submit()
    }
}

fun List<String>.saveDiskCacheData(size: Point?) {
    this.forEach { url ->
        if (size != null) {
            getDiskCacheRequestManager().downloadOnly().load(url).submit(size.x, size.y)
        } else {
            getDiskCacheRequestManager().downloadOnly().load(url).submit()
        }
    }
}

fun clearMemoryCache() {
    Glide.get(App.getInstance()).clearMemory()
}

fun Application.clearDiskCache() {
    Thread { Glide.get(this).clearDiskCache() }.start()
}