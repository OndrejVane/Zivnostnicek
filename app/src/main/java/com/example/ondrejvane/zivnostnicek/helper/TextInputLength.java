package com.example.ondrejvane.zivnostnicek.helper;

/**
 * V této třídě jsou uvedene konstanty, které
 * udávají maximální délku všech vstupních řetezců v
 * aplikaci. (Délky jsou určeny podle serverové databáze)
 */
public class TextInputLength {

    /*
    Hodnoty pro USER
     */

    //maximální délka celého jména
    public static final int USER_FULL_NAME_LENGTH = 45;

    //maximální délka emailu
    public static final int USER_EMAIL_LENGTH = 45;

    //minimální délka hesla
    public static final int PASSWORD_MIN_LENGTH = 8;


    /*
    Hodnoty pro TRADER
     */

    //maximální délka názvu obchodníka
    public static final int TRADER_NAME_LENGTH = 45;

    //maximální délka kontaktní osoby
    public static final int TRADER_CONTACT_PERSON_LENGTH = 45;

    //maximální délka názvu města
    public static final int TRADER_CITY_LENGTH = 45;

    //maxiální délka čísla popisného
    public static final int TRADER_HOUSE_NUMBER_LENGTH = 10;

    //maximální délka názvu ulice
    public static final int TRADER_STREET_LENGTH = 45;

    /*
    Hodnoty pro NOTE
     */

    //maxiální délka nadpisu poznámky
    public static final int NOTE_TITLE_LENGTH = 45;

    //maximální délka textu poznáky
    public static final int NOTE_TEXT_LENGTH = 150;


    /*
    Hodnoty STORAGE ITEM
     */

    //maximální délka pro název skladové položky
    public static final int STORAGE_ITEM_NAME_LENGTH = 45;

    //maximální délka poznámky ke skladové položce
    public static final int STORAGE_ITEM_NOTE_LENGTH = 150;

    /*
    Hodnoty BILL TYPE
     */

    //maximální délka názvu typu faktury
    public static final int BILL_TYPE_NAME_LENGTH = 45;


    /*
    Hodnoty BILL
     */

    //maximální délka názvu faktury
    public static final int BILL_NAME_LENGTH = 45;


}
