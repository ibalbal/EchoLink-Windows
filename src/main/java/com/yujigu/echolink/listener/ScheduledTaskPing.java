package com.yujigu.echolink.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskPing {

    private ScheduledExecutorService scheduler;

    public interface ScheduledTask{
        void ping();
    }

    public ScheduledTaskPing() {
        // 创建一个单线程的 ScheduledExecutorService
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void startScheduledTask(ScheduledTask scheduledTask) {
        // 初始延迟0秒，之后每隔60秒执行一次任务
        scheduler.scheduleAtFixedRate(() -> {
            // 这里写你需要执行的操作
            System.out.println("ping");
            scheduledTask.ping();
        }, 0, 60, TimeUnit.SECONDS);
    }

    public void stopScheduledTask() {
        // 停止任务执行并关闭 ScheduledExecutorService
        scheduler.shutdown();
    }

    
}
