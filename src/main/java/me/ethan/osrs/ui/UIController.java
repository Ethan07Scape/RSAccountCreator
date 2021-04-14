package me.ethan.osrs.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import me.ethan.osrs.api.proxy.ProxyHandler;
import me.ethan.osrs.data.Constants;
import me.ethan.osrs.threading.CreationThread;
import me.ethan.osrs.utils.Condition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UIController {
    private final List<CreationThread> threadList = new ArrayList<>();
    @FXML
    private Label proxiesLoaded;
    @FXML
    private Label accountsMade;
    @FXML
    private TextField threadCount;
    @FXML
    private TextField apiKey;
    @FXML
    private Button startButton;

    public void start() {
        final int threads = getThreadCount();
        if (threads == -1 || threads == 0) {
            System.err.println("Please enter a correct amount of threads.");
            return;
        }
        if (ProxyHandler.getInstance().getProxySize() <= 0) {
            System.err.println("I suggest you load proxies...");
            Constants.USE_PROXIES = false;
        }
        Constants.CAPTCHA_KEY = this.apiKey.getText();
        Constants.THREAD_AMOUNT = threads;
        createWorkerThreads();
        startThreads();
        startUpdateDaemonTask();
        startButton.setDisable(true);
    }

    public void loadProxies() {
        try {
            final FileChooser fileChooser = new FileChooser();
            final File selectedFile = fileChooser.showOpenDialog(apiKey.getScene().getWindow());
            if (selectedFile.getAbsolutePath().endsWith(".txt")) {
                ProxyHandler.getInstance().readPath(selectedFile.getAbsolutePath());
            }
            proxiesLoaded.setText(ProxyHandler.getInstance().getStartAmount() + "");
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private int getThreadCount() {
        try {
            return Integer.parseInt(threadCount.getText());
        } catch (Exception e) {
            System.err.println("That probably wasn't a valid integer.");
        }
        return -1;
    }


    private void createWorkerThreads() {
        for (int i = 0; i < Constants.THREAD_AMOUNT; i++) {
            final CreationThread creationThread = new CreationThread();
            threadList.add(creationThread);
        }
    }

    private void startThreads() {
        for (CreationThread thread : threadList) {
            thread.start();
            Condition.sleep(50);
        }
    }

    private void startUpdateDaemonTask() {
        final Task task = new Task<Void>() {
            @Override
            public Void call() {
                while (true) {
                    Platform.runLater(() -> update());
                    Condition.sleep(1000);
                }
            }
        };
        final Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void update() {
        int made = 0;
        for (CreationThread t : threadList) {
            made += t.getCreatedAccounts();
        }
        accountsMade.setText(made + "");
    }
}
