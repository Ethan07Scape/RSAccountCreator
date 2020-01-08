package me.ethan.osrs.api;


import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HttpAPI {
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0";
    private final CookieStore cookieStore = new BasicCookieStore();


    public String get(final String url) throws Exception {
        final HttpGet request = new HttpGet(url);
        request.setHeader("User-Agent", USER_AGENT);
        request.setHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");

        final HttpResponse response = getHttpClient().execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        final BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        final StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    private final CloseableHttpClient getHttpClient() {
        return HttpClientBuilder.create().disableAutomaticRetries().setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCookieStore(cookieStore).build();
    }
}
