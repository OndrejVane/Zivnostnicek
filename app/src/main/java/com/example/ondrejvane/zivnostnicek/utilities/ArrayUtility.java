package com.example.ondrejvane.zivnostnicek.utilities;

public class ArrayUtility {

    /**
     * Metoda, která převede pole stringu(čísel) na pole integeru.
     *
     * @param strings pole stringu(čísel)
     * @return pole integeru
     */
    public static int[] arrayStringToInteger(String[] strings) {
        int[] integers = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            integers[i] = Integer.parseInt(strings[i]);
        }
        return integers;

    }
}
