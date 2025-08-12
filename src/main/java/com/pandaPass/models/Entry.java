package com.pandaPass.models;

import com.pandaPass.services.SessionManager;
import com.pandaPass.utils.EncodingUtil;
import com.pandaPass.utils.EncryptionUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.UUID;

/**
 * Represents a single credential entry, e.g. for website or service.
 * Contains a descriptive service name, username encrypted password, and an encrypted random key used
 * specifically to encrypt/decrypt this password.
 */
public class Entry {
    private String service;
    private String username;
    private String encryptedPassword; // Encrypted password as Base64
    private final String encryptedKey;
    private int categoryId;

    private transient String runtimeId;
    private transient BooleanProperty isCompromisedProperty;

    public Entry(String service, String username, Category category, String encryptedPassword, String encryptedKey){
        this.service = service;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.encryptedKey = encryptedKey;
        categoryId = category.getId();
    }

    // Getters, Setters
    public String getService(){
        return service;
    }
    public void setService(String service){
        this.service = service;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public int getCategoryId(){return categoryId;}
    public void setCategoryId(int categoryId){
        this.categoryId = categoryId;
    }

    public boolean isCompromised(){return isCompromisedProperty.get();}
    public void setIsCompromised(boolean isCompromised){
        this.isCompromisedProperty.set(isCompromised);
    }

    public BooleanProperty getIsCompromisedProperty(){return isCompromisedProperty;}

    public String getRuntimeId(){return runtimeId;}

    public void initializeRuntimeFields(){
        this.runtimeId = String.valueOf(UUID.randomUUID());
        this.isCompromisedProperty = new SimpleBooleanProperty(false);
    }

    public boolean setPassword(String plaintextPassword){
        try{
            String decryptedKey = EncryptionUtil.decrypt(SessionManager.getVaultSession().getKey(), encryptedKey);
            byte[] keyBytes = EncodingUtil.decodeBase64ToByteArray(decryptedKey);
            encryptedPassword = EncryptionUtil.encrypt(keyBytes, plaintextPassword);
            return true;
        } catch (Exception e){
            System.out.println("Error while updating password: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String toString(){
        return "Service: \"" + service + "\", Username: \"" + username + "\", Password: " + encryptedPassword + ", UUID: " + runtimeId;
    }

    /**
     * Decrypts the password-specific key using the vault-key and decrypts the key as well as the password.
     * @return The plaintext password for this entry as String.
     */
    public String toDecryptedString() {
        String decryptedPassword = getDecryptedPassword();
        return "Service: \"" + service + "\", Username: \"" + username + "\", Password: " + decryptedPassword + " (" + getDecryptedPassword() + ").";
    }

    public String getDecryptedPassword(){
        String decryptedPassword;
        try{
            byte[] key = SessionManager.getVaultSession().getKey();
            byte[] passwordKey = EncodingUtil.decodeBase64ToByteArray(EncryptionUtil.decrypt(key, encryptedKey));
            decryptedPassword = EncryptionUtil.decrypt(passwordKey, encryptedPassword);
        } catch (Exception e){
            System.err.println("Error decrypting entry: " + e.getMessage());
            decryptedPassword = "Error";
        }
        return decryptedPassword;
    }
}
