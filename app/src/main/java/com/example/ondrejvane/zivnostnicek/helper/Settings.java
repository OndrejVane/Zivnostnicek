package com.example.ondrejvane.zivnostnicek.helper;

import android.app.Activity;
import android.content.SharedPreferences;

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

    //instance této třídy (Singleton)
    private static Settings self = null;

    private Settings(){

    }

    public static synchronized Settings getInstance() {
        if (self == null){
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

    public void resetInstance(){
        self = null;
    }

    /**
     * Uložení aktuálního nastavení do shared preferences.
     * @param activity  aktivity, ze které je metoda volána
     */
    public void saveSettingsToSharedPreferences(Activity activity){
        String userId = Integer.toString(UserInformation.getInstance().getUserId());
        SharedPreferences sp = activity.getSharedPreferences("SETTINGS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("FOREIGN_IN_ID_"+userId, isForeignIdentificationNumberPossible);            //uložení hodnoty pro zobrazení zaharaničního IČ
        editor.putBoolean("FOREIGN_IN_TID_"+userId, isForeignTaxIdentificationNumberPossible);        //uložení hodnoty pro zobrazení zahraníčního DIČ
        editor.putBoolean("PICKED_YEAR_ID_"+userId, isPickedOneYear);                                 //uložení hodnoty pro zobrazování dat v aplikaci pouze pro jeden rok
        editor.putInt("ARRAY_INDEX_YEAR_ID_"+userId, arrayYearId);                                    //uložení indexu v seznamu roku podle vybraného roku ve spinneru
        editor.putString("YEAR_ID_"+userId, year);                                                    //uložení vybraného roku pro zobrazování dat
        editor.apply();
    }

    /**
     * Načtení aktuálního nastavení ze shared preferences.
     * @param activity  aktivita, ze které je metoda volána
     */
    public void readSettingsFromSharedPreferences(Activity activity){
        String userId = Integer.toString(UserInformation.getInstance().getUserId());
        SharedPreferences sp = activity.getSharedPreferences("SETTINGS", MODE_PRIVATE);

        isForeignIdentificationNumberPossible = sp.getBoolean("FOREIGN_IN_ID_"+userId, false);
        isForeignTaxIdentificationNumberPossible = sp.getBoolean("FOREIGN_IN_TID_"+userId,false);
        isPickedOneYear = sp.getBoolean("PICKED_YEAR_ID_"+userId,false);
        year = sp.getString("YEAR_ID_"+userId, null);
        arrayYearId = sp.getInt("ARRAY_INDEX_YEAR_ID_"+userId, -1);
    }

}
