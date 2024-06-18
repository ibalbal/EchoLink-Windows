package com.yujigu.echolink.aes.impl;

import com.yujigu.echolink.aes.AESMode;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

public class ECBMode implements AESMode {
    @Override
    public Cipher cipher() {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        return cipher;
    }
}
