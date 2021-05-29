package com.darkweb.genesissearchengine.appManager.kotlinHelperLibraries

import android.content.Context
import android.widget.ImageView
import mozilla.components.browser.engine.gecko.fetch.GeckoViewFetchClient
import mozilla.components.browser.icons.BrowserIcons
import mozilla.components.browser.icons.IconRequest
import mozilla.components.browser.icons.generator.DefaultIconGenerator
import org.mozilla.geckoview.GeckoRuntime
import java.util.concurrent.TimeUnit

class BrowserIconManager {

    fun onLoadIconIntoView(mContext: Context, mRuntime: GeckoRuntime, mView:ImageView, pURL:String) : BrowserIcons {
        val fetchClient = GeckoViewFetchClient(mContext,mRuntime, Pair(10L, TimeUnit.MINUTES))
        val mIcons = BrowserIcons(mContext, httpClient = fetchClient, generator = DefaultIconGenerator())
        mIcons.loadIntoView(mView,IconRequest(pURL))
        return mIcons
    }

    fun onLoadIcon(mContext: Context, mRuntime: GeckoRuntime) {
        val fetchClient = GeckoViewFetchClient(mContext,mRuntime, Pair(10L, TimeUnit.MINUTES))
        BrowserIcons(mContext, httpClient = fetchClient, generator = DefaultIconGenerator())
    }
}