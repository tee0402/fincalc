import java.math.*;

class CurrencyPair {
  private final String currency1;
  private final String currency2;
  private final BigDecimal conversionRate;

  CurrencyPair(String currency1, String currency2, BigDecimal conversionRate) {
    this.currency1 = currency1;
    this.currency2 = currency2;
    this.conversionRate = conversionRate;
  }

  String getCurrency1() {
    return currency1;
  }

  String getCurrency2() {
    return currency2;
  }

  BigDecimal getConversionRate() {
    return conversionRate;
  }

  boolean equals(String currency1, String currency2) {
    return this.currency1.equals(currency1) && this.currency2.equals(currency2);
  }
}