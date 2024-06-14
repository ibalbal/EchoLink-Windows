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
        datas.setContent(clipboardText);
        // 发送消息到WebSocket服务器
        client.sendMessage(JSONObject.toJSONString(datas));
    }

}