package com.yujigu.echolink.aes;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public interface AESMode {

    Cipher cipher(SecretKey secretKey, int mode, IvParameterSpec iv);

    int AES_128 = 128;

    int AES_192 = 192;

    int AES_256 = 256;
}
