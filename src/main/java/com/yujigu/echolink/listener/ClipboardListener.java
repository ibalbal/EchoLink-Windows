package com.yujigu.echolink.listener;
import com.alibaba.fastjson2.JSONObject;
import com.symxns.sym.jni.windows.Clipboard.WindowsClipboard;
import com.yujigu.echolink.Datas;
import com.yujigu.echolink.WebSocketClient;
import com.yujigu.echolink.model.DeviceType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ClipboardListener implements WindowsClipboard.ClipboardListener{
    private final Datas datas;
    private final WebSocketClient client;

    public ClipboardListener(WebSocketClient client, Datas datas) {
        this.datas = datas;
        this.client = client;
    }

    @Override
    public void onClipboardChange(String clipboardText) {
        try {
            // 对剪贴板文本进行Base64解码
            String decodedText = base64Decode(clipboardText);
            // 设置Datas对象的内容
            datas.setContent(decodedText);
            // 发送消息到WebSocket服务器
            client.sendMessage(JSONObject.toJSONString(datas));
            // 输出剪贴板文本（调试用）
            System.out.println(clipboardText);
        } catch (IllegalArgumentException e) {
            // 捕获解码异常，处理异常情况
            e.printStackTrace();
        }
    }

    public static String base64Decode(String input) {
        // 对输入的Base64字符串进行解码
        byte[] decodedBytes = Base64.getDecoder().decode(input.getBytes(StandardCharsets.UTF_8));
        // 将解码后的字节数组转换为字符串返回
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}