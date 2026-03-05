package com.chartmania.util;

import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Component;

@Component
public class GenericUtil {
    public static String generateRandomString(int length) {
        StringKeyGenerator generator = new Base64StringKeyGenerator(length);
        return generator.generateKey();
    }
}
