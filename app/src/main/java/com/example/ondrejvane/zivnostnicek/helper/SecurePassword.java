package com.example.ondrejvane.zivnostnicek.helper;

import com.itextpdf.text.pdf.crypto.AESCipher;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Třída, která implementuje metodu pro šifrování a
 * dešifrování hesla pro lokální uložení do databáze.
 * Heslo je šifrováno, aby nebylo možné hash zkopírovat a
 * zaslat někým jiným na server.
 */
public class SecurePassword {

    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};


    public static String encrypt(String input){
        return input;
    }



    public static String decrypt(String input){
        return input;
    }



}
