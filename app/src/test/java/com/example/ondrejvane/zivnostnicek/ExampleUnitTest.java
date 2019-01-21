package com.example.ondrejvane.zivnostnicek;

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
}