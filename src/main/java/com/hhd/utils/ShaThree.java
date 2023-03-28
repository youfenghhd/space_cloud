package com.hhd.utils;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author -无心
 * @date 2023/2/17 16:22:36
 */
public class ShaThree {
    public static String encrypt(String strSrc) {
        SHAKEDigest digest = new SHAKEDigest(256);
        byte[] inputBytes = strSrc.getBytes();
        digest.update(inputBytes, 0, inputBytes.length);
        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result, 0);
        return Hex.toHexString(result);
    }
}
