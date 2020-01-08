package me.ethan.osrs.threading;

import me.ethan.osrs.api.proxy.Proxy;
import me.ethan.osrs.api.proxy.ProxyHandler;
import me.ethan.osrs.data.Constants;
import me.ethan.osrs.handlers.AccountCreation;
import me.ethan.osrs.utils.Condition;
import me.ethan.osrs.utils.Utils;

public class CreationThread extends Thread {
    private volatile boolean running = false;
    private int createdAccounts;
    private int errorAccounts;
    private AccountCreation accountCreation;
    public void run() {
        running = true;
        while (running) {
            try {
                final Proxy proxy = grabProxy();
                accountCreation = new AccountCreation(proxy);
                final String response = accountCreation.getResponse();
                System.err.println("ACCOUNT RESPONSE: " + response);
                switch (response) {
                    case "SUCCESS":
                        handleCreatedAccount();
                        break;
                    case "ERROR":
                        handleError();
                        break;
                }
                Condition.sleep(200);
            } catch (Exception e) {

            }
        }
    }

    private void handleCreatedAccount() {
        if (accountCreation == null)
            return;
        createdAccounts++;
        Utils.getInstance().writeAccount(accountCreation.getEmail() + ":" + Constants.BOT_PASSWORD);
        System.err.println("We created the account: " + accountCreation.getEmail() + " total: " + createdAccounts);
    }

    private void handleError() {
        if (accountCreation == null)
            return;
        errorAccounts++;
        System.err.println("We ran into a error creating the account: " + accountCreation.getEmail() + " total: " + errorAccounts);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public final Proxy grabProxy() {
        if (!Constants.USE_PROXIES)
            return null;
        return ProxyHandler.getInstance().getNextWorkingProxy();
    }

    public int getCreatedAccounts() {
        return createdAccounts;
    }
}
