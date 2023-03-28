package com.hhd.utils;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author -无心
 * @date 2023/2/16 0:58:16
 */
public class Md5OfFile {

//    public static String getFileMd5String(File file) {
//        String md5Result = StringUtil.EMPTY_STRING;
//        try {
//            InputStream fis = Files.newInputStream(file.toPath());
//            byte[] buffer = new byte[fis.available()];
//            while (fis.read(buffer) > 0) {
//                md5Result = DigestUtils.md5Hex(buffer);
//            }
//            fis.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("计算文件MD5值错误！！+" + e);
//        }
//        return md5Result;
//    }

    public static String getFileMd5String(MultipartFile file) {
        try {
            //获取文件的byte信息
            byte[] uploadBytes = file.getBytes();
            // 拿到一个MD5转换器
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            //转换为16进制
            return new BigInteger(1, digest).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("" + e);
        }
    }
}
