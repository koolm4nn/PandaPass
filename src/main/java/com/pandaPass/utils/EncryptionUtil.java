package com.pandaPass.utils;

import com.pandaPass.configs.CryptoSettings;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class providing AES-256 encryption and decryption functionality.
 * <p>
 * Uses AES in CBC mode with PKCS5 padding, incorporating a randomly generated Initialization Vector (IV)
 * to ensure ciphertext uniqueness for identical plaintext inputs.
 *
 * <p>
 * Overview:
 * <ul>
 *     <li><b>AES:</b> Advanced Encryption Standard (symmetric encryption)</li>
 *     <li><b>CBC:</b> Cipher Block Chaining mode; XORs each plaintext block with the previous ciphertext block</li>
 *     <li><b>PKCS5Padding:</b> Ensures plaintext matches AES's block size (16 bytes)</li>
 *     <li><b>IV:</b> Random 16-byte value, stored alongside the ciphertext</li>
 * </ul>
 */
public class EncryptionUtil {

    private static final String AES_ALGORITHM = CryptoSettings.AES_CIPHER_ALGORITHM;
    private static final int IV_SIZE = CryptoSettings.AES_IV_SIZE_BYTES; // AES requires a block size of 16 bytes
    private static final String AES_KEY_ALGORITHM = CryptoSettings.AES_KEY_ALGORITHM;

    // Prevent instantiation
    private EncryptionUtil(){}

    /**
     * Encrypts the given plaintext using AES-256 in CBC mode with PKCS5 padding.
     * A randomly generated IV is prepended.
     *
     * @param key Secret key as byte array. Must match the expected AES key length (e.g. 32 bytes for AES-256).
     * @param plaintext Plaintext string to encrypt.
     * @return A Base64-encoded string containing both the iv and the encrypted plaintext.
     * @throws NoSuchAlgorithmException If the AES algorithm is unavailable.
     * @throws NoSuchPaddingException If the padding scheme is unavailable.
     * @throws InvalidKeyException If the provided key is invalid.
     * @throws InvalidAlgorithmParameterException If the IV parameter is invalid.
     * @throws IllegalBlockSizeException If the plaintext size is incorrect. (?)
     * @throws BadPaddingException If padding is improperly applied.
     */
    public static String encrypt(byte[] key, String plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // Generate random IV
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv); // Generate random initial vector
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Init cipher
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM); // Get AES Cipher
        SecretKeySpec secretKey = new SecretKeySpec(key, AES_KEY_ALGORITHM); // Provide a key for AES
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec); // Init cipher with parameter values

        // Perform encryption
        byte[] encrypted = cipher.doFinal(plaintext.getBytes()); // Encrypt data
        byte[] combined = new byte[IV_SIZE + encrypted.length];

        // Combine IV and encrypted data
        System.arraycopy(iv, 0, combined, 0, IV_SIZE);
        System.arraycopy(encrypted, 0, combined, IV_SIZE, encrypted.length);

        // Return Base64 encoded result
        return EncodingUtil.encodeByteArrayToBase64(combined); // Display in readable format
    }

    /**
     * Decrypts an AES-encrypted, Base64-encoded string produced by the {@link #encrypt(byte[], String)}} method.
     * Extracts the IV from the first 16 bytes of the decoded data.
     *
     * @param key Secret key as a byte array. Must match the original encryption key.
     * @param ciphertext Base64-encoded string containing the IV and ciphertext.
     * @return The decrypted plaintext string.
     * @throws NoSuchAlgorithmException              If the AES algorithm is unavailable.
     * @throws NoSuchPaddingException                If the padding scheme is unavailable.
     * @throws InvalidKeyException                   If the provided key is invalid.
     * @throws InvalidAlgorithmParameterException    If the IV parameter is invalid.
     * @throws IllegalBlockSizeException             If the ciphertext size is incorrect.
     * @throws BadPaddingException                   If padding is improperly applied or decryption fails.
     */
    public static String decrypt(byte[] key, String ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] decoded = EncodingUtil.decodeBase64ToByteArray(ciphertext);

        // Extract IV and encrypted content
        byte[] iv = new byte[IV_SIZE];
        byte[] encryptedBytes = new byte[decoded.length - IV_SIZE];
        System.arraycopy(decoded, 0, iv, 0, IV_SIZE);
        System.arraycopy(decoded, IV_SIZE, encryptedBytes, 0, decoded.length - IV_SIZE);

        // Init cipher
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(key, AES_KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        // Perform decryption
        return new String(cipher.doFinal(encryptedBytes));
    }

    /**
     * Generates a cryptographically secure random AES key.
     * @return The generated AES key as a byte array.
     * @throws NoSuchAlgorithmException If the AES algorithm is unavailable.
     */
    public static byte[] generateRandomKey() throws NoSuchAlgorithmException{
        KeyGenerator keyGenerator = KeyGenerator.getInstance(CryptoSettings.AES_KEY_ALGORITHM);
        keyGenerator.init(CryptoSettings.AES_RANDOM_KEY_SIZE_BITS);
        return keyGenerator.generateKey().getEncoded();

    }

}
