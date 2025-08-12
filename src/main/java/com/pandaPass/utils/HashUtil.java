package com.pandaPass.utils;

import com.pandaPass.configs.CryptoSettings;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for secure hashing and password-based key derivation.
 * Implements:
 * <ul>
 *     <li><b>PBKDF2 with HMAC-SHA256</b>: Used for deriving cryptographic keys from passwords with a unique salt and high iteration count to resist brute-force attacks.</li>
 *     <li><b>HMAC-SHA512 hashing</b>: For message authentication using a secret key.</li>
 *     <li>Secure random salt generation.</li>
 * </ul>
 */
public class HashUtil {
    private static final int PBKDF2_ITERATIONS = CryptoSettings.PBKDF2_ITERATION_COUNT; // 2^20
    private static final int PBKDF2_KEY_LENGTH = CryptoSettings.PBKDF2_KEY_LENGTH_BITS; // Standard for PBKDF2 with HMAC-SHA512
    private static final String PBKDF2_ALGORITHM = CryptoSettings.PBKDF2_ALGORITHM;
    private static final String HASH_ALGORITHM = CryptoSettings.HASH_ALGORITHM;

    private static final String SHA_1_ALGORITHM = "SHA-1";

    private static final int DEFAULT_SALT_SIZE_BYTES = 32;

    private HashUtil(){}

    /**
     * Derives a cryptographic key from a password using PBKDF2 with HMAC-SHA256.
     * <p>
     * The function applies a secure salt and an adjustable iteration count to slow down brute-force attempts.
     *
     * @param password The plaintext password to derive the key from.
     * @param salt A securely generated random salt.
     * @return The derived cryptographic key as a byte array.
     * @throws NoSuchAlgorithmException If the PBKDF2 algorithm is unavailable.
     * @throws InvalidKeySpecException  If the key specification is invalid.
     */
    public static byte[] deriveKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH);

        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * Computes a HMAC-SHA512 hash of the provided message using the specified key.
     *
     * @param key The secret key used for HMAC computation.
     * @param message The plaintext message to be hashed.
     * @return A Base64-encoded string representation of the hash.
     * @throws NoSuchAlgorithmException If the HMAC algorithm is unavailable.
     * @throws InvalidKeyException If the provided key is invalid.
     */
    public static String hashHMAC(byte[] key, String message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HASH_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key, HASH_ALGORITHM);
        mac.init(keySpec);

        byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return EncodingUtil.encodeByteArrayToBase64(hash);
    }
    /**
     * Generates a secure, random salt for use in cryptographic operations.
     *
     * @return A byte array containing a securely generated random salt.
     */
    public static byte[] generateSalt(){
        byte[] salt = new byte[DEFAULT_SALT_SIZE_BYTES];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static String hashSha1(String message) {
        try{
            MessageDigest digest = MessageDigest.getInstance(SHA_1_ALGORITHM);
            byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e){
            System.err.println("Invalid algorithm " + SHA_1_ALGORITHM + " selected for hashing sha-1.");
            return "";
        }
    }

    private static String bytesToHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }
}
