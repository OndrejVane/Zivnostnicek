package com.example.ondrejvane.zivnostnicek.helper;

import android.app.Activity;
import android.content.SharedPreferences;

import com.example.ondrejvane.zivnostnicek.session.UserInformation;

import static android.content.Context.MODE_PRIVATE;

public class Settings {

    //tato hodnota udává, zda je možno vkládat do aplikace cizí IČ
    private boolean isForeignIdentificationNumberPossible = false;

    //tato hodnota udává, zda je možno vkládat do aplikace cizí DIČ
    private boolean isForeignTaxIdentificationNumberPossible = false;

    //tato hodnota udává, zda se budou zobrazovat data poze za jeden vybraný rok
    private boolean isPickedOneYear = false;

    //vybranný rok
    private String year = null;

    //id vybraného roku
    private int arrayYearId = -1;

    //tato hodnota udává, zda se budou zobrazovat data puoze pro jeden vybraný měsíc
    private boolean isPickedOneMonth = false;

    //vybranný měsíc
    private String month = null;

    //id vybraného měsíce
    private int arrayMonthId = -1;

    //udržuje informaci o tom, jestli je synchronizace zapnuta
    private boolean isSyncOn = false;

    private boolean isSyncAllowWifi = false;

    //instance této třídy (Singleton)
    private static Settings self = null;

    //klíče ke všem nastavením
    private static final String SETTINGS_NAME_SP = "SETTINGS";
    private static final String FOREIGN_IN_KEY = "FOREIGN_IN_ID_";
    private static final String FOREIGN_TIN_KEY = "FOREIGN_TIN_ID_";
    private static final String PICKED_YEAR_KEY = "PICKED_YEAR_ID_";
    private static final String ARRAY_INDEX_YEAR_KEY = "ARRAY_INDEX_YEAR_ID_";
    private static final String YEAR_KEY = "YEAR_ID_";
    private static final String PICKED_MONTH_KEY = "PICKED_MONTH_ID_";
    private static final String ARRAY_INDEX_MONTH_KEY = "ARRAY_INDEX_MONTH_ID_";
    private static final String MONTH_KEY = "MONTH_ID_";
    private static final String SYNC_ON_KEY = "SYNC_ON_ID_";
    private static final String SYNC_WIFI_ON_KEY = "SYNC_WIFI_ON_ID_";


    /**
     * Privátní konstruktor třídy protože se jedná o singleton
     */
    private Settings() {

    }

    /**
     * Pokud neexistuje instance této třídy, tak jí tato
     * procedura vytvoří. Pokud už instace existuje, tak
     * vrátí tuto instanci.
     *
     * @return instance této třídy
     */
    public static synchronized Settings getInstance() {
        if (self == null) {
            self = new Settings();
        }
        return self;
    }

    public boolean isIsForeignIdentificationNumberPossible() {
        return isForeignIdentificationNumberPossible;
    }

    public void setIsForeignIdentificationNumberPossible(boolean isForeignIdentificationNumberPossible) {
        this.isForeignIdentificationNumberPossible = isForeignIdentificationNumberPossible;
    }

    public boolean isIsForeignTaxIdentificationNumberPossible() {
        return isForeignTaxIdentificationNumberPossible;
    }

    public void setIsForeignTaxIdentificationNumberPossible(boolean isForeignTaxIdentificationNumberPossible) {
        this.isForeignTaxIdentificationNumberPossible = isForeignTaxIdentificationNumberPossible;
    }

    public boolean isIsPickedOneYear() {
        return isPickedOneYear;
    }

    public void setIsPickedOneYear(boolean isPickedOneYear) {
        this.isPickedOneYear = isPickedOneYear;
    }

    public String getYear() {
        return year;
    }

    public int getArrayYearId() {
        return arrayYearId;
    }

