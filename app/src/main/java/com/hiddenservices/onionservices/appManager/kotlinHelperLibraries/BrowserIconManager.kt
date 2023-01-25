package com.hiddenservices.onionservices.appManager.kotlinHelperLibraries

import android.content.Context
import android.widget.ImageView
import mozilla.components.browser.engine.gecko.fetch.GeckoViewFetchClient
import mozilla.components.browser.icons.BrowserIcons
import mozilla.components.browser.icons.IconRequest
import mozilla.components.browser.icons.generator.DefaultIconGenerator
import org.mozilla.geckoview.GeckoRuntime
import java.util.concurrent.TimeUnit

class BrowserIconManager {

    var fetchClient: GeckoViewFetchClient? = null
    var mIcons: BrowserIcons? = null

    fun init(
        mContext: Context,
        mRuntime: GeckoRuntime,
    ){
        fetchClient = GeckoViewFetchClient(mContext, mRuntime, Pair(1L, TimeUnit.SECONDS))
        mIcons = BrowserIcons(mContext, httpClient = fetchClient!!, generator = DefaultIconGenerator())
    }

    fun onLoadIconIntoView(
        mView: ImageView,
        pURL: String
    ): BrowserIcons {
        mIcons?.loadIntoView(mView, IconRequest(pURL))
        return mIcons!!
    }

    fun onLoadIcon(mContext: Context, mRuntime: GeckoRuntime) {
        val fetchClient = GeckoViewFetchClient(mContext, mRuntime, Pair(1L, TimeUnit.SECONDS))
        BrowserIcons(mContext, httpClient = fetchClient, generator = DefaultIconGenerator())
    }
}