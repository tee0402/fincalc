import java.io.*;
import java.math.*;
import java.util.*;

class CurrencyPairs {
  private final ArrayList<CurrencyPair> currencyPairs = new ArrayList<>();

  CurrencyPairs() {
    // Load currency pairs into ArrayList
    try {
      File file = new File("currencypairs.txt");
      if (file.exists() || file.createNewFile()) {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          Scanner lineScanner = new Scanner(line);
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
    return updateCurrencyPairsFile();
  }

  // Updates text file containing currency pairs
  private boolean updateCurrencyPairsFile() {
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
}