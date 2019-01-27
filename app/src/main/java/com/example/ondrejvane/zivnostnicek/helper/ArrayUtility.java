package com.example.ondrejvane.zivnostnicek.helper;

public class ArrayUtility {

    /**
     * Metoda, která převede pole strngu(čísel) na pole integeru.
     * @param strings   String[]    pole stringu(čísel)
     * @return          int[]       pole integeru
     */
    public static int[] arrayStringToInteger(String[] strings) {
        int[] integers = new int[strings.length];
        for (int i = 0; i<strings.length; i++){
            integers[i] = Integer.parseInt(strings[i]);
        }
        return integers;

    }
}
