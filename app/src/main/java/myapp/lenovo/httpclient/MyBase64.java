package myapp.lenovo.httpclient;

import android.util.Log;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;

import Decoder.BASE64Decoder;

/**
 * Created by wn on 2017/7/9.
 */

public class MyBase64 {
    public static byte[] decode(String base64String) throws IOException {
        byte[] bytes=new BASE64Decoder().decodeBuffer(base64String);
        return bytes;
    }

    public static String encode(byte[] binaryData){
        return Base64.encodeBase64String(binaryData);
    }
}
