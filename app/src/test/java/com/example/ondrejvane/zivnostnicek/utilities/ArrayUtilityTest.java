package com.example.ondrejvane.zivnostnicek.utilities;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayUtilityTest {

    @Test
    public void arrayStringToInteger() {
        String[] strings1 = {"1", "2", "3", "4", "10000", "123"};
        int[] integers1 = {1, 2, 3, 4, 10000, 123};
        assertArrayEquals(integers1, ArrayUtility.arrayStringToInteger(strings1));

        String[] strings2 = {"12", "234", "1200303", "0"};
        int[] integers2 = {12, 234, 1200303, 0};
        assertArrayEquals(integers2, ArrayUtility.arrayStringToInteger(strings2));
    }

    @Test
    public void countAverageRating() {
        String[] strings1 = {"1", "2", "3", "4", "10000", "123"};
        float result1 = 1688.83f;
        double delta1 = 0.001;
        assertEquals(result1, ArrayUtility.countAverageRating(strings1), delta1);

        String[] strings2 = {"12", "234", "1200303", "0"};
        float result2 = 300137.25f;
        double delta2 = 0.0;
        assertEquals(result2, ArrayUtility.countAverageRating(strings2), delta2);

    }
}