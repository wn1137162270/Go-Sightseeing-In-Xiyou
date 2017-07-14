package myapp.lenovo.httpclient;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by wn on 2017/7/9.
 */

public class MyBase64 {
    static String decode(String base64String){
        return new String(Base64.decodeBase64(base64String));
    }

    static String encode(byte[] binaryData){
        return Base64.encodeBase64String(binaryData);
    }
}
