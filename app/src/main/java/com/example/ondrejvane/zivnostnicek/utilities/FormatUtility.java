package com.example.ondrejvane.zivnostnicek.utilities;

public class FormatUtility {

    /**
     * Metoda, která naformátuje částku do formátu +12 345.00,-
     *
     * @param input vstupní řetězec
     * @return výstupní naformátovaný řetězec
     */
    public static String formatIncomeAmount(String input) {
        String output;
        //přidání nul na konec, pokud neobsahuje tečku
        if (!input.contains(".")) {
            input = input + ".00";
        }

        //přidání mezery mezi tisíce
        int possition = input.indexOf('.');

        if (possition <= 3) {
            output = "+" + input + ",-";
            return output;
        } else if (possition <= 6) {
            output = input.substring(0, possition - 3) + " " + input.substring(possition - 3);
            output = "+" + output + ",-";
            return output;
        }
        return input;
    }

    /**
     * Metoda, která naformátuje částku do formátu -12 345.00,-
     *
     * @param input vstupní řetězec
     * @return výstupní naformátovaný řetězec
     */
    public static String formatExpenseAmount(String input) {
        String output;
        //přidání nul na konec, pokud neobsahuje tečku
        if (!input.contains(".")) {
            input = input + ".00";
        }

        //přidání mezery mezi tisíce
        int possition = input.indexOf('.');

        if (possition <= 3) {
            output = "-" + input + ",-";
            return output;
        } else if (possition <= 6) {
            output = input.substring(0, possition - 3) + " " + input.substring(possition - 3);
            output = "-" + output + ",-";
            return output;
        }
        return input;
    }

    public static String formatBalanceAmount(float input){
        String output;
        if(input > 0){
            output = formatIncomeAmount(Float.toString(input));
        }else if(input < 0){
            output = formatExpenseAmount(Float.toString(input * (-1)));
        }else {
            output = "0.0,-";
        }
        return  output;
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
}