package com.yujigu.echolink.model;

import com.yujigu.echolink.model.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Datas implements Serializable {
    private String uid; //设备id
    private String secret; //链接密钥
    private String content; //文本类数据
    private String deviceType;

    public Datas(String deviceId, String secretKey, DeviceType deviceType) {
        this.uid = deviceId;
        this.secret = secretKey;
        this.deviceType = deviceType.name();
    }
}
