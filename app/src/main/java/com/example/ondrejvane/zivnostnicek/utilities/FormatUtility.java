package com.example.ondrejvane.zivnostnicek.utilities;


import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtility {

    /**
     * Metoda, která naformátuje částku do formátu +12.345,00,-
     *
     * @param input vstupní hodnota
     * @return výstupní naformátovaný řetězec
     */
    public static String formatIncomeAmount(float input) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
        String currency = format.format(input);
        currency = currency.substring(1);
        currency = currency.replace(",", " ");  //mezera místo čárek
        currency = "+" + currency + ",-";
        return currency;
    }

    /**
     * Metoda, která naformátuje částku do formátu -12.345,00,-
     *
     * @param input vstupní hodnota
     * @return výstupní naformátovaný řetězec
     */
    public static String formatExpenseAmount(float input) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
        String currency = format.format(input);
        currency = currency.substring(1);
        currency = currency.replace(",", " ");  //mezera místo čárek
        currency = "-" + currency + ",-";
        return currency;
    }

    public static String formatBalanceAmount(float input) {
        String output;
        if (input > 0) {
            output = formatIncomeAmount(input);
        } else if (input < 0) {
            output = formatExpenseAmount(input * (-1));
        } else {
            output = "0.00,-";
        }
        return output;
    }

    /**
     * Metoda, která z řetězce datumu vrátí měsíc.
     *
     * @param date vstupní datum
     * @return měsíc
     */
    public static String getMonthFromDate(String date) {
        String output;
        output = date.substring(date.indexOf(".") + 1, date.indexOf(".", date.indexOf(".") + 1));
        return output;
    }

    /**
     * Metoda, která z řetězce datumu vrátí rok.
     *
     * @param date vstupní datum
     * @return rok
     */
    public static String getYearFromDate(String date) {
        String output;
        output = date.substring(date.length() - 4, date.length());
        return output;
    }

    public static boolean isRightDate(int year, int month, int foundYear, int foundMonth) {
        //nevybrán měsíc ani rok
        if (year == -1 && month == -1) {
            return true;
            //vybrán pouze rok
        } else if (year != -1 && month == -1) {

            if (year == foundYear) {
                return true;
            }

            //vybrán rok i měsíc
        } else if (year != -1 && month != -1) {

            if (year == foundYear && month == foundMonth) {
                return true;
            }

            //vybrán měsíc ale ne rok
        } else {
            return false;
        }

        return false;
    }

    public static String formatDateToShow(String date) {
        boolean star = false;
        date = date.replace("\n", "");

        if (date.charAt(0) == '*') {
            date = date.replace("*", "");
            star = true;
        }

        StringBuilder stringBuilder = new StringBuilder(date);



        //pokud den záčíná nulou tak jí odeberu
        if(date.charAt(0) == '0'){
            stringBuilder.deleteCharAt(0);
        }


        //pokud mesíc začíná nulou, tak ho odstraním
        if(date.charAt(3) == '0'){
            stringBuilder.deleteCharAt(2);
        }

        if(star){
            return "*" + stringBuilder.toString();
        }else {
            return stringBuilder.toString();
        }
    }
}
