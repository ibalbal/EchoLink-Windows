package com.yujigu.echolink.aes;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public abstract class AES {


    // Generate a random IV
    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static IvParameterSpec generateIV(String iv) {
        return new IvParameterSpec(iv.getBytes());
    }

    /**
     * AES加密字符串
     *
     * @param content  需要被加密的字符串
     * @param secretKey 加密时的密码
     * @param aesMode 加密模式
     * @param iv    偏移量
     * @return 密文
     */
    public static String encrypt(String content, SecretKey secretKey, IvParameterSpec iv, AESMode aesMode) {
        try {
            int mode = Cipher.ENCRYPT_MODE;
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedData = operator(secretKey, mode, aesMode, byteContent, iv);
            byte[] ivAndEncryptedData = new byte[iv.getIV().length + encryptedData.length];
            System.arraycopy(iv.getIV(), 0, ivAndEncryptedData, 0, iv.getIV().length);
            System.arraycopy(encryptedData, 0, ivAndEncryptedData, iv.getIV().length, encryptedData.length);
            return Base64.getEncoder().encodeToString(ivAndEncryptedData);
        } catch (Exception e) {
            log.error("加密失败：{}", e.getMessage());
        }
        return "加密失败";
    }

    /**
     * 解密AES加密过的字符串
     *
     * @param content  AES加密过过的内容
     * @param secretKey 加密时的密码
     * @param aesMode 加密模式
     * @param iv    偏移量
     * @return 明文
     */
    public static String decrypt(String content, SecretKey secretKey, IvParameterSpec iv, AESMode aesMode) {
        try {
            int mode = Cipher.DECRYPT_MODE;
            byte[] ivAndEncryptedData = Base64.getDecoder().decode(content);
            byte[] encryptedData = Arrays.copyOfRange(ivAndEncryptedData, 16, ivAndEncryptedData.length);
            byte[] decryptedData = operator(secretKey, mode, aesMode, encryptedData, iv);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("解密失败：{}", e.getMessage());
        }
        return "解密失败";

    }

    private static byte[] operator(SecretKey secretKey, int mode, AESMode aesMode, byte[] byteContent, IvParameterSpec iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = aesMode.cipher(secretKey, mode, iv);
        return cipher.doFinal(byteContent);
    }

    /**
     * 获取密钥
     * @return
     * @throws Exception
     */
    public static SecretKey getSecretKey(int type, String key){
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            //kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes());
            kgen.init(type, secureRandom);

            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            printSecretKey(secretKey);
            return secretKey;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printSecretKey(SecretKey secretKey) {
        if (secretKey instanceof SecretKeySpec) {
            SecretKeySpec spec = (SecretKeySpec) secretKey;
            System.out.println("Algorithm: " + spec.getAlgorithm());
            System.out.println("key[]: " + Arrays.toString(spec.getEncoded()));
        }
    }
}
