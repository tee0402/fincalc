import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Scanner;

class CurrencyPairs {
  private final ArrayList<CurrencyPair> currencyPairs = new ArrayList<>();

  CurrencyPairs() {
    try {
      File file = new File("currencypairs.txt");
      // Load currency pair information
      if (file.exists() || file.createNewFile()) {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
          Scanner lineScanner = new Scanner(scanner.nextLine());
          currencyPairs.add(new CurrencyPair(lineScanner.next(), lineScanner.next(), new BigDecimal(lineScanner.next())));
        }
        scanner.close();
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  // Returns the currency pair with the specified currencies if found and null otherwise
  private CurrencyPair getCurrencyPair(String currency1, String currency2) {
    for (CurrencyPair currencyPair : currencyPairs) {
      if (currencyPair.equals(currency1, currency2)) {
        return currencyPair;
      }
    }
    return null;
  }

  // Returns the currency pair with the specified currencies regardless of order if found and null otherwise
  CurrencyPair getCurrencyPairIgnoreOrder(String currency1, String currency2) {
    for (CurrencyPair currencyPair : currencyPairs) {
      if (currencyPair.equalsIgnoreOrder(currency1, currency2)) {
        return currencyPair;
      }
    }
    return null;
  }

  // Adds or updates a currency pair ignoring order
  boolean addOrUpdateCurrencyPairIgnoreOrder(String currency1, String currency2, BigDecimal conversionRate) {
    CurrencyPair currencyPair1 = getCurrencyPair(currency1, currency2);
    CurrencyPair currencyPair2 = getCurrencyPair(currency2, currency1);
    if (currencyPair1 != null) {
      currencyPair1.setConversionRate(conversionRate);
    } else {
      if (currencyPair2 != null) {
        currencyPairs.remove(currencyPair2);
      }
      currencyPairs.add(new CurrencyPair(currency1, currency2, conversionRate));
    }
    return write();
  }

  // Updates text file containing currency pairs
  private boolean write() {
    try {
      FileWriter fileWriter = new FileWriter("currencypairs.txt");
      for (CurrencyPair currencyPair : currencyPairs) {
        fileWriter.write(currencyPair.getCurrency1() + " " + currencyPair.getCurrency2() + " " + currencyPair.getConversionRateString() + "\n");
      }
      fileWriter.close();
      return true;
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  // Converts an amount from one currency to another, returns -1 if the currency pair does not exist
  BigDecimal convert(String currency1, String currency2, BigDecimal amount) {
    if (currency1.equals(currency2)) {
      return FinCalc.round(amount);
    }
    CurrencyPair currencyPair1 = getCurrencyPair(currency1, currency2);
    CurrencyPair currencyPair2 = getCurrencyPair(currency2, currency1);
    if (currencyPair1 != null) {
      return FinCalc.round(currencyPair1.getConversionRate().multiply(amount));
    } else if (currencyPair2 != null) {
      return FinCalc.round(BigDecimal.ONE.divide(currencyPair2.getConversionRate(), new MathContext(20, RoundingMode.HALF_EVEN)).multiply(amount));
    } else {
      return null;
    }
  }

  static class CurrencyPair {
    private final String currency1;
    private final String currency2;
    private BigDecimal conversionRate;

    private CurrencyPair(String currency1, String currency2, BigDecimal conversionRate) {
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

    private BigDecimal getConversionRate() {
      return conversionRate;
    }
    String getConversionRateString() {
      return conversionRate.toPlainString();
    }
    private void setConversionRate(BigDecimal conversionRate) {
      this.conversionRate = conversionRate;
    }

    private boolean equals(String currency1, String currency2) {
      return this.currency1.equals(currency1) && this.currency2.equals(currency2);
    }

    private boolean equalsIgnoreOrder(String currency1, String currency2) {
      return equals(currency1, currency2) || equals(currency2, currency1);
    }
  }
}