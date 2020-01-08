package me.ethan.osrs.core;

import me.ethan.osrs.api.proxy.ProxyRequest;
import me.ethan.osrs.ui.MainUI;


public class Core {
    private static String REAL_IP = "0.0.0.0";

    public static void main(String[] args) {
        REAL_IP = new ProxyRequest().getIP();
        System.err.println("Original IP: " + REAL_IP);
        final MainUI ui = new MainUI();
    }

    public static String getRealIp() {
        return REAL_IP;
    }

}
