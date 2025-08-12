package com.pandaPass.services;

import com.pandaPass.models.Vault;

import java.util.Arrays;

public class VaultSession {
    private final byte[] key;
    private Vault vault;
    private final byte[] salt;

    public VaultSession(byte[] key, Vault vault, byte[] salt){
        this.key = key;
        this.vault = vault;
        this.salt = salt;
    }

    public byte[] getKey(){
        return key;
    }

    public Vault getVault(){
        return vault;
    }

    public void clear(){
        Arrays.fill(key, (byte) 0);
        Arrays.fill(salt, (byte) 0);
        vault = null;
    }
}
