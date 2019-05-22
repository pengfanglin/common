package com.fanglin.common.utils;

import com.fanglin.common.core.others.ValidateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密
 * @author 彭方林
 * @date 2019/4/2 17:54
 * @version 1.0
 **/
@Slf4j
public class EncodeUtils {
    /**
     * aes对称加密
     */
    public static String aesEncode(String content) {
        return aesEncode(content, null);
    }

    /**
     * AES加密
     */
    public static String aesEncode(String content, String key) {
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(md5Encode(key == null ? "" : key).toLowerCase().getBytes(), "AES");
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return new String(Base64.encodeBase64(cipher.doFinal(content.getBytes())));
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new ValidateException("加密出错");
        }
    }

    /**
     * AES解密
     */
    public static String aesDecode(String content) {
        return aesDecode(content, null);
    }

    /**
     * AES解密
     */
    public static String aesDecode(String content, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(md5Encode(key == null ? "" : key).toLowerCase().getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.decodeBase64(content.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new ValidateException("解密出错");
        }
    }

    /**
     * md5加密
     */
    public static String md5Encode(String content, String key) {
        //定义一个字节数组
        byte[] secretBytes;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update((key == null ? content : content + key).getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
            throw new ValidateException("没有md5这个算法！");
        }
        //将加密后的数据转换为16进制数字
        StringBuilder md5code = new StringBuilder(new BigInteger(1, secretBytes).toString(16));
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code.insert(0, "0");
        }
        return md5code.toString();
    }

    /**
     * md5加密
     */
    public static String md5Encode(String content) {
        return md5Encode(content, null);
    }

    /**
     * base64加密
     */
    public static String base64Encode(String content) {
        return new String(Base64.encodeBase64(content.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * base64解码
     */
    public static String base64Decode(String content) {
        return new String(Base64.decodeBase64(content.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * sha1(哈希值校验)
     */
    public static String sha1(String data) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
            throw new ValidateException("哈希值校验失败");
        }
        md.update(data.getBytes());
        StringBuilder buf = new StringBuilder();
        byte[] bits = md.digest();
        for (byte bit : bits) {
            int a = bit;
            if (a < 0) {
                a += 256;
            }
            if (a < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }
}
