package com.wpr.zk.hostprovider;

import java.util.concurrent.*;

/**
 * 简单的定时任务
 * Created by peirong.wpr on 2017/4/11.
 */
public class TimerService {
    static public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
                                                            long delay, TimeUnit unit) {
        return scheduledExecutor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }


    static ScheduledExecutorService scheduledExecutor = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("com.taobao.diamond.client.Timer");
                    t.setDaemon(true);
                    return t;
                }
            });
}
