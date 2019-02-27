package com.example.ondrejvane.zivnostnicek.helper;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Třída, která implementuje metodu pro šifrování a
 * dešifrování hesla pro lokální uložení do databáze.
 * Heslo je šifrováno, aby nebylo možné hash zkopírovat a
 * zaslat někým jiným na server.
 */
public class SecurePassword {

    private static final String ALGORITHM = "Aes";
    private static final byte[] keyValue =
            new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};


    public static String encrypt(String input){
        String strData="";

        try {
            SecretKeySpec skeyspec=new SecretKeySpec(keyValue, ALGORITHM);
            Cipher cipher=Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            byte[] encrypted=cipher.doFinal(input.getBytes());
            strData=new String(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strData;
    }



    public static String decrypt(String input){
        String strData="";

        try {
            SecretKeySpec skeyspec=new SecretKeySpec(keyValue,ALGORITHM);
            Cipher cipher=Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted=cipher.doFinal(input.getBytes());
            strData=new String(decrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strData;
    }



}
