//package com.niuyin.service.video.util;
//
//import org.apache.commons.codec.binary.Base64;
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
//
//import java.io.IOException;
//
//public class Base64Tool {
//
//    /**
//     * 字节数组转Base64编码
//     */
//    public static String byteToBase64(byte[] bytes) {
//        BASE64Encoder encoder = new BASE64Encoder();
////        return encoder.encode(bytes);
//        return new String(Base64.encodeBase64(bytes));
//    }
//
//    /**
//     * Base64编码转字节数组
//     */
//    public static byte[] base64ToByte(String base64Key) throws IOException {
//        BASE64Decoder base64Decoder = new BASE64Decoder();
////        return base64Decoder.decodeBuffer(base64Key);
//        return Base64.decodeBase64(base64Key);
//    }
//
//
//}
