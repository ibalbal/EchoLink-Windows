package com.yujigu.echolink;

import com.alibaba.fastjson2.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.symxns.sym.jni.windows.Clipboard.Clipboard;
import com.yujigu.echolink.listener.ClipboardListener;
import com.yujigu.echolink.listener.ScheduledTaskPing;
import com.yujigu.echolink.model.DeviceType;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class EchoLinkApplication extends Application {
    private boolean isRunning = false;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private TextField textField;
    private Button toggleButton;
    private ImageView qrCodeImageView;
    private WebSocketClient client;
    private ScheduledTaskPing scheduledTaskPing;;

    @Override
    public void start(Stage primaryStage) {
        textField = new TextField();
        Button generateKeyButton = new Button("随机密钥");
        toggleButton = new Button("开启");
        qrCodeImageView = new ImageView();

        generateKeyButton.setOnAction(e -> {
            //SBjXXAgTVrQUEK6qt3EQ
            String randomKey = generateRandomKey(32);
            textField.setText(randomKey);
        });

        toggleButton.setOnAction(e -> {
            if (isRunning) {
                stopGeneration();
            } else {
                startGeneration();
            }
        });

        // 设置按钮和文本框的宽度
        generateKeyButton.setMaxWidth(300);
        toggleButton.setMaxWidth(300);
        textField.setMaxWidth(300);

        // 调整布局
        HBox topLayout = new HBox(10, generateKeyButton, textField, toggleButton);
        topLayout.setPadding(new Insets(10));
        VBox rootLayout = new VBox(20, topLayout, qrCodeImageView);
        rootLayout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #f0f0f0;");
        topLayout.setStyle("-fx-alignment: center;");

        textField.setStyle("-fx-font-size: 10pt");
        generateKeyButton.setStyle("-fx-font-size: 10pt");
        toggleButton.setStyle("-fx-font-size: 10pt");

        qrCodeImageView.setFitWidth(300);
        qrCodeImageView.setFitHeight(300);

        Scene scene = new Scene(rootLayout, 380, 400);
        primaryStage.setTitle("EchoLink");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGeneration() {
        toggleButton.setText("停止");
        textField.setDisable(true);
        String secretKey = textField.getText();
        Image qrImage = generateQRCodeImage(secretKey);
        if (qrImage != null) {
            qrCodeImageView.setImage(qrImage);
        }
        isRunning = true;

        //开启
        startAction(secretKey);
    }

    private void stopGeneration() {
        toggleButton.setText("开启");
        textField.setDisable(false);
        isRunning = false;

        //停止
        stopAction();
    }

    private String generateRandomKey(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder(length);
        while (key.length() < length) {
            char ch = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
            if (ch != '#' && ch != '/' && ch != '\\') {
                key.append(ch);
            }
        }
        return key.toString();
    }

    private Image generateQRCodeImage(String text) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            return new Image(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void connectWebSocket(String secretKey, String deviceId) {
        try {
            String ws = "ws://ocean.ibalbal.com/ocean-bitong-ws/websocket/" + secretKey + "/" + deviceId;
            log.info("链接地址：{}", ws);
            if (client == null){
                client = new WebSocketClient(new URI(ws));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopWebSocket() {
        scheduledTaskPing.stopScheduledTask();
        client.close();
        client = null;
    }


    //心跳
    private void startPing(){
        scheduledTaskPing.startScheduledTask(() -> client.sendMessage("ping"));
    }

    private void startAction(String secretKey){
        //生产随机设备id
        String deviceId = UUID.randomUUID().toString().replaceAll("-","");

        //创建webSocket链接
        connectWebSocket(secretKey, deviceId);
        //创建剪切板监听事件
        Clipboard.getInstance(new ClipboardListener(client, new Datas(deviceId, secretKey, DeviceType.WINDOWS)));
        //收到消息-设置剪切板中
        client.addMessageHandler(message -> {
            log.info("收到消息：{}" + message);
            Datas datas = JSONObject.parseObject(message, Datas.class);
            if (datas.getContent() == null || Objects.equals(datas.getContent(), "")){
                log.info("内容消息体为空不做处理");
                return;
            }
            //将数据设置到剪切板
            Clipboard.setClipboard(datas.getContent());
        });

        scheduledTaskPing  = new ScheduledTaskPing();
        startPing();
    }



    private void stopAction(){
        stopWebSocket();
        Clipboard.destroy();
    }

    public static void main(String[] args) {
        launch();
    }
}