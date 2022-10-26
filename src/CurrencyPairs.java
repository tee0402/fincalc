import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

class CurrencyPairs {
  private final Map<String, Map<String, BigDecimal>> currencyPairs = new HashMap<>();

  CurrencyPairs() {
    try {
      File file = new File("currencypairs.txt");
      // Load currency pair information
      if (file.exists() || file.createNewFile()) {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
          Scanner lineScanner = new Scanner(scanner.nextLine());
          String currency1 = lineScanner.next();
          currencyPairs.putIfAbsent(currency1, new HashMap<>());
          currencyPairs.get(currency1).put(lineScanner.next(), new BigDecimal(lineScanner.next()));
        }
        scanner.close();
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  // Returns the exchange rate for the currency pair if found and null otherwise
  BigDecimal getExchangeRate(String currency1, String currency2) {
    // BFS
    Set<String> visited = new HashSet<>();
    Queue<Tuple> q = new ArrayDeque<>();
    visited.add(currency1);
    q.add(new Tuple(currency1, BigDecimal.ONE));
    while (!q.isEmpty()) {
      Tuple tuple = q.poll();
      String currency = tuple.currency;
      BigDecimal exchangeRate = tuple.exchangeRate;
      if (currencyPairs.containsKey(currency)) {
        Map<String, BigDecimal> pairs = currencyPairs.get(currency);
        if (pairs.containsKey(currency2)) {
          return exchangeRate.multiply(pairs.get(currency2));
        }
        for (String pairCurrency : pairs.keySet()) {
          if (!visited.contains(pairCurrency)) {
            visited.add(pairCurrency);
            q.add(new Tuple(pairCurrency, exchangeRate.multiply(pairs.get(pairCurrency))));
          }
        }
      }
    }
    return null;
  }

  private static class Tuple {
    private final String currency;
    private final BigDecimal exchangeRate;
    private Tuple(String currency, BigDecimal exchangeRate) {
      this.currency = currency;
      this.exchangeRate = exchangeRate;
    }
  }

  // Adds or updates a currency pair
  boolean putCurrencyPair(String currency1, String currency2, BigDecimal exchangeRate) {
    currencyPairs.putIfAbsent(currency1, new HashMap<>());
    currencyPairs.get(currency1).put(currency2, exchangeRate);
    currencyPairs.putIfAbsent(currency2, new HashMap<>());
    currencyPairs.get(currency2).put(currency1, BigDecimal.ONE.divide(exchangeRate, new MathContext(20, RoundingMode.HALF_EVEN)));
    return write();
  }

  // Updates text file containing currency pairs
  private boolean write() {
    try {
      FileWriter fileWriter = new FileWriter("currencypairs.txt");
      for (String currency1 : currencyPairs.keySet()) {
        for (String currency2 : currencyPairs.get(currency1).keySet()) {
          fileWriter.write(currency1 + " " + currency2 + " " + currencyPairs.get(currency1).get(currency2) + "\n");
        }
      }
      fileWriter.close();
      return true;
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  // Converts an amount from one currency to another, returns null if the currency pair does not exist
  BigDecimal convert(String currency1, String currency2, BigDecimal amount) {
    if (currency1.equals(currency2)) {
      return FinCalc.round(amount);
    }
    BigDecimal exchangeRate = getExchangeRate(currency1, currency2);
    return exchangeRate == null ? null : FinCalc.round(exchangeRate.multiply(amount));
  }
}