package com.yujigu.echolink.aes.impl;

import com.google.zxing.qrcode.decoder.Mode;
import com.yujigu.echolink.aes.AES;
import com.yujigu.echolink.aes.AESData;
import com.yujigu.echolink.aes.AESMode;
import com.yujigu.echolink.aes.Encryption;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESEncryption extends Encryption {

    @Override
    public void init(SecretKey secretKey, IvParameterSpec iv, AESMode aesMode) {
        this.secretKey = secretKey;
        this.aesMode = aesMode;
        this.iv = iv;
    }

    @Override
    public String encrypt(String content){
        return AES.encrypt(content, this.secretKey, this.iv, this.aesMode);
    }

    @Override
    public String decrypt(String content){
        return AES.decrypt(content, this.secretKey, this.iv, this.aesMode);
    }
}
