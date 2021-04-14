package me.ethan.osrs.api.proxy;

import java.net.Proxy.Type;

public class Proxy {

    private final String ip;
    private final int port;
    private String country;
    private String responseTime = null;

    private Type type;

    private ProxyStatus status = ProxyStatus.DEAD;
    private ProxyAnonymity level = ProxyAnonymity.TRANSPARENT;

    public Proxy(String ip, int port, Type type) {
        this.ip = ip;
        this.port = port;
        this.setProxyType(type);
        System.out.println("Added "+ip+port+type);
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public String getCountry() {
        return this.country;
    }

    public Proxy setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getResponseTime() {
        return this.responseTime;
    }

    public Proxy setResponseTime(String responseTime) {
        this.responseTime = responseTime;
        return this;
    }

    public Type getProxyType() {
        return this.type;
    }

    public Proxy setProxyType(Type type) {
        this.type = type;
        return this;
    }

    public ProxyStatus getProxyStatus() {
        return this.status;
    }

    public Proxy setProxyStatus(ProxyStatus status) {
        this.status = status;
        return this;
    }

    public ProxyAnonymity getProxyAnonymity() {
        return this.level;
    }

    public Proxy setProxyAnonymity(ProxyAnonymity level) {
        this.level = level;
        return this;
    }

    public enum ProxyAnonymity {
        TRANSPARENT, ANONYMOUS, ELITE
    }

    public enum ProxyStatus {
        ALIVE, DEAD
    }
}
