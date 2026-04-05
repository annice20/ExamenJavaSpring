package com.example.examen.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class TotpService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    // Génère une clé secrète TOTP
    public String generateSecret() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    // Génère l'URL TOTP standard (otpauth://) pour le QR Code
    public String generateQrCodeDataUri(String secret, String email) throws Exception {
        String otpauthUrl = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                "MonApp",   // nom de l'application (affiché dans Google Authenticator)
                email,
                new GoogleAuthenticatorKey.Builder(secret).build()
        );

        // Génération de l'image QR Code avec ZXing
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(otpauthUrl, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());

        return "data:image/png;base64," + base64;
    }

    // Vérifie le code saisi par l'utilisateur
    public boolean verifyCode(String secret, String code) {
        try {
            int totpCode = Integer.parseInt(code.trim());
            return gAuth.authorize(secret, totpCode);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}