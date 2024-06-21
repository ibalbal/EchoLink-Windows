package com.yujigu.echolink.listener;

import com.symxns.sym.jni.windows.Clipboard.HandlerListener;
import com.symxns.sym.core.aes.Encryption;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClipboardPreHandlerListener implements HandlerListener {
    private final Encryption aes128Encryption;

    public ClipboardPreHandlerListener(Encryption aes128Encryption) {
        this.aes128Encryption = aes128Encryption;
    }

    @Override
    public String beforeClipboardSet(String value) {
        String decrypt = aes128Encryption.decrypt(value);
        log.info("decrypt: {}", decrypt);
        return decrypt;
    }

    @Override
    public String beforeClipboardGet(String value) {
        String encrypt = aes128Encryption.encrypt(value);
        log.info("encrypt: {}", encrypt);
        return encrypt;
    }
}
