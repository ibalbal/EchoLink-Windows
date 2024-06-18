package com.yujigu.echolink;

import com.yujigu.echolink.service.ScheduledTaskPing;
import com.yujigu.echolink.service.impl.ScheduledTaskPingImpl;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@ClientEndpoint
public class WebSocketClient {
    private static Session userSession;
    private static MessageHandler messageHandler;
    private CountDownLatch latch = new CountDownLatch(1);
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private URI endpointURI;
    private boolean running = true;

    public WebSocketClient(URI endpointURI) {
        this.endpointURI = endpointURI;
        connect();
    }

    private void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            log.error("Failed to connect to server", e);
            scheduleReconnect();
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        log.info("Opening WebSocket connection");
        WebSocketClient.userSession = userSession;
        latch.countDown();
        startHeartbeat(scheduler);
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        log.info("Closing WebSocket connection: " + reason);
        try {
            WebSocketClient.userSession.close();
        } catch (IOException e) {
            log.error("Error closing WebSocket session", e);
        } finally {
            WebSocketClient.userSession = null;
            if (running) {
                scheduleReconnect();
            }
        }
    }

    @OnMessage
    public void onMessage(String message) {
        if (messageHandler != null) {
            messageHandler.handleMessage(message);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket error", throwable);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        log.info("WebSocketClient is being collected.");
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        try {
            if (WebSocketClient.userSession == null){
                log.error("userSession is null");
                return;
            }
            latch.await();
            WebSocketClient.userSession.getAsyncRemote().sendText(message);
        } catch (InterruptedException e) {
            log.error("Error sending message", e);
        }
    }

    public void close() {
        running = false;
        if (WebSocketClient.userSession != null && WebSocketClient.userSession.isOpen()) {
            try {
                WebSocketClient.userSession.close();
            } catch (Exception e) {
                log.error("Error closing WebSocket", e);
            }
        }
        scheduler.shutdown();

    }

    private void scheduleReconnect() {
        scheduler.schedule(this::connect, 5, TimeUnit.SECONDS);
    }

    private void startHeartbeat(ScheduledExecutorService scheduler) {
        scheduler.scheduleAtFixedRate(() -> {
            if (userSession != null && userSession.isOpen()) {
                log.info("Sending ping");
                ScheduledTaskPing.startScheduledTask(new ScheduledTaskPingImpl(), userSession);
            }
        }, 0, 30, TimeUnit.SECONDS);  // Send ping every 30 seconds
    }

    public static interface MessageHandler {
        void handleMessage(String message);
    }
}
