package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundTaskUtils {
    public BackgroundTaskUtils() {
    }

    public void runTask(Runnable task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }
}
