package com.yujigu.echolink.service.impl;

import com.yujigu.echolink.service.ScheduledTaskPing;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;

@Slf4j
public class ScheduledTaskPingImpl implements ScheduledTaskPing.ScheduledTask {
    @Override
    public void ping(Session userSession) {
        try {
            userSession.getAsyncRemote().sendPing(null);
        } catch (IOException e) {
            log.error("Error sending ping", e);
        }
    }
}
