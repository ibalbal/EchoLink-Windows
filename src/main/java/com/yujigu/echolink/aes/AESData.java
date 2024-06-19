package com.yujigu.echolink.aes;

import com.yujigu.echolink.aes.impl.CBCMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AESData {
    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;
    private CBCMode cbcMode;
}
