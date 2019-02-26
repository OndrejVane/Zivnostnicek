package com.example.ondrejvane.zivnostnicek.helper;

import android.util.Log;

/**
 * Třída, která obsahuje statické funkce pro validování
 * vstupů od uživatele napříč celou apliakcí.
 */
public class InputValidation {

    /**
     * Kostruktor třídy je privátní,aby nešla
     * vytvořit instance této třídy.
     */
    private InputValidation() {

    }

    /**
     * Metoda, která validuje jediný povinný údaj ve formuláři.
     * Pokud je prázdný, metoda vrací false.
     *
     * @return boolean
     */
    public static boolean validateCompanyName(String input) {
        if (input.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Metoda, která validuje zadaný telefon od uživatele.
     * Telefonní číslo je nepovinné. Pokud je tedy prázdé, tak
     * metoda vrací true. Metoda akceptuje formát 123456789
     * a formát s předvolbou +420 123456789.
     *
     * @return boolean
     */
    public static boolean validatePhoneNumber(String telephoneNumber) {
        if (telephoneNumber.isEmpty()) {
            return true;
        } else {
            if (telephoneNumber.startsWith("+")) {
                if (telephoneNumber.length() == 13) {
                    return true;
                } else {
                    return false;
                }
            }
            if (Character.isDigit(telephoneNumber.charAt(0))) {
                if (telephoneNumber.length() == 9) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Metoda, která validuje IČO. Formát iča je 8 číslic.
     * Poslední číslice je kontrolní. Algoritmus pro ověření iča
     * zde => https://cs.wikipedia.org/wiki/Identifika%C4%8Dn%C3%AD_%C4%8D%C3%ADslo_osoby
     * <p>
     * Validní tvar iča je : 12345678
     *
     * @return boolean
     */
    public static boolean validateIdentificationNumber(String identificationNumber) {
        identificationNumber = identificationNumber.toUpperCase();
        int count = 8;
        int sum = 0;
        int expectedValue;
        int trueValue;

        //pokud je prázdné je to ok
        if(identificationNumber.isEmpty()){
            return true;
        }

        //kontrola délky řetězce
        if (identificationNumber.length() == 8) {

            for (int i = 0; i < (identificationNumber.length() - 1); i++) {
                //kontrola, zda jde o číslici
                if (Character.isDigit(identificationNumber.charAt(i))) {

                    int temp = Character.getNumericValue(identificationNumber.charAt(i));
                    sum = sum + (count * temp);
                    count--;
                } else {

                    return false;
                }

            }

            expectedValue = (11 - (sum % 11)) % 10;
            trueValue = Integer.parseInt(identificationNumber.substring(identificationNumber.length() - 1));

            return expectedValue == trueValue;

        } else {
            return false;
        }
    }

    /**
     * Metoda, která validuje české DIČ. Kontroluje, zda obsahuje první dvě písmena CZ,
     * která označují kod země. Po té musí následovat 8-10 číslic.
     * https://cs.wikipedia.org/wiki/Da%C5%88ov%C3%A9_identifika%C4%8Dn%C3%AD_%C4%8D%C3%ADslo
     *
     * Validní tvar DIč je : CZ12345678
     *@param taxIdentificationNumber české DIČ
     * @return boolean, zda je dič správné
     */
    public static boolean validateCzechTaxIdentificationNumber(String taxIdentificationNumber) {
        taxIdentificationNumber = taxIdentificationNumber.toUpperCase();
        int inputLength = taxIdentificationNumber.length();
        if (taxIdentificationNumber.isEmpty()) {
            return true;
        } else {
            if (inputLength >= 10 && inputLength <= 12) {
                if ((Character.isLetter(taxIdentificationNumber.charAt(0)) && Character.isLetter(taxIdentificationNumber.charAt(1)))
                        &&
                        (taxIdentificationNumber.charAt(0) == 'C' && taxIdentificationNumber.charAt(1) == 'Z')) {
                    for (int i = 2; i < inputLength; i++) {
                        if (!Character.isDigit(taxIdentificationNumber.charAt(i))) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Metoda, která validuje zahraniční identifikační číslo
     *
     * @param taxIdentificationNumber zahraniční identifikační číslo
     * @return boolean, zda je DIČ správné
     */
    public static boolean validateForeignTaxIdentificationNumber(String taxIdentificationNumber) {
        taxIdentificationNumber = taxIdentificationNumber.toUpperCase();
        int inputLength = taxIdentificationNumber.length();
        if (taxIdentificationNumber.isEmpty()) {
            return true;
        } else {
            if (inputLength >= 10 ) {
                if (Character.isLetter(taxIdentificationNumber.charAt(0)) && Character.isLetter(taxIdentificationNumber.charAt(1))){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Metoda, která validuje, zda je string prázdný.
     *
     * @return
     */
    public static boolean validateNote(String input) {
        if (input.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Metoda, která validuje, zda je string prázdný.
     *
     * @return
     */
    public static boolean validateIsEmpty(String input) {
        if (input.isEmpty()) {
            return false;
        }
        return true;
    }

}
