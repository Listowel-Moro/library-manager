package listo.librarymanager.utils;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;

public class JavaFXInitializer {
    private static boolean isJavaFXInitialized = false;

    public static synchronized void initializeJavaFX() throws InterruptedException {
        if (!isJavaFXInitialized) {
            if (!Platform.isFxApplicationThread()) {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.startup(latch::countDown);
                latch.await();
            }
            isJavaFXInitialized = true;
        }
    }
}

