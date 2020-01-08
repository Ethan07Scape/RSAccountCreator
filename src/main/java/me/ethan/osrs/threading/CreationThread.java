package me.ethan.osrs.threading;

import me.ethan.osrs.data.Constants;
import me.ethan.osrs.handlers.AccountCreation;
import me.ethan.osrs.utils.Utils;

public class CreationThread extends Thread {
    private volatile boolean running = false;
    private int createdAccounts;
    private int errorAccounts;
    private AccountCreation accountCreation;

    public void run() {
        running = true;
        while (running) {
            accountCreation = new AccountCreation();
            final String response = accountCreation.getResponse();
            System.err.println("ACCOUNT RESPONSE: "+response);
            switch (response) {
                case "SUCCESS":
                    handleCreatedAccount();
                    break;
                case "ERROR":
                    handleError();
                    break;
            }
        }
    }

    private void handleCreatedAccount() {
        if (accountCreation == null)
            return;

        createdAccounts++;
        Utils.getInstance().writeAccount(accountCreation.getEmail() + ":" + Constants.BOT_PASSWORD);
        System.err.println("We created the account: " + accountCreation.getEmail() + " total: " + createdAccounts);
        if (accountCreation.getWebDriver() != null) {
            accountCreation.getWebDriver().quit();
            accountCreation.setWebDriver(null);
        }
    }

    private void handleError() {
        if (accountCreation == null)
            return;
        errorAccounts++;
        System.err.println("We ran into a error creating the account: " + accountCreation.getEmail() + " total: " + errorAccounts);
        if (accountCreation.getWebDriver() != null) {
            System.out.println(accountCreation.getWebDriver().getPageSource());
            accountCreation.getWebDriver().quit();
            accountCreation.setWebDriver(null);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
