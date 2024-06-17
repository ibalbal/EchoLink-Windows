package com.yujigu.echolink;

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
    private Session userSession = null;
    private MessageHandler messageHandler;
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
        this.userSession = userSession;
        latch.countDown();
        startHeartbeat();
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        log.info("Closing WebSocket connection: " + reason);
        try {
            this.userSession.close();
        } catch (IOException e) {
            log.error("Error closing WebSocket session", e);
        } finally {
            this.userSession = null;
            if (running) {
                scheduleReconnect();
            }
        }
    }

    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket error", throwable);
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        try {
            latch.await();
            this.userSession.getAsyncRemote().sendText(message);
        } catch (InterruptedException e) {
            log.error("Error sending message", e);
        }
    }

    public void close() {
        running = false;
        if (userSession != null && userSession.isOpen()) {
            try {
                userSession.close();
            } catch (Exception e) {
                log.error("Error closing WebSocket", e);
            }
        }
        scheduler.shutdown();
    }

    private void scheduleReconnect() {
        scheduler.schedule(this::connect, 5, TimeUnit.SECONDS);
    }

    private void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            if (userSession != null && userSession.isOpen()) {
                try {
                    userSession.getAsyncRemote().sendPing(null);
                } catch (IOException e) {
                    log.error("Error sending ping", e);
                }
            }
        }, 0, 30, TimeUnit.SECONDS);  // Send ping every 30 seconds
    }

    public static interface MessageHandler {
        void handleMessage(String message);
    }
}
