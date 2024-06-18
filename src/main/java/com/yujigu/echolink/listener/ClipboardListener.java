package com.yujigu.echolink.listener;
import com.alibaba.fastjson2.JSONObject;
import com.symxns.sym.jni.windows.Clipboard.WindowsClipboard;
import com.yujigu.echolink.model.Datas;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClipboardListener implements WindowsClipboard.ClipboardListener{
    private final Datas datas;
    private final WebSocketClient client;

    public ClipboardListener(WebSocketClient client, Datas datas) {
        this.datas = datas;
        this.client = client;
    }

    @Override
    public void onClipboardChange(String clipboardText) {
        log.info("剪贴板内容发生变化：" + clipboardText);
        datas.setContent(clipboardText);
        // 发送消息到WebSocket服务器
        client.sendMessage(JSONObject.toJSONString(datas));
    }

}