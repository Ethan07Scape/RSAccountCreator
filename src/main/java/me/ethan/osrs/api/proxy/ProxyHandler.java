package me.ethan.osrs.api.proxy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Stream;

public class ProxyHandler {
    private static ProxyHandler instance;
    private final ArrayBlockingQueue<String> proxies;
    private int startAmount;
    private String lastPath;

    public ProxyHandler() {
        proxies = new ArrayBlockingQueue<>(100000);
    }

    public static ProxyHandler getInstance() {
        if (instance == null)
            instance = new ProxyHandler();
        return instance;
    }

    public void readPath(final String path) {
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            stream.forEach(a -> addToQueue(a));
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastPath = path;
        startAmount = proxies.size();
    }

    private void addToQueue(String line) {
        if (proxies.size() < 100000) {
            proxies.add(line);
        }
    }

    private synchronized Proxy getNextInQueue() {
        if (proxies.size() <= 0) {
            if (lastPath.length() > 0) {
                readPath(lastPath);
            }
        }
        try {
            final String proxyString = proxies.take();
            final String host = proxyString.split(":")[0];
            final String port = proxyString.split(":")[1];
            return new Proxy(host, Integer.parseInt(port), java.net.Proxy.Type.HTTP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized Proxy getNextWorkingProxy() {
        final Proxy p = getNextInQueue();
        if (p == null)
            return null;
        if (ProxyChecker.getInstance().isProxyWorking(p)) {
            System.err.println("Using Proxy: " + p.getIp() + ":" + p.getPort() + " - " + p.getResponseTime());
            return p;
        } else {
            return getNextWorkingProxy();
        }
    }

    public int getProxySize() {
        return proxies.size();
    }

    public int getStartAmount() {
        return startAmount;
    }
}
