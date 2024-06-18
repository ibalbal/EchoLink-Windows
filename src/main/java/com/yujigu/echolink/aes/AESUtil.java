package com.yujigu.echolink.aes;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    /**
     * AES加密字符串
     *
     * @param content  需要被加密的字符串
     * @param password 加密需要的密码
     * @return 密文
     */
    public static byte[] encrypt(String content, String password) {
        try {
            int mode = Cipher.ENCRYPT_MODE;
            byte[] byteContent = content.getBytes("utf-8");
            return operator(password, mode, byteContent);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] operator(String password, int mode, byte[] byteContent) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = getSecretKey(password);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
        try {
            cipher.init(mode, secretKey, AES.generateIV("1234567890123456"));// 初始化为加密模式的密码器
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        byte[] result = cipher.doFinal(byteContent);// 加密
        return result;
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

    /**
     * 解密AES加密过的字符串
     *
     * @param content  AES加密过过的内容
     * @param password 加密时的密码
     * @return 明文
     */
    public static byte[] decrypt(byte[] content, String password) {
        try {
            int mode = Cipher.DECRYPT_MODE;
            return operator(password, mode, content);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String content = "1993";
        String password = "加密密码";
        System.out.println("需要加密的内容：" + content);
        byte[] encrypt = encrypt(content, password);
        String base64Encode = Base64.getEncoder().encodeToString(encrypt);
        System.out.println("base64Encode：" + base64Encode);
        byte[] byte2 = Base64.getDecoder().decode(base64Encode);
        byte[] decrypt = decrypt(byte2, password);
        System.out.println("解密后的内容：" + new String(decrypt, "utf-8"));
    }


    public static void printSecretKey(SecretKey secretKey) {
        if (secretKey instanceof SecretKeySpec) {
            SecretKeySpec spec = (SecretKeySpec) secretKey;
            System.out.println("Algorithm: " + spec.getAlgorithm());
            System.out.println("key[]: " + Arrays.toString(spec.getEncoded()));
        }
    }
}
