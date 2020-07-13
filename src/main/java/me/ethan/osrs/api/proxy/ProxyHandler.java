package me.ethan.osrs.api.proxy;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
        System.out.println("Reading path "+path);

        try
        {
            FileInputStream fstream_school = new FileInputStream(path);
            DataInputStream data_input = new DataInputStream(fstream_school);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input));
            String str_line;

            while ((str_line = buffer.readLine()) != null)
            {
                str_line = str_line.trim();
                if ((str_line.length()!=0))
                {
                    str_line = str_line.replaceAll("[^0-9.]", "");
                    addToQueue(str_line);
                }
            }
        }catch(Exception e){
            System.err.println("File Read Error "+e.getMessage());
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
            String[] detail = proxyString.split(":");
            if (detail[0].startsWith("S")) { // ScrapeBox has a capital S in front of all exported SOCKS proxies
                detail[0] = detail[0].replaceFirst("S", "");
                return new Proxy(detail[0], Integer.parseInt(detail[1]), java.net.Proxy.Type.SOCKS);
            }else {
                return new Proxy(detail[0], Integer.parseInt(detail[1]), java.net.Proxy.Type.HTTP);
            }
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
            System.out.println("Using Proxy: " + p.getIp() + ":" + p.getPort() + " - " + p.getResponseTime());
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
