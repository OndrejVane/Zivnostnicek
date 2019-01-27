package com.example.ondrejvane.zivnostnicek;

import com.example.ondrejvane.zivnostnicek.helper.FormatUtility;
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void taxIdentificationNumber(){
        boolean result = InputValidation.validateIdentificationNumber("CZ27082440");
        assertEquals(true, result);
    }

    @Test
    public void testGetMonthFromDate(){
        String result = FormatUtility.getMonthFromDate("12.2.2019");
        assertEquals("2", result);

        result = FormatUtility.getMonthFromDate("12.12.2019");
        assertEquals("12", result);

        result = FormatUtility.getMonthFromDate("12.8.2019");
        assertEquals("8", result);
    }

    @Test
    public void testGetYearFromString(){
        String result = FormatUtility.getYearFromDate("12.8.2019");
        assertEquals("2019", result);

        result = FormatUtility.getYearFromDate("12.8.2123");
        assertEquals("2123", result);
    }
}