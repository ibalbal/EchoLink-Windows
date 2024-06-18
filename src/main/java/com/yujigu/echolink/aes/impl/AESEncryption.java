package com.yujigu.echolink.aes.impl;

import com.yujigu.echolink.aes.AES;
import com.yujigu.echolink.aes.AESMode;
import com.yujigu.echolink.aes.Encryption;
import javax.crypto.SecretKey;

public class AESEncryption implements Encryption {

    @Override
    public String encrypt(AESMode aesMode, String iv, String content, SecretKey secretKey){
        return AES.encrypt(secretKey, aesMode, iv, content);
    }

    @Override
    public String decrypt(AESMode aesMode, String iv, String content, SecretKey secretKey){
        return AES.decrypt(secretKey, aesMode, iv, content);
    }
}
