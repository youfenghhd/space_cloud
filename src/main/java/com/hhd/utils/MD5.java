package com.hhd.utils;

import io.netty.util.internal.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author -无心
 * @date 2023/2/16 0:58:16
 */
public class MD5 {

    public static String encrypt(String strSrc) {
        try {
            char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            byte[] bytes = strSrc.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            bytes = md.digest();
            int j = bytes.length;
            char[] chars = new char[j * 2];
            int k = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                chars[k++] = hexChars[b >>> 4 & 0xf];
                chars[k++] = hexChars[b & 0xf];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("MD5加密出错！！+" + e);
        }
    }

    public static String getFileMd5String(File file){
        String md5Result = StringUtil.EMPTY_STRING;
        try {
            InputStream fis = Files.newInputStream(file.toPath());
            byte[] buffer = new byte[fis.available()];
            while (fis.read(buffer) > 0) {
                md5Result = DigestUtils.md5Hex(buffer);
            }
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("计算文件MD5值错误！！+" + e);
        }
        return md5Result;
    }

    public static void main(String[] args) {
        System.out.println(MD5.encrypt("123456"));
    }

}
