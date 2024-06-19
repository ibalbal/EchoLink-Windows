package com.yujigu.echolink.aes.impl;

import com.yujigu.echolink.aes.AESMode;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
@Slf4j
public class GCMMode implements AESMode {
    @Override
    public Cipher cipher(SecretKey secretKey, int mode, IvParameterSpec iv) {
        Cipher cipher = null;
        try {
            log.info("模式：GCM");
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] keyBytes = secretKey.getEncoded();
            int keyLength = keyBytes.length * 8; // 字节数 * 8 = 位数
            GCMParameterSpec gcmSpec = new GCMParameterSpec(keyLength, iv.getIV());
            cipher.init(mode, secretKey, gcmSpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return cipher;
    }
}
