package me.ethan.osrs.api.proxy;

import javafx.util.Pair;
import me.ethan.osrs.data.Constants;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

public class ProxyChecker {
    private static ProxyChecker instance;
    private final ProxyRequest proxyRequest;

    public ProxyChecker() {
        proxyRequest = new ProxyRequest();
    }

    public static ProxyChecker getInstance() {
        if (instance == null)
            instance = new ProxyChecker();
        return instance;
    }

    private void setProxyStatus(Proxy p) {
        final java.net.Proxy proxy = new java.net.Proxy(p.getProxyType(), new InetSocketAddress(p.getIp(), p.getPort()));
        final Pair<HttpURLConnection, Long> pair = proxyRequest.connect(proxy);
        if (pair != null) {
            try {
                p.setProxyStatus(Proxy.ProxyStatus.ALIVE);
                ProxyRequest.Response response = proxyRequest.getResponse(pair.getKey());
                p.setProxyAnonymity(response.anonymity);
                p.setCountry(response.country);
                p.setResponseTime(pair.getValue() + " (ms)");
            } catch (Exception e) {
                p.setProxyStatus(Proxy.ProxyStatus.DEAD);
                p.setProxyAnonymity(null);
            }
        } else {
            p.setProxyStatus(Proxy.ProxyStatus.DEAD);
            p.setProxyAnonymity(null);
        }
    }

    public boolean isProxyWorking(Proxy p) {
        setProxyStatus(p);
        final String host = p.getIp();
        final int port = p.getPort();
        if (p.getProxyStatus() == Proxy.ProxyStatus.ALIVE) {
            if (p.getIp().equals("p.webshare.io")) {
                return true;
            }
            if (connectsToRuneScape(p)) {
                System.out.println(host + ":" + port + " - passed RuneScape.");
                return true;
            } else {
                System.err.println(host + ":" + port + " - did not pass RuneScape");
            }
        } else if (p.getProxyStatus() == Proxy.ProxyStatus.DEAD) {
            System.err.println(host + ":" + port + " - DEAD");
        }
        return false;
    }

    private boolean connectsToRuneScape(Proxy p) {
        try {
            final java.net.Proxy proxy;
            if (p.getProxyType().equals(java.net.Proxy.Type.HTTP)){
                proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(p.getIp(), p.getPort()));
            }else{
                proxy = new java.net.Proxy(java.net.Proxy.Type.SOCKS, new InetSocketAddress(p.getIp(), p.getPort()));
            }
            final HttpsURLConnection connection = (HttpsURLConnection) new URL(Constants.CREATE_LINK).openConnection(proxy);
            if (connection != null) {
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                final int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    final StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    if (response.toString().contains("content by sharing my data")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
