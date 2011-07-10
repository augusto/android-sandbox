package com.augusto.mymediaplayer.common;

public final class ThreadUtil {
    
    private ThreadUtil() {}

    public static void sleep(int timeInMillis) {
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException e) { }
    }
}
