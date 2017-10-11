class CurrencyPair {
  private String currency1;
  private String currency2;
  private double conversionRate;

  CurrencyPair(String currency1, String currency2, double conversionRate) {
    this.currency1 = currency1;
    this.currency2 = currency2;
    this.conversionRate = conversionRate;
  }

  private String getCurrency1() {
    return currency1;
  }

  private String getCurrency2() {
    return currency2;
  }

  double getConversionRate() {
    return conversionRate;
  }

  boolean equals(CurrencyPair currencyPair) {
    return currencyPair.getCurrency1().equals(currency1) && currencyPair.getCurrency2().equals(currency2);
  }
}
