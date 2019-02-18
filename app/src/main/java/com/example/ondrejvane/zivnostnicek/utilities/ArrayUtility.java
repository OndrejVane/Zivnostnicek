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

    /**
     * Metoda, která sočte průměrné hodnocení obchodníka.
     * @return  float průměrné hodnocení obchodníka
     */
    public static float countAverageRating(String[] rating) {
        float sum = 0;
        float averageRating;
        for (int i = 0; i<rating.length; i++){
            sum = sum + Float.parseFloat(rating[i]);
        }
        averageRating = sum/rating.length;
        return (float) (Math.round(averageRating * Math.pow(10, 2)) / Math.pow(10, 2));
    }
}
