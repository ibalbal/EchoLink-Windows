package com.yujigu.echolink;

import javax.websocket.*;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class WebSocketClient {
    private Session userSession = null;
    private MessageHandler messageHandler;
    private CountDownLatch latch = new CountDownLatch(1);

    public WebSocketClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("Opening WebSocket connection");
        this.userSession = userSession;
        latch.countDown();
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("Closing WebSocket connection");
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        try {
            latch.await();
            this.userSession.getAsyncRemote().sendText(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (userSession != null && userSession.isOpen()) {
            try {
                userSession.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static interface MessageHandler {
        void handleMessage(String message);
    }
}
