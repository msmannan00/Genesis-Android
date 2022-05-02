/*
 * Copyright 2012-2016 Nathan Freitas

 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hiddenservices.onionservices.libs.netcipher.proxy;

import android.content.Context;
import android.content.Intent;

public interface ProxyHelper {

    boolean isInstalled(Context context);

    void requestStatus(Context context);

    boolean requestStart(Context context);

    Intent getInstallIntent(Context context);

    Intent getStartIntent(Context context);

    String getName();

    String FDROID_PACKAGE_NAME = "org.fdroid.fdroid";
    String PLAY_PACKAGE_NAME = "com.android.vending";

    /**
     * A request to Orbot to transparently start Tor services
     */
    String ACTION_START = "android.intent.action.PROXY_START";
    /**
     * {@link Intent} send by Orbot with {@code ON/OFF/STARTING/STOPPING} status
     */
    String ACTION_STATUS = "android.intent.action.PROXY_STATUS";
    /**
     * {@code String} that contains a status constant: {@link #STATUS_ON},
     * {@link #STATUS_OFF}, {@link #STATUS_STARTING}, or
     * {@link #STATUS_STOPPING}
     */
    String EXTRA_STATUS = "android.intent.extra.PROXY_STATUS";

    String EXTRA_PROXY_PORT_HTTP = "android.intent.extra.PROXY_PORT_HTTP";
    String EXTRA_PROXY_PORT_SOCKS = "android.intent.extra.PROXY_PORT_SOCKS";

    /**
     * A {@link String} {@code packageName} for Orbot to direct its status reply
     * to, used in {@link #ACTION_START} {@link Intent}s sent to Orbot
     */
    String EXTRA_PACKAGE_NAME = "android.intent.extra.PROXY_PACKAGE_NAME";

    /**
     * All tor-related services and daemons are stopped
     */
    String STATUS_OFF = "OFF";
    /**
     * All tor-related services and daemons have completed starting
     */
    String STATUS_ON = "ON";
    String STATUS_STARTING = "STARTING";
    String STATUS_STOPPING = "STOPPING";
    /**
     * The user has disabled the ability for background starts triggered by
     * apps. Fallback to the old Intent that brings up Orbot.
     */
    String STATUS_STARTS_DISABLED = "STARTS_DISABLED";
}

