package com.yujigu.echolink.aes.impl;

import com.yujigu.echolink.aes.AESMode;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
@Slf4j
public class ECBMode implements AESMode {
    @Override
    public Cipher cipher(SecretKey secretKey, int mode, IvParameterSpec iv) {
        Cipher cipher;
        try {
            log.info("模式：ECB");
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(mode, secretKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return cipher;
    }
}
