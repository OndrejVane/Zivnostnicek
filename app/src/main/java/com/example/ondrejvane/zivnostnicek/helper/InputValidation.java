package com.example.ondrejvane.zivnostnicek.helper;

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
     * Metoda, která validuje IČO. Kontroluje, zda obsahuje první dva znaky a po té 8
     * číslic. Následně ověří validitu iča podle algoritmu pro ověření iča.
     * Pokud je pole prázdné vrací true protože je to nepovinný údaj.
     * Odkaz na algoritmus: https://phpfashion.com/jak-overit-platne-ic-a-rodne-cislo
     *
     * @return boolean
     */
    public static boolean validateIdentificationNumber(String identificationNumber) {
        int temp = 0;
        int a, c;
        if (identificationNumber.isEmpty()) {
            return true;
        } else {
            if (identificationNumber.length() == 10) {
                if (Character.isLetter(identificationNumber.charAt(0)) && Character.isLetter(identificationNumber.charAt(1))) {
                    for (int i = 2; i < identificationNumber.length() - 1; i++) {
                        if (Character.isDigit(identificationNumber.charAt(i))) {
                            temp = temp + (Character.getNumericValue(identificationNumber.charAt(i)) * (10 - i));
                        } else {
                            return false;
                        }
                    }
                    a = temp % 11;
                    if (a == 0) {
                        c = 1;
                    } else if (a == 1) {
                        c = 0;
                    } else {
                        c = 11 - temp;
                    }
                    if (Character.getNumericValue(identificationNumber.charAt(9)) == c) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Metoda, která validuje DIČ.
     *
     * @return
     */
    public static boolean validateTaxIdentificationNumber(String taxIdentificationNumber) {
        int inputLength = taxIdentificationNumber.length();
        if (taxIdentificationNumber.isEmpty()) {
            return true;
        } else {
            if (inputLength >= 10 && inputLength <= 12) {
                if (Character.isLetter(taxIdentificationNumber.charAt(0)) && Character.isLetter(taxIdentificationNumber.charAt(1))) {
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
