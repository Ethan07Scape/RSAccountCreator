package me.ethan.osrs.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Utils {
    private static Utils instance = new Utils();

    public static Utils getInstance() {
        return instance;
    }

    public synchronized void writeAccount(String account) {
        try {
            final String txtPath = System.getProperty("user.home") + "\\Desktop\\New Accounts.txt";
            final File file = new File(txtPath);
            final FileWriter fw = new FileWriter(file, true);
            final BufferedWriter bw = new BufferedWriter(fw);
            bw.write(account);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
