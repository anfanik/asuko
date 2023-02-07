package me.anfanik.asuko;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RestartHelper {

    public static final String KEY_CAN_RESTART = "ASUKA_CAN_RESTART";
    public static final String KEY_APPROVE_RESTART = "ASUKA_APPROVE_RESTART";
    private static final int DELAY = 10;

    public static void setHandler(Supplier<Boolean> handler, Executor executor) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            executor.execute(() -> {
                boolean value = handler.get();
                System.setProperty(KEY_CAN_RESTART, String.valueOf(value));
            });
        }, DELAY, DELAY, TimeUnit.SECONDS);
    }

    public static void approveRestart() {
        System.setProperty(KEY_APPROVE_RESTART, "true");
    }

    public static boolean isCanRestart() {
        return Boolean.parseBoolean(System.getProperty(KEY_CAN_RESTART, "false"));
    }

    public static boolean isApprovedRestart() {
        return Boolean.parseBoolean(System.getProperty(KEY_APPROVE_RESTART, "false"));
    }
}