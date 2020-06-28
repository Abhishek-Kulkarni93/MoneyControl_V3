package com.example.bitcashier.models;

import java.util.Arrays;
import java.util.List;

public class Currency {

    public static final List<String> currencyNames = Arrays.asList("EUR (€) - European Euro", "USD ($) - United States Dollars", "GBP (£) - Great Britain Pound", "INR (₹) - Indian Rupees", "YEN (¥) - Japanese Yen", "SGD ($) - Singapore Dollars", "AUD ($) - Australian Dollars", "CHF (₣) - Swiss Franc", "CAD ($) - Canadian Dollars");
    private static final List<String> currencyCodes = Arrays.asList("EUR","USD","GBP","INR","YEN","SGD","AUD","CHF","CAD");
    private static final List<String> currencySymbols = Arrays.asList("€","$","£","₹","¥","$","$","₣","$");

    // Currency conversion rates taken as of 27/06/2020 from https://www.xe.com/currencyconverter
    private static final List<Double> convertToEuroCurrencyRates = Arrays.asList(1.00,0.891411,1.09977,0.0117861,0.00831429,0.639764,0.611658,0.940116,0.651009);

    public static String getCurrencyCode(String currencyName) {
        return currencyCodes.get(currencyNames.indexOf(currencyName));
    }

    public static String getCurrencySymbol(String currencyCode) {
        return currencySymbols.get(currencyCodes.indexOf(currencyCode));
    }

    public static String getCurrencySpinnerValue(String currencyCode) {
        return currencyNames.get(currencyCodes.indexOf(currencyCode));
    }

    private String code;
    private double amount;
    private double euroAmount;
    private double otherAmount;

    public Currency() {
        // Empty constructor
    }

    public Currency(double amount, String code) {
        this.amount = amount;
        this.code = code;

        this.euroAmount = this.convertToEuro();
        this.otherAmount = this.convertToOther();
    }

    public double getEuroAmount() {
        return euroAmount;
    }

    public double getOtherAmount() {
        return otherAmount;
    }

    public double convertToEuro() {
        return convertToEuroCurrencyRates.get(currencyCodes.indexOf(this.code)) * this.amount;
    }

    public double convertToOther() {
        return this.amount / convertToEuroCurrencyRates.get(currencyCodes.indexOf(this.code));
    }
}
