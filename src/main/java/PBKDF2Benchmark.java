import com.pandaPass.utils.EncodingUtil;

import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

public class PBKDF2Benchmark {

    public static void benchmark() throws Exception {
        String password = "BenchmarkTestPasswordForTestingDifferentDurationOfIterations";
        byte[] salt = generateSalt(); // Generate random salt

        // Test different iteration counts
        int[] iterationsList = { 50000, 100000, 200000, 310000, 500000, 1000000, 2000000, 4000000, 8000000, 16000000 };

        System.out.println("Benchmarking PBKDF2-HMAC-SHA256:");
        for (int iterations : iterationsList) {
            long startTime = System.nanoTime();
            String hash = hashPassword(password, salt, iterations);
            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            System.out.printf("Iterations: %d -> Time: %d ms%n", iterations, durationMs);
        }
    }

    public static String hashPassword(String password, byte[] salt, int iterations) throws Exception {
        int keyLength = 256; // Standard key length for PBKDF2
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return EncodingUtil.encodeByteArrayToBase64(hash);
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[16]; // 128-bit salt
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
