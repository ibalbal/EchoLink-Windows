package com.yujigu.echolink.aes;

import com.yujigu.echolink.aes.impl.AESEncryption;
import com.yujigu.echolink.aes.impl.CBCMode;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;


public interface Encryption {

    /**
     * 获取密钥
     * @return
     * @throws Exception
     */
    default SecretKey getSecretKey(String key) throws Exception {
//        KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
//        kgen.init(128, new SecureRandom(key.getBytes()));// 利用用户密码作为随机数初始化出
//        //加密没关系，SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以解密只要有password就行
//        SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
//        printSecretKey(secretKey);
//        return secretKey;
        KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
        //kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key.getBytes());
        kgen.init(128, secureRandom);

        SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
        printSecretKey(secretKey);
        return secretKey;
    }
    public static void printSecretKey(SecretKey secretKey) {
        if (secretKey instanceof SecretKeySpec) {
            SecretKeySpec spec = (SecretKeySpec) secretKey;
            System.out.println("Algorithm: " + spec.getAlgorithm());
            System.out.println("key[]: " + Arrays.toString(spec.getEncoded()));
        }
    }

    /**
     * 加密
     * @param content
     * @param secretKey
     * @return
     * @throws Exception
     */
    String encrypt(AESMode aesMode, String iv, String content, SecretKey secretKey) throws Exception;

    /**
     * 解密
     * @param content
     * @param secretKey
     * @return
     * @throws Exception
     */
    String decrypt(AESMode aesMode, String iv, String content, SecretKey secretKey) throws Exception;

    public static void main(String[] args) throws Exception {
        Encryption aes128Encryption = new AESEncryption();
        SecretKey secretKey = aes128Encryption.getSecretKey("1234567890123456");
        String encrypt = aes128Encryption.encrypt(new CBCMode(), "1234567890123456", "1993", secretKey );
        String decrypt = aes128Encryption.decrypt(new CBCMode(), "1234567890123456", encrypt, secretKey);
        System.out.println("AES-128 Encrypted: " + encrypt);
        System.out.println("AES-128 Decrypted: " + decrypt);

//        SecretKey secret128Key = aes128Encryption.getSecretKey();
//        String encryptedTextAES128  = aes128Encryption.AESKeyEncrypt("12", secret128Key);
//        String decryptedTextAES128  = aes128Encryption.AESKeyDecrypt(encryptedTextAES128 , secret128Key);
//        System.out.println("AES-128 Encrypted: " + encryptedTextAES128);
//        System.out.println("AES-128 Decrypted: " + decryptedTextAES128);
//
//        Encryption aes192Encryption = new Aes192Encryption();
//        SecretKey secret192Key = aes192Encryption.getSecretKey();
//        String encryptedTextAES192 = aes192Encryption.AESKeyEncrypt("12", secret192Key);
//        String decryptedTextAES192 = aes192Encryption.AESKeyDecrypt(encryptedTextAES192, secret192Key);
//        System.out.println("AES-192 Encrypted: " + encryptedTextAES192);
//        System.out.println("AES-192 Decrypted: " + decryptedTextAES192);
//
//        Encryption aes256Encryption = new Aes256Encryption();
//        SecretKey secret256Key = aes256Encryption.getSecretKey();
//        String encryptedTextAES256 = aes256Encryption.AESKeyEncrypt("12", secret256Key);
//        String decryptedTextAES256 = aes256Encryption.AESKeyDecrypt(encryptedTextAES256, secret256Key);
//        System.out.println("AES-256 Encrypted: " + encryptedTextAES256);
//        System.out.println("AES-256 Decrypted: " + decryptedTextAES256);
    }
}
