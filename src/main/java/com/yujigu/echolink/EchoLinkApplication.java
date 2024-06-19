package com.yujigu.echolink;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.symxns.sym.jni.windows.Clipboard.Clipboard;
import com.yujigu.echolink.aes.AES;
import com.yujigu.echolink.aes.AESMode;
import com.yujigu.echolink.aes.Encryption;
import com.yujigu.echolink.aes.impl.AESEncryption;
import com.yujigu.echolink.aes.impl.OFBMode;
import com.yujigu.echolink.listener.ClipboardListener;
import com.yujigu.echolink.listener.ClipboardPreHandlerListener;
import com.yujigu.echolink.listener.WebSocketClient;
import com.yujigu.echolink.model.Datas;
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
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.UUID;

@Slf4j
public class EchoLinkApplication extends Application {
    private boolean isRunning = false;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private TextField textField;
    private Button toggleButton;
    private ImageView qrCodeImageView;
    private WebSocketClient client;
    private TextField ivTextField;
    private TextField keyTextField;
    @Override
    public void start(Stage primaryStage) {
        textField = new TextField();
        Button generateKeyButton = new Button("随机密钥");
        toggleButton = new Button("开启");
        qrCodeImageView = new ImageView();

        generateKeyButton.setOnAction(e -> {
            //SBjXXAgTVrQUEK6qt3EQ
            String randomKey = generateRandomKey(32);
            textField.setText("SBjXXAgTVrQUEK6qt3EQ");
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

        // 添加Key和IV输入框
        Label keyLabel = new Label("加密 Key:");
        keyTextField = new TextField();
        HBox keyBox = new HBox(10, keyLabel, keyTextField);
        keyBox.setPadding(new Insets(2));
        keyBox.setStyle("-fx-alignment: center;");

        Label ivLabel = new Label("IV (16位):");
        ivTextField = new TextField();
        HBox ivBox = new HBox(10, ivLabel, ivTextField);
        ivBox.setPadding(new Insets(2));
        ivBox.setStyle("-fx-alignment: center;");

        Label secretLabel = new Label("SecretKey:");
        TextField secretTextField = new TextField();
        HBox secretBox = new HBox(10, secretLabel, secretTextField);
        secretBox.setPadding(new Insets(2));
        secretBox.setStyle("-fx-alignment: center;");

        // 调整布局
        HBox topLayout = new HBox(10,  secretLabel, textField);
        topLayout.setPadding(new Insets(2));

        HBox button = new HBox(10, generateKeyButton, toggleButton);
        button.setPadding(new Insets(2));
        button.setStyle("-fx-alignment: center;");

        VBox rootLayout = new VBox(20, keyBox, ivBox, topLayout, button, qrCodeImageView);
        rootLayout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #f0f0f0;");
        topLayout.setStyle("-fx-alignment: center;");
        textField.setStyle("-fx-font-size: 10pt");
        generateKeyButton.setStyle("-fx-font-size: 10pt");
        toggleButton.setStyle("-fx-font-size: 10pt");

        qrCodeImageView.setFitWidth(300);
        qrCodeImageView.setFitHeight(300);

        Scene scene = new Scene(rootLayout, 380, 520);
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
            if (client == null){
                log.info("链接地址：{}", ws);
                client = new WebSocketClient(new URI(ws));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopWebSocket() {
        if (client != null){
            client.close();
            client = null; // 将对象引用设为null
            System.gc(); // 建议垃圾收集器运行
        }
    }

    private void startAction(String secretKey){
//        String iv = "1234567890123456";
        String iv = ivTextField.getText();
        String password = keyTextField.getText();
        IvParameterSpec ivParameterSpec = AES.generateIV(iv);
        SecretKey key = AES.getSecretKey(AESMode.AES_128, password);
        Encryption aes128Encryption = new AESEncryption();
        aes128Encryption.init(key, ivParameterSpec, new OFBMode());
        ivTextField.setDisable(true);
        keyTextField.setDisable(true);
        //生产随机设备id
        String deviceId = UUID.randomUUID().toString().replaceAll("-","");

        //创建webSocket链接
        connectWebSocket(secretKey, deviceId);
        //创建剪切板监听事件
        ClipboardPreHandlerListener clipboardPreHandlerListener = new ClipboardPreHandlerListener(aes128Encryption);
        ClipboardListener clipboardListener = new ClipboardListener(client, clipboardPreHandlerListener, new Datas(deviceId, secretKey, DeviceType.WINDOWS));
        Clipboard.getInstance(clipboardListener, clipboardPreHandlerListener);
        //收到消息-设置剪切板中
        client.addMessageHandler(clipboardListener::handleMessage);

    }

    private void stopAction(){
        ivTextField.setDisable(false);
        keyTextField.setDisable(false);
        stopWebSocket();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        // 在应用程序退出时执行
        log.info("Application is stopping");
        // 例如保存数据或释放资源
        stopWebSocket();
        Clipboard.destroy();
    }
}