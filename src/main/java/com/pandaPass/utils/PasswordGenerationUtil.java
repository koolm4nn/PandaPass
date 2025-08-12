package com.pandaPass.utils;

import java.security.SecureRandom;

public class PasswordGenerationUtil {
    private static final int MIN_PASSWORD_LENGTH = 32;
    private static final int MAX_PASSWORD_LENGTH = 9999;

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~";
    private static final String charSet = UPPERCASE + LOWERCASE + DIGITS + SYMBOLS;

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword(int length) throws IllegalArgumentException{
        if(length < MIN_PASSWORD_LENGTH){
            throw new IllegalArgumentException("Password-Length must be at least " + MIN_PASSWORD_LENGTH + ".");
        }

        if(length > MAX_PASSWORD_LENGTH){
            throw new IllegalArgumentException("Password-Length must not be greater than " + MAX_PASSWORD_LENGTH + ".");
        }
        StringBuilder sb = new StringBuilder(length);

        int randomIndex;
        for(int idx = 0; idx < length; idx++){
            randomIndex = random.nextInt(charSet.length()); // int between 0 and charset length
            sb.append(charSet.charAt(randomIndex));
        }

        return sb.toString();
    }
}
