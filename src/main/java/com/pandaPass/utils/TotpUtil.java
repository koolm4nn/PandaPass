package com.pandaPass.utils;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javafx.embed.swing.SwingFXUtils;

import javax.crypto.SecretKey;

public class TotpUtil {
    private static final String TOTP_URI_FORMAT = "otpauth://totp/%s:%s?secret=%s&issuer=%s&digits=6&period=30";
    private static final int qrCodeWidth = 500;
    private static final int qrCodeHeight = 500;

    private static final String TOTP_ALGORITHM = "HmacSHA1";

    private static final TimeBasedOneTimePasswordGenerator TOTP_GENERATOR;

    static {
        try{
            TOTP_GENERATOR = new TimeBasedOneTimePasswordGenerator(
                    Duration.of(30, ChronoUnit.SECONDS),
                    6,
                    TOTP_ALGORITHM);

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }

    }

    public static byte[] generateTotpSecret(){
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    private static String generateTotpUri(String accountName, String issuer, byte[] totpSecret){
        String encodedSecret = EncodingUtil.encodeByteArrayToBase32(totpSecret);

        return String.format(TOTP_URI_FORMAT, issuer, accountName, encodedSecret, issuer);
    }

    public static Image generateQrCode(String accountName, String issuer, byte[] totpSecret){
        String uri = generateTotpUri(accountName, issuer, totpSecret);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try{
            BitMatrix bitMatrix = qrCodeWriter.encode(uri, BarcodeFormat.QR_CODE, qrCodeWidth, qrCodeHeight);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            return SwingFXUtils.toFXImage(bufferedImage, null);

        } catch (WriterException e){
            System.err.println("Error creating qr code: " + e.getMessage());
        }

        return SwingFXUtils.toFXImage(new BufferedImage(qrCodeWidth, qrCodeHeight, BufferedImage.TYPE_BYTE_BINARY), null);
    }

    public static boolean verifyTotpCode(byte[] totpSecret, String userCode) throws InvalidKeyException {
        if(userCode == null || !userCode.matches("\\d{6}")){
            System.err.println("Invalid user code format: " + userCode);
            return false;
        }
        try{
            SecretKey key = new javax.crypto.spec.SecretKeySpec(totpSecret, TOTP_ALGORITHM);

            String expectedCode = String.format("%06d", TOTP_GENERATOR.generateOneTimePassword(key, Instant.now()));
            return expectedCode.equals(userCode);
        } catch (Exception e) {
            System.err.println("Error verifying totp Code: " + e.getMessage());
        }
        return false;
    }
}
