package me.ethan.osrs.core;

import me.ethan.osrs.data.Constants;
import me.ethan.osrs.threading.CreationThread;

import java.util.ArrayList;
import java.util.List;

public class Core {
    private static final List<CreationThread> threadList = new ArrayList<>();
    public static void main(String[] args) {
        createWorkerThreads();
        startThreads();
    }

    private static void createWorkerThreads() {
        for(int i = 0; i < Constants.THREAD_AMOUNT; i++) {
            final CreationThread creationThread = new CreationThread();
            threadList.add(creationThread);
        }
    }

    private static void startThreads() {
        for(CreationThread thread : threadList) {
            thread.start();
        }
    }
}
