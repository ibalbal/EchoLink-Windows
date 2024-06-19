package com.yujigu.echolink.aes;

import com.yujigu.echolink.aes.impl.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;


public abstract class Encryption {

    public SecretKey secretKey;
    public IvParameterSpec iv;
    public AESMode aesMode;
    public static void printSecretKey(SecretKey secretKey) {
        if (secretKey instanceof SecretKeySpec) {
            SecretKeySpec spec = (SecretKeySpec) secretKey;
            System.out.println("Algorithm: " + spec.getAlgorithm());
            System.out.println("key[]: " + Arrays.toString(spec.getEncoded()));
        }
    }

    protected abstract void init(SecretKey secretKey, IvParameterSpec iv, AESMode aesMode);

    /**
     * 加密
     * @param content
     * @return
     * @throws Exception
     */
    protected abstract String encrypt(String content) throws Exception;

    /**
     * 解密
     * @param content
     * @return
     * @throws Exception
     */
    protected abstract String decrypt(String content) throws Exception;

    public static void main(String[] args) throws Exception {
        String iv = "1234567890123456";

        IvParameterSpec ivParameterSpec = AES.generateIV(iv);
        SecretKey secretKey = AES.getSecretKey(AESMode.AES_128,"加密密码");

        Encryption aes128Encryption = new AESEncryption();
        aes128Encryption.init(secretKey, ivParameterSpec, new OFBMode());

        String encrypt = aes128Encryption.encrypt("1993");
        String decrypt = aes128Encryption.decrypt(encrypt);
        System.out.println("AES-128 Encrypted: " + encrypt);
        System.out.println("AES-128 Decrypted: " + decrypt);
    }
}
