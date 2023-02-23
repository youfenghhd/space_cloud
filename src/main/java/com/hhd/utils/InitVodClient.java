package com.hhd.utils;

import com.aliyun.oss.ClientException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;

/**
 * @author -无心
 * @date 2023/2/23 16:55:06
 */
public class InitVodClient {
    public static DefaultAcsClient initVodClient() throws ClientException{
        DefaultProfile profile = DefaultProfile.getProfile(InitOssClient.REGION_ID, InitOssClient.ACCESS_KEY_ID, InitOssClient.ACCESS_KEY_SECRET);
        return new DefaultAcsClient(profile);
    }
}
