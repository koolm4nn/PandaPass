package com.pandaPass.utils;

import org.apache.commons.codec.binary.Base32;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodingUtil {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static Base32 base32 = new Base32();

    private EncodingUtil(){}

    public static String encodeStringToBase64(String input){
        byte[] bytes = input.getBytes(CHARSET);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String decodeBase64ToString(String base64){
        byte[] bytes = Base64.getDecoder().decode(base64);
        return new String(bytes, CHARSET);
    }

    public static String encodeByteArrayToBase64(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decodeBase64ToByteArray(String base64){
        return Base64.getDecoder().decode(base64);
    }

    public static String encodeByteArrayToBase32(byte[] bytes){
        return base32.encodeToString(bytes);
    }
}
