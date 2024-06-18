package com.yujigu.echolink.service;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;


@Slf4j
public class ScheduledTaskPing {

    public interface ScheduledTask{
        void ping(Session userSession);
    }

    public static void startScheduledTask(ScheduledTask scheduledTask, Session userSession) {
        scheduledTask.ping(userSession);
    }

}
