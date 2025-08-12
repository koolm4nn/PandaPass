package com.pandaPass.services;

import com.pandaPass.models.Vault;

import java.util.Arrays;

/**
 * Manages the state of a user's vault session.
 * Holds the decrypted vault and the derived key for encryption/decryption during an active session.
 */
public class VaultSessionService {
    private byte[] key;
    private Vault vault;
    private byte[] salt;

    public VaultSessionService(){}

    /**
     * Unlocks a vault by setting the key, vault and salt of the user.
     * @param key The derived encryption/decryption key
     * @param vault The decrypted vault object
     * @param salt The user-specific salt
     */
    public void unlockVault(byte[] key, Vault vault, byte[] salt){
        this.key = key;
        this.vault = vault;
        this.salt = salt;
    }

    /**
     * Locks the vault session by clearing the key, vault and salt.
     */
    public void lockVault(){
        Arrays.fill(key, (byte) 0);
        Arrays.fill(salt, (byte) 0);
        this.vault = null;
    }

    /**
     * Retrieves the current vault.
     * @return Unlocked vault instance.
     * @throws IllegalStateException if no session is active.
     */
    public Vault getVault() throws IllegalStateException {
        if(vault == null){
            throw new IllegalStateException("Vault session is not initialized.");
        }
        return vault;
    }

    /**
     * Retrieves the encryption key for current session.
     * @return The encryption key
     * @throws IllegalStateException if no session is active
     */
    public byte[] getKey() throws IllegalStateException{
        if(key == null){
            throw new IllegalStateException("Vault session is not initialized.");
        }
        return key;
    }

    /**
     * Retrieves the user-specific salt for the current session.
     * @return The salt
     * @throws IllegalStateException if no session is active
     */
    public byte[] getSalt() throws IllegalStateException {
        if(salt == null){
            throw new IllegalStateException("Vault session is not initialized.");
        }
        return salt;
    }
}
