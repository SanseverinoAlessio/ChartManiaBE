package com.chartmania.util;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class imageUtil {
    public static byte[] convertBase64ImageInBytes(String base64Image) {
        int comma = base64Image.indexOf(",");
        String resultBase64 = base64Image.substring(comma + 1);
        byte[] imageByte = Base64.getDecoder().decode(resultBase64);
        return imageByte;
    }
}
