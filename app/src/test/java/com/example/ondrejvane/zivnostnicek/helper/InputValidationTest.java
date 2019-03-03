package com.example.ondrejvane.zivnostnicek.helper;

import org.junit.Test;

import static org.junit.Assert.*;

public class InputValidationTest {

    @Test
    public void validateCompanyName() {
        assertEquals(false, InputValidation.validateCompanyName(""));
        assertEquals(true, InputValidation.validateCompanyName("aasdhjah"));
        assertEquals(true, InputValidation.validateCompanyName("123abc"));
    }

    @Test
    public void validatePhoneNumber() {
        assertEquals(true, InputValidation.validatePhoneNumber("+420123456789"));
        assertEquals(true, InputValidation.validatePhoneNumber("123456789"));
    }

    @Test
    public void validateIdentificationNumber() {
        assertEquals(true, InputValidation.validateIdentificationNumber("49777513"));   //zču
        assertEquals(true, InputValidation.validateIdentificationNumber("27082440"));   //alza.cz
        assertEquals(true, InputValidation.validateIdentificationNumber("40524485"));   //elfetex
        assertEquals(true, InputValidation.validateIdentificationNumber("25655701"));   //czc.cz

        assertEquals(false, InputValidation.validateIdentificationNumber("49777514"));
        assertEquals(false, InputValidation.validateIdentificationNumber("27182440"));
        assertEquals(false, InputValidation.validateIdentificationNumber("40524685"));
        assertEquals(false, InputValidation.validateIdentificationNumber("35655701"));
    }


    @Test
    public void validateCzechTaxIdentificationNumber() {
        assertEquals(true, InputValidation.validateCzechTaxIdentificationNumber("CZ49777513"));  //zču
        assertEquals(true, InputValidation.validateCzechTaxIdentificationNumber("CZ27082440"));  //alza.cz
        assertEquals(true, InputValidation.validateCzechTaxIdentificationNumber("CZ40524485"));  //elfetex
        assertEquals(true, InputValidation.validateCzechTaxIdentificationNumber("cz25655701"));  //czc.cz
        assertEquals(true, InputValidation.validateCzechTaxIdentificationNumber("CZ12345678"));  //náhodné1
        assertEquals(true, InputValidation.validateCzechTaxIdentificationNumber("CZ123456789"));  //náhodné2
        assertEquals(true, InputValidation.validateCzechTaxIdentificationNumber("CZ1234567890"));  //náhodné3

        assertEquals(false, InputValidation.validateCzechTaxIdentificationNumber("CZC49777513"));
        assertEquals(false, InputValidation.validateCzechTaxIdentificationNumber("CZ1234567"));
        assertEquals(false, InputValidation.validateCzechTaxIdentificationNumber("C12345678"));
        assertEquals(false, InputValidation.validateCzechTaxIdentificationNumber("12345678"));

    }

    @Test
    public void validateForeignTaxIdentificationNumber(){
        assertEquals(true, InputValidation.validateForeignTaxIdentificationNumber("lt49777513"));
        assertEquals(true, InputValidation.validateForeignTaxIdentificationNumber("HU49777513"));
        assertEquals(true, InputValidation.validateForeignTaxIdentificationNumber("BE49777513"));
        assertEquals(true, InputValidation.validateForeignTaxIdentificationNumber("bg49777513"));
    }

    @Test
    public void validateNote() {
        assertEquals(false, InputValidation.validateNote(""));
        assertEquals(true, InputValidation.validateNote("aasdhjah"));
        assertEquals(true, InputValidation.validateNote("123abc"));
    }

    @Test
    public void validateIsEmpty() {
        assertEquals(false, InputValidation.validateIsEmpty(""));
        assertEquals(true, InputValidation.validateIsEmpty("aasdhjah"));
        assertEquals(true, InputValidation.validateIsEmpty("123abc"));
    }

    @Test
    public void removeSpecialChars(){
        String input;

        //běžné znaky
        input = "This $word !has \\/allot #of=\"% special% characters";
        assertEquals("This word has allot of special characters", InputValidation.removeSpecialChars(input));

        input = "Test ='Test !#$%\n";
        assertEquals("Test Test ", InputValidation.removeSpecialChars(input));

        input = "`#$~^&*{}°^\n\t\0";
        assertEquals("", InputValidation.removeSpecialChars(input));

        //čeština
        input = "áčďéěíňóřšťúůýž";
        assertEquals("áčďéěíňóřšťúůýž", InputValidation.removeSpecialChars(input));
        input = "ÁČĎÉĚÍŇÓŘŠŤÚŮÝŽ";
        assertEquals("ÁČĎÉĚÍŇÓŘŠŤÚŮÝŽ", InputValidation.removeSpecialChars(input));

        //email
        input = "vane1@seznam.cz";
        assertEquals("vane1@seznam.cz", InputValidation.removeSpecialChars(input));


    }

}