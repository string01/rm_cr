package com.ocr.cash_register;

public interface TestConst {
    static String FORMAT_0 = "1 1 1 1 1";
    static String FORMAT_5 = "0 0 1 0 0";
    static String FORMAT_48 = "2 0 0 3 2";
    static String FORMAT_68 = "1 2 3 4 5";
    static String FORMAT_68_1 = "3 0 0 3 2";
    static String FORMAT_60 = "1 2 3 0 5";
    static String FORMAT_85 = "1 4 3 0 10";
    static String FORMAT_100 = "3 2 2 0 10";
    static Double EXPECTED_DOUBLE_0 = 38.0;
    static Double EXPECTED_DOUBLE_1 = 76.0;
    static Double EXPECTED_DOUBLE_2 = 0.0;
    static String OUTPUT_0 = "$0 0 0 0 0 0";
    static String OUTPUT_5 = "$5 0 0 1 0 0";
    static String OUTPUT_68 = "$68 1 2 3 4 5";
    static String OUTPUT_128 = "$128 2 4 6 4 10";
    static String OUTPUT_43 = "$43 1 0 3 4 0";
    static String OUTPUT_11 = "$11 0 0 1 3 0";
    static String OUTPUT_32 = "$32 1 0 2 1 0";
}
