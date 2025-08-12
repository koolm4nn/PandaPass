package com.pandaPass.services;

import com.pandaPass.models.Vault;
import com.pandaPass.repositories.UserRepository;
import com.pandaPass.utils.EncodingUtil;
import com.pandaPass.utils.EncryptionUtil;
import com.pandaPass.utils.HashUtil;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Provides business logic for user-realated operations such as registration, authentication, vault encryption
 * and session management.
 */
public class UserService {
    private final UserRepository userRepository;
    //private final VaultSessionService vaultSessionService;

    public UserService(UserRepository userRepository){
         this.userRepository = userRepository;
         //this.vaultSessionService = vaultSessionService;
    }

    /**
     * Retrieves salt from database for user.
     * @param mail User's mail
     * @return Salt of the user or random salt if user not found.
     */
    public byte[] getSaltForUser(String mail) {
        String salt = userRepository.findSaltByUserMail(mail);

        if(salt == null){
            return HashUtil.generateSalt();
        } else {
            return EncodingUtil.decodeBase64ToByteArray(salt);
        }
    }

    /**
     * Retrieves totp secret from database for user.
     * @param mail User's mail
     * @return Encrypted totp secret of user or null if user not found.
     */
    public String getTotpSecretForUser(String mail) {
        String totpSecret = userRepository.findTotpSecretByUserMail(mail);

        if(totpSecret == null){
            // TODO: add error
            return null;
        } else {
            return totpSecret;
        }
    }

    /**
     * Saves the current state of the vault to the database.
     * @return True if saving was successful, otherwise false
     */
    public boolean saveVaultForUser(){
        try{
            VaultSession vaultSession = SessionManager.getVaultSession();
            String mail = vaultSession.getVault().getMail();
            String vaultAsJson = vaultSession.getVault().toJson();
            byte[] key = vaultSession.getKey();
            String encrypted = EncryptionUtil.encrypt(key, vaultAsJson);
            boolean saveWasSuccessful = userRepository.saveVaultForUser(mail, encrypted);
            SessionManager.getVaultSession().clear();
            //ServiceLocator.getVaultSessionService().lockVault();
            if(!saveWasSuccessful){
                throw new RuntimeException("Vault could not be saved.");
            }
            return saveWasSuccessful;
        } catch (Exception e) {
            System.err.println("Error while saving vault: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetch the vault for user from database, decrypt it and initialise user session.
     * @return True if session is set with decrypted vault, otherwise false
     */
    public boolean unlockVaultForUser() {
        try{
            // Derive key from password and user's salt using PBKDF2
            //      -> key := PBKDF2(mail|password)
            LoginSession loginSession = SessionManager.getLoginSession();
            String mail = loginSession.getMail();
            byte[] key = Arrays.copyOf(loginSession.getDerivedKey(), loginSession.getDerivedKey().length);
            byte[] salt = Arrays.copyOf(loginSession.getSalt(), loginSession.getSalt().length);
            SessionManager.clearLoginSession();

            String encryptedVault = userRepository.findVaultByUserMail(mail);
            String decryptedVault = EncryptionUtil.decrypt(key, encryptedVault);
            Vault vault = Vault.fromJson(decryptedVault);
            SessionManager.setVaultSession(new VaultSession(key, vault, salt));
            return true;
        } catch (Exception e) {
            System.err.println("Error while unlocking vault: " + e.getMessage());
            return false;
        }
    }

    /**
     * Creates a new database entry for user. Creates user-unique salt, calculates the authentication hash with the
     * key derived from the user credentials.
     * @param mail User's mail
     * @param password User's plaintext password
     * @return True if new user was created, otherwise false
     */
    public boolean signUpUser(String mail, String password, String totpSecret){
        // Check for username collision
        boolean mailAlreadyExists = userRepository.findUserByMail(mail);
        if(mailAlreadyExists){
            System.err.println("User with the mail \"" + mail + "\" already exists.");
            return false;
        }

        byte[] salt = HashUtil.generateSalt();

        boolean insertingWasSuccessful;
        try {
            String keyInput = mail + password;
            byte[] key = HashUtil.deriveKey(keyInput, salt);
            String authHash = HashUtil.hashHMAC(key, password);
            String saltAsBase64 = EncodingUtil.encodeByteArrayToBase64(salt);

            String encryptedTotpSecret = EncryptionUtil.encrypt(key, totpSecret);
            insertingWasSuccessful = ServiceLocator.getUserRepository().insertUser(mail, saltAsBase64, authHash, key, encryptedTotpSecret);
        } catch (Exception e) {
            System.err.println("Error occurred while signing up new user: \"" + e.getMessage());
            return false;
        }
        return insertingWasSuccessful;
    }

    /**
     * Authenticates the user by comparing the hash from login credentials with the stored hash for the mail.
     * @param mail User's mail
     * @param password User's plaintext password
     * @return True if hash matches, otherwise false
     */
    public boolean authenticateAndCreateSession(String mail, String password){
        try{
            // Derive key from password and user's salt using PBKDF2
            //      -> key := PBKDF2(mail|password)
            String keyInput = mail + password;
            byte[] salt = getSaltForUser(mail);
            byte[] key = HashUtil.deriveKey(keyInput, salt);

            // Compute authentication hash:
            //      -> hash(derivedKey|password)
            String hash = HashUtil.hashHMAC(key, password);
            String storedAuthHash = userRepository.findAuthHashByUserMail(mail);

            // Compare authentication hash with stored in database:
            //      - if it matches: proceed
            //      - else: generic error message
            if(hash.equals(storedAuthHash)){
                String encryptedTotpSecret = ServiceLocator.getUserService().getTotpSecretForUser(mail);
                byte[] totpSecret = EncodingUtil.decodeBase64ToByteArray(EncryptionUtil.decrypt(key, encryptedTotpSecret));
                LoginSession ls = new LoginSession(mail, salt, key, totpSecret);
                SessionManager.setLoginSession(ls);

                return true;
            } else {
                return false;
            }
        } catch(Exception e){
            System.err.println("Authentication failed: " + e.getMessage());
            return false;
        }
    }
}