package me.ethan.osrs.api.proxy;

import com.google.gson.Gson;
import javafx.util.Pair;
import me.ethan.osrs.core.Core;
import me.ethan.osrs.data.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ProxyRequest {

    public Response getResponse(String json) {
        return new Gson().fromJson(json, Response.class);
    }

    public Response getResponse(HttpURLConnection connection) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }
        return new Gson().fromJson(sb.toString(), Response.class);
    }

    public Pair<HttpURLConnection, Long> connect(Proxy proxy) {
        try {
            HttpURLConnection connection;
            if (proxy == null) {
                connection = (HttpURLConnection) new URL(Constants.PROXY_API).openConnection();
            } else {
                connection = (HttpURLConnection) new URL(this.getQueryURL(Core.getRealIp())).openConnection(proxy);
            }
            connection.setRequestProperty("User-Agent", "Proxy Checker v.1.1" +
                    " - (proxychecker.co) : " + System.getProperty("os.name") +
                    " v." + System.getProperty("os.version"));
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            long startTime = System.currentTimeMillis();
            connection.connect();
            long endTime = System.currentTimeMillis();
            return new Pair<>(connection, (endTime - startTime));
        } catch (IOException e) {
            return null;
        }
    }

    public String getIP() {
        try {
            return getResponse(connect(null).getKey()).ip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    private String getQueryURL(String ip) {
        if (ip == null) {
            return Constants.PROXY_API;
        } else {
            return Constants.PROXY_API + "?ip=" + ip;
        }
    }

    public class Response {
        public String ip;
        public String country;
        public me.ethan.osrs.api.proxy.Proxy.ProxyAnonymity anonymity;
    }

}
