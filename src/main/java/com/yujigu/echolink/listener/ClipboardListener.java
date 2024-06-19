package com.yujigu.echolink.listener;
import com.alibaba.fastjson2.JSONObject;
import com.symxns.sym.jni.windows.Clipboard.Clipboard;
import com.symxns.sym.jni.windows.Clipboard.HandlerListener;
import com.symxns.sym.jni.windows.Clipboard.WindowsClipboard;
import com.yujigu.echolink.model.Datas;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class ClipboardListener implements WindowsClipboard.ClipboardListener, WebSocketClient.MessageHandler {
    private final Datas datas;
    private final WebSocketClient client;
    private final HandlerListener clipboardPreHandlerListener;

    private static String tmpClipboardText;

    public ClipboardListener(WebSocketClient client, ClipboardPreHandlerListener clipboardPreHandlerListener, Datas datas) {
        this.datas = datas;
        this.client = client;
        this.clipboardPreHandlerListener = clipboardPreHandlerListener;
    }

    @Override
    public void onClipboardChange(String clipboardText) {
        if (clipboardText == null) {
            return;
        }

        String decryptData = null;
        if (ClipboardListener.tmpClipboardText != null) {
            decryptData = clipboardPreHandlerListener.beforeClipboardSet(ClipboardListener.tmpClipboardText);
            if (clipboardText.equals(decryptData)) {
                return;
            }
        }
        String encryptData = clipboardPreHandlerListener.beforeClipboardGet(clipboardText);
        log.info("剪贴板内容发生变化：" + clipboardText);
        datas.setContent(encryptData);
        // 发送消息到WebSocket服务器
        client.sendMessage(JSONObject.toJSONString(datas));
    }

    @Override
    public void handleMessage(String message) {
        log.info("收到消息：{}" , message);
        Datas datas = JSONObject.parseObject(message, Datas.class);
        if (datas.getContent() == null || Objects.equals(datas.getContent(), "")){
            log.info("内容消息体为空不做处理");
            return;
        }
        ClipboardListener.tmpClipboardText = datas.getContent();
        //将数据设置到剪切板
        Clipboard.setClipboard(datas.getContent());
    }
}