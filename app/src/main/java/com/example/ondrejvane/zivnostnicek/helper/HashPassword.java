package com.example.ondrejvane.zivnostnicek.helper;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Třída která generuje hash hesla. Hash je následně ukládán do
 * databáze pro větší bezpečnost. Použitá hashovací funkce je SHA-512.
 */
public class HashPassword {


    private final String algorithm = "SHA-512";
    private final char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Metoda, která převede jednotlivé byty na hexadecimální znakový řetězec.
     *
     * @param bytes vstupní pole bytů
     * @return řetězec v hecadecimálním tvaru
     */
    private String bytesToStringHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Metoda, která převede vstupní řetězec pomocí příslušného algoritmu na hash.
     * Algoritmus je definovaný v konstantě algorithm.
     *
     * @param data vstupní řetězec
     * @return hash vstupního řetězce v bytech
     */
    public String hashPassword(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.reset();
            byte[] hash = messageDigest.digest(data.getBytes());
            return bytesToStringHex(hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d("HashPassword", "Algorithm not found");
            return null;
        }

    }
}
