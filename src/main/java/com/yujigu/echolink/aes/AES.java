package com.yujigu.echolink.aes;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;



public abstract class AES {
    /**
     * AES加密字符串
     *
     * @param content  需要被加密的字符串
     * @param secretKey 加密需要的密码
     * @return 密文
     */
    public static String encrypt(SecretKey secretKey, AESMode aesMode, String iv, String content) {
        try {
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            byte[] encrypt = operator(secretKey, aesMode, iv, byteContent);
            return Base64.getEncoder().encodeToString(encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解密AES加密过的字符串
     *
     * @param content  AES加密过过的内容
     * @param secretKey 加密时的密码
     * @return 明文
     */
    public static String decrypt(SecretKey secretKey, AESMode aesMode, String iv, String content) {
        try {
            byte[] base64Encode = Base64.getDecoder().decode(content);
            byte[] decrypt = operator(secretKey, aesMode, iv, base64Encode);
            return new String(decrypt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] operator(SecretKey secretKey, AESMode aesMode, String iv, byte[] byteContent) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = aesMode.cipher();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        return cipher.doFinal(byteContent);
    }

    public static SecureRandom generateIV(String number) {
        return new SecureRandom(number.getBytes());
    }

    private static SecretKey getSecretKey(String password) throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(password.getBytes());
        kgen.init(128, secureRandom);

        SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
        printSecretKey(secretKey);
        return secretKey;
    }


    public static void main(String[] args) throws Exception {
//        String content = "1993";
//        String password = "加密密码";
//        System.out.println("需要加密的内容：" + content);
//        String base64Encode = encrypt(content, password);
//
//        byte[] byte2 = Base64.getDecoder().decode(base64Encode);
//        byte[] decrypt = decrypt(byte2, password);
//        System.out.println("解密后的内容：" + new String(decrypt, "utf-8"));
    }


    public static void printSecretKey(SecretKey secretKey) {
        if (secretKey instanceof SecretKeySpec) {
            SecretKeySpec spec = (SecretKeySpec) secretKey;
            System.out.println("Algorithm: " + spec.getAlgorithm());
            System.out.println("key[]: " + Arrays.toString(spec.getEncoded()));
        }
    }
}
