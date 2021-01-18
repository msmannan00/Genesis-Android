package com.darkweb.genesissearchengine.helperManager;

import com.android.volley.toolbox.HurlStack;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import static com.darkweb.genesissearchengine.constants.constants.CONST_PROXY_SOCKS;

public class ProxiedHurlStack extends HurlStack {

    private static final String PROXY_ADDRESS = CONST_PROXY_SOCKS;
    private static final int PROXY_PORT = 9050;

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS,
                InetSocketAddress.createUnresolved(PROXY_ADDRESS, PROXY_PORT));
        return (HttpURLConnection) url.openConnection(proxy);
    }
}