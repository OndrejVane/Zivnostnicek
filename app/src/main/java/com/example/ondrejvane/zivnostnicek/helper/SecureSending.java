package com.example.ondrejvane.zivnostnicek.helper;

/**
 * Tato třída je umožněná k pozdějšímu doimplementování
 * šifrovaní při odesílání dat na server.
 */
public class SecureSending {

    private String key;
    private String initialVector;

    public SecureSending(String key, String initialVector){
        this.key = key;
        this.initialVector = initialVector;
    }


    /**
     * Zde je možno doplnit šifrovací funkci.
     * @param input plain text
     * @return      encrypted text
     */
    public String encrypt(String input){
        return input;
    }


    /**
     * Zde je možno doimplementovat dešifrovací funkci.
     * @param input encrypted text
     * @return      plain text
     */
    public String decrypt(String input){
        return input;
    }
}