    public void setArrayYearId(int arrayYearId) {
        this.arrayYearId = arrayYearId;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isPickedOneMonth() {
        return isPickedOneMonth;
    }

    public void setIsPickedOneMonth(boolean pickedOneMonth) {
        isPickedOneMonth = pickedOneMonth;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getArrayMonthId() {
        return arrayMonthId;
    }

    public void setArrayMonthId(int arrayMonthId) {
        this.arrayMonthId = arrayMonthId;
    }

    public boolean isSyncOn() {
        return isSyncOn;
    }

    public void setSyncOn(boolean syncOn) {
        isSyncOn = syncOn;
    }

    public boolean isSyncAllowWifi() {
        return isSyncAllowWifi;
    }

    public void setSyncAllowWifi(boolean syncAllowWifi) {
        isSyncAllowWifi = syncAllowWifi;
    }

    public void resetInstance() {
        self = null;
    }


    /**
     * Uložení aktuálního nastavení do shared preferences.
     *
     * @param activity aktivity, ze které je metoda volána
     */
    public void saveSettingsToSharedPreferences(Activity activity) {
        String userId = Integer.toString(UserInformation.getInstance().getUserId());
        SharedPreferences sp = activity.getSharedPreferences(SETTINGS_NAME_SP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(FOREIGN_IN_KEY + userId, isForeignIdentificationNumberPossible);            //uložení hodnoty pro zobrazení zaharaničního IČ
        editor.putBoolean(FOREIGN_TIN_KEY + userId, isForeignTaxIdentificationNumberPossible);        //uložení hodnoty pro zobrazení zahraníčního DIČ
        editor.putBoolean(PICKED_YEAR_KEY + userId, isPickedOneYear);                                 //uložení hodnoty pro zobrazování dat v aplikaci pouze pro jeden rok
        editor.putInt(ARRAY_INDEX_YEAR_KEY + userId, arrayYearId);                                    //uložení indexu v seznamu roku podle vybraného roku ve spinneru
        editor.putString(YEAR_KEY + userId, year);                                                    //uložení vybraného roku pro zobrazování dat
        editor.putBoolean(PICKED_MONTH_KEY + userId, isPickedOneMonth);                               //uložení hodnoty, zda je vybranný jeden měsíc
        editor.putInt(ARRAY_INDEX_MONTH_KEY + userId, arrayMonthId);                                  //uložení indexu měsíce
        editor.putString(MONTH_KEY + userId, month);                                                  //uložení názvu vybraného měsíce
        editor.putBoolean(SYNC_ON_KEY + userId, isSyncOn);                                            //uložení nastavení o zapnuté synchronizaci
        editor.putBoolean(SYNC_WIFI_ON_KEY + userId, isSyncAllowWifi);                                //uložení zda je poovlená synchroniza puze přes wifi
        editor.apply();
    }

    /**
     * Načtení aktuálního nastavení ze shared preferences.
     *
     * @param activity aktivita, ze které je metoda volána
     */
    public void readSettingsFromSharedPreferences(Activity activity) {
        String userId = Integer.toString(UserInformation.getInstance().getUserId());
        SharedPreferences sp = activity.getSharedPreferences(SETTINGS_NAME_SP, MODE_PRIVATE);

        isForeignIdentificationNumberPossible = sp.getBoolean(FOREIGN_IN_KEY + userId, false);
        isForeignTaxIdentificationNumberPossible = sp.getBoolean(FOREIGN_TIN_KEY + userId, false);
        isPickedOneYear = sp.getBoolean(PICKED_YEAR_KEY + userId, false);
        arrayYearId = sp.getInt(ARRAY_INDEX_YEAR_KEY + userId, -1);
        year = sp.getString(YEAR_KEY + userId, null);
        isPickedOneMonth = sp.getBoolean(PICKED_MONTH_KEY + userId, false);
        arrayMonthId = sp.getInt(ARRAY_INDEX_MONTH_KEY + userId, -1);
        month = sp.getString(MONTH_KEY + userId, null);
        isSyncOn = sp.getBoolean(SYNC_ON_KEY + userId, false);
        isSyncAllowWifi = sp.getBoolean(SYNC_WIFI_ON_KEY + userId, false);
    }

}
