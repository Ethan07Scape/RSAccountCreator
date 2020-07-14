package me.ethan.osrs.api;


import me.ethan.osrs.api.proxy.Proxy;
import me.ethan.osrs.data.Constants;
import me.ethan.osrs.utils.Random;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HttpAPI {
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0";
    private final CookieStore cookieStore = new BasicCookieStore();


    public String get(final String url, final Proxy proxyAddress) throws Exception {
        final HttpGet request = new HttpGet(url);
        if (proxyAddress != null) {
            final HttpHost proxy = new HttpHost(proxyAddress.getIp(), proxyAddress.getPort(), "http");

            final RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .setConnectionRequestTimeout(10000)
                    .setSocketTimeout(10000)
                    .setConnectTimeout(10000)
                    .build();
            request.setConfig(config);
        }
        request.setHeader("User-Agent", USER_AGENT);
        request.setHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");

        final HttpResponse response = getHttpClient().execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        final BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        final StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    public String post(final String url, final List<NameValuePair> nameValuePairs, final Proxy proxyAddress)
            throws Exception {

        final HttpPost post = new HttpPost(url);
        if (proxyAddress != null) {
            final HttpHost proxy = new HttpHost(proxyAddress.getIp(), proxyAddress.getPort(), "http");

            final RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .setConnectionRequestTimeout(10000)
                    .setSocketTimeout(10000)
                    .setConnectTimeout(10000)
                    .build();
            post.setConfig(config);
        }
        post.setHeader("Host", "secure.runescape.com");
        post.setHeader("Origin", "https://secure.runescape.com");
        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        post.setHeader("Accept-Language", "en-US,en;q=0.9");
        post.setHeader("Accept-Encoding", "gzip, deflate, br");
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Cache-Control", "max-age=0");
        post.setHeader("Referer", Constants.CREATE_LINK);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Upgrade-Insecure-Requests", "1");
        post.setHeader("Sec-Fetch-Mode", "navigate");
        post.setHeader("Sec-Fetch-Site", "same-origin");
        post.setHeader("Sec-Fetch-User", "?1");

        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        final HttpResponse response = getHttpClient().execute(post);

        final int responseCode = response.getStatusLine().getStatusCode();

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return result.toString();

    }

    public List<NameValuePair> getFilledForm(String html, String response, String email) throws UnsupportedEncodingException {

        final Document doc = Jsoup.parse(html);

        final Element loginForm = doc.getElementsByAttributeValue("action", "create_account").get(0);
        final Elements inputElements = loginForm.getElementsByTag("input");

        final List<NameValuePair> paramList = new ArrayList<>();

        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");
            switch (key) {
                case "day":
                    value = "" + Random.nextInt(1,30);
                    break;
                case "month":
                    value = "" + Random.nextInt(1,12);
                    break;
                case "year":
                    value = "19" + Random.nextInt(70, 99);
                    break;
                case "email1":
                    value = email;
                    break;
                case "password1":
                    value = Constants.BOT_PASSWORD;
                    break;
            }
            if (key.length() > 0 && value.length() > 0) {
                //System.err.println("Key: "+key + " - "+value);
                paramList.add(new BasicNameValuePair(key, value));
            }
        }
        paramList.add(new BasicNameValuePair("g-recaptcha-response", response));
        return paramList;
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClientBuilder.create().disableAutomaticRetries().setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCookieStore(cookieStore).build();
    }
}
