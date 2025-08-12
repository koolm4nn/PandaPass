package com.pandaPass.configs;

/**
 * Defines algorithms, key sizes, iterations and other constants used throughout the app
 * for hashing, key derivation and encryption.
 */
public class CryptoSettings {
    // AES configurations
    public static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String AES_KEY_ALGORITHM = "AES";
    public static final int AES_IV_SIZE_BYTES = 16;
    public static final int AES_RANDOM_KEY_SIZE_BITS = 256;

    // PBKDF2 configurations
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA512";
    public static final int PBKDF2_ITERATION_COUNT = 1048576; // 2^20
    public static final int PBKDF2_KEY_LENGTH_BITS = 256; // Standard for PBKDF2 with HMAC-SHA512

    // Hashing configurations
    public static final String HASH_ALGORITHM = "HmacSHA512";

    private CryptoSettings(){}
}
