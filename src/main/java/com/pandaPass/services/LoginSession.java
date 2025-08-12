package com.pandaPass.services;

import java.util.Arrays;

public class LoginSession {
    private String mail;
    private final byte[] salt;
    private final byte[] derivedKey;
    private final byte[] totpSecret;

    public LoginSession(String mail, byte[] salt, byte[] derivedKey, byte[] totpSecret){
        this.mail = mail;
        this.salt = salt;
        this.derivedKey = derivedKey;
        this.totpSecret = totpSecret;
    }

    public String getMail(){
        return mail;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getDerivedKey(){
        return derivedKey;
    }

    public byte[] getTotpSecret(){
        return totpSecret;
    }

    public void clear(){
        mail = "\0";
        Arrays.fill(derivedKey, (byte) 0);
        Arrays.fill(totpSecret, (byte) 0);
        Arrays.fill(salt, (byte) 0);
    }
}
