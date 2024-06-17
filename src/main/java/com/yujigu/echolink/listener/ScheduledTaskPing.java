package com.yujigu.echolink.listener;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScheduledTaskPing {

    private ScheduledExecutorService scheduler;

    public interface ScheduledTask{
        void ping(Session userSession);
    }

    public ScheduledTaskPing() {
        // 创建一个单线程的 ScheduledExecutorService
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void startScheduledTask(ScheduledTask scheduledTask, Session userSession) {
        // 初始延迟0秒，之后每隔60秒执行一次任务
        scheduler.scheduleAtFixedRate(() -> {
            // 这里写你需要执行的操作
            log.info("ping");
            scheduledTask.ping(userSession);
        }, 0, 60, TimeUnit.SECONDS);
    }

    public void stopScheduledTask() {
        // 停止任务执行并关闭 ScheduledExecutorService
//        scheduler.shutdown();
    }


}
