package com.bgsoftware.wildbuster.utils;

import java.util.Timer;
import java.util.TimerTask;

public final class TimerUtils {

    public static void runTimer(Timer timer, Runnable runnable, long period){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, 0L, period * 50);
    }

}
