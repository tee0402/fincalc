import java.math.*;
import java.util.*;
import java.io.*;
import java.security.*;
import java.nio.charset.StandardCharsets;

class FinCalc {
  private ArrayList<CurrencyPair> currencyPairs;
  private ArrayList<Account> accounts;
  private MathContext mathContext;
  private boolean admin;

  // Initializes variables and calls the functions for loading data
  FinCalc() {
    currencyPairs = new ArrayList<>();
    accounts = new ArrayList<>();
    mathContext = new MathContext(2);
    admin = false;
    loadCurrencyData();
    loadAccountData();
  }

  // Loads currency pairs into ArrayList
  private void loadCurrencyData() {
    try {
      File file = new File("currency.txt");
      if (file.exists()){
        Scanner fileReader = new Scanner(file);
        while (fileReader.hasNextLine()) {
          String line = fileReader.nextLine();
          Scanner lineScanner = new Scanner(line);
          CurrencyPair currencyPair = new CurrencyPair(lineScanner.next(), lineScanner.next(), new BigDecimal(lineScanner.next()));
          currencyPairs.add(currencyPair);
        }
        fileReader.close();
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  // Loads usernames and balances into ArrayList
  private void loadAccountData() {
    try {
      File file = new File("account.txt");
      if (file.exists()){
        Scanner fileReader = new Scanner(file);
        while (fileReader.hasNextLine()) {
          String line = fileReader.nextLine();
          Scanner lineScanner = new Scanner(line);
          Account account = new Account(lineScanner.next(), new BigDecimal(lineScanner.next()));
          accounts.add(account);
        }
        fileReader.close();
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  // Converts a byte array to a hexadecimal string
  private String bytesToHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for(byte b : bytes) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }

  // Hashes a string using SHA-256 and returns the hash in hexadecimal form
  private String hash(String text) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
      return bytesToHex(hash);
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  // Returns a 256-bit salt
  private String salt() {
    byte[] salt = new byte[32];
    SecureRandom random = new SecureRandom();
    random.nextBytes(salt);
    return bytesToHex(salt);
  }

  // Runs the command line application
  void run() {
    boolean running = true;
    Scanner scanner = new Scanner(System.in);

    System.out.println("FinCalc 2.0");
    System.out.println("Type 'help' for a list of commands.");

    while (running) {
      System.out.print(">> ");

      String commandLine = scanner.nextLine();
      Scanner scannerCommandLine = new Scanner(commandLine);
      String command = scannerCommandLine.next();

      if (command.toUpperCase().equals("MAINT")) {
        String currency1 = "";
        String currency2 = "";
        BigDecimal conversionRate = BigDecimal.ZERO;

        if (scannerCommandLine.hasNext()) {
          currency1 = scannerCommandLine.next().toUpperCase();
        }
        if (scannerCommandLine.hasNext()) {
          currency2 = scannerCommandLine.next().toUpperCase();
        }
        if (scannerCommandLine.hasNextBigDecimal()) {
          conversionRate = scannerCommandLine.nextBigDecimal();
        }

        if (currency1.matches("^[a-zA-Z]{3}$") && currency2.matches("^[a-zA-Z]{3}$") && conversionRate.compareTo(BigDecimal.ZERO) > 0) {
          boolean boolVerified = false;

          System.out.println(currency1 + "/" + currency2 + "=" + conversionRate);
          System.out.print("Would you like to save this currency conversion data in the database? (y/n): ");

          while (!boolVerified) {
            command = scanner.nextLine();
            switch (command) {
              case "y":
                boolVerified = true;
                CurrencyPair currencyPair = new CurrencyPair(currency1, currency2, conversionRate);
                if (getCurrencyPair(currencyPair, currencyPairs) == -1) {
                  saveCurrencyData(currency1, currency2, conversionRate);
                  currencyPairs.add(currencyPair);
                  System.out.println("Currency pair saved.");
                }
                else {
                  System.out.println("Saving failed. Currency pair already exists.");
                }
                break;
              case "n":
                boolVerified = true;
                System.out.println("Saving canceled.");
                break;
              default:
                System.out.println("Please enter 'y' for yes or 'n' for no.");
            }
          }
        }
        else {
          System.out.println("Please enter valid arguments for MAINT:");
          System.out.println("MAINT [currency 1] [currency 2] [conversion rate]");
        }
      }
      else if (command.toLowerCase().equals("deposit")) {
        String username = "";
        String currency = "";
        BigDecimal amount = BigDecimal.ZERO;

        if (scannerCommandLine.hasNext()) {
          username = scannerCommandLine.next();
        }
        if (scannerCommandLine.hasNext()) {
          currency = scannerCommandLine.next().toUpperCase();
        }
        if (scannerCommandLine.hasNextBigDecimal()) {
          amount = scannerCommandLine.nextBigDecimal();
        }
        else if (scannerCommandLine.hasNext()) {
          amount = new BigDecimal(scannerCommandLine.next().replaceAll("[^0-9.]", ""));
        }

        if (username.matches("^[a-zA-Z]+[a-zA-Z0-9]*$") && currency.matches("^[a-zA-Z]{3}$") && amount.compareTo(BigDecimal.ZERO) > 0) {
          if (currency.equals("USD")) {
            changeBalance(username, amount, true);
            System.out.println("Deposited " + amount.setScale(2, BigDecimal.ROUND_HALF_EVEN) + " USD into the account of " + username + ". Your new balance is " + accounts.get(getAccount(username, accounts)).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN) + " USD.");
          }
          else {
            BigDecimal convertedAmount = convert(currency, "USD", amount);
            if (convertedAmount.compareTo(BigDecimal.ZERO) > 0) {
              changeBalance(username, convertedAmount, true);
              System.out.println("Deposited " + convertedAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN) + " USD into the account of " + username + ". Your new balance is " + accounts.get(getAccount(username, accounts)).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN) + " USD.");
            }
            else {
              System.out.println("No conversion data to USD. Please enter conversion data first.");
            }
          }
        }
        else {
          System.out.println("Please enter valid arguments for deposit:");
          System.out.println("deposit [username] [currency] [amount]");
        }
      }
      else if (command.toLowerCase().equals("withdraw")) {
        String username = "";
        String currency = "";
        BigDecimal amount = BigDecimal.ZERO;

        if (scannerCommandLine.hasNext()) {
          username = scannerCommandLine.next();
        }
        if (scannerCommandLine.hasNext()) {
          currency = scannerCommandLine.next().toUpperCase();
        }
        if (scannerCommandLine.hasNextBigDecimal()) {
          amount = scannerCommandLine.nextBigDecimal();
        }
        else if (scannerCommandLine.hasNext()) {
          amount = new BigDecimal(scannerCommandLine.next().replaceAll("[^0-9.]", ""));
        }

        if (username.matches("^[a-zA-Z]+[a-zA-Z0-9]*$") && currency.matches("^[a-zA-Z]{3}$") && amount.compareTo(BigDecimal.ZERO) > 0) {
          if (currency.equals("USD")) {
            if (changeBalance(username, amount, false)) {
              System.out.println("Withdrew " + amount.setScale(2, BigDecimal.ROUND_HALF_EVEN) + " USD from the account of " + username + ". Your new balance is " + accounts.get(getAccount(username, accounts)).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN) + " USD.");
            }
            else {
              System.out.println("Insufficient funds.");
            }
          }
          else {
            BigDecimal convertedAmount = convert(currency, "USD", amount);
            if (convertedAmount.compareTo(BigDecimal.ZERO) > 0) {
              if (changeBalance(username, convertedAmount, false)) {
                System.out.println("Withdrew " + convertedAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN) + " USD from the account of " + username + ". Your new balance is " + accounts.get(getAccount(username, accounts)).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN) + " USD.");
              }
              else {
                System.out.println("Insufficient funds.");
              }
            }
            else {
              System.out.println("No conversion data to USD. Please enter conversion data first.");
            }
          }
        }
        else {
          System.out.println("Please enter valid arguments for withdraw:");
          System.out.println("withdraw [username] [currency] [amount]");
        }
      }
      else if (command.toLowerCase().equals("transfer")) {
        String username = "";
        String currency = "";
        BigDecimal amount = BigDecimal.ZERO;

        if (scannerCommandLine.hasNext()) {
          username = scannerCommandLine.next();
        }
        if (scannerCommandLine.hasNext()) {
          currency = scannerCommandLine.next().toUpperCase();
        }
        if (scannerCommandLine.hasNextBigDecimal()) {
          amount = scannerCommandLine.nextBigDecimal();
        }
        else if (scannerCommandLine.hasNext()) {
          amount = new BigDecimal(scannerCommandLine.next().replaceAll("[^0-9.]", ""));
        }

        if (username.matches("^[a-zA-Z]+[a-zA-Z0-9]*$") && currency.matches("^[a-zA-Z]{3}$") && amount.compareTo(BigDecimal.ZERO) > 0) {

        }
        else {
          System.out.println("Please enter valid arguments for transfer:");
          System.out.println("transfer [username] [currency] [amount]");
        }
      }
      else if (command.toLowerCase().equals("help")) {
        System.out.println("Use ISO Codes for currencies, e.g. USD, EUR, JPY");
        System.out.println("Conversion rate is the number of units of currency 2 that are equal to one unit of currency 1");
        System.out.println();
        System.out.println("LIST OF COMMANDS:");
        System.out.println("MAINT [currency 1] [currency 2] [conversion rate] - Enter currency conversion data");
        System.out.println("deposit [username] [currency] [amount] - Deposit <amount> in <currency> into account with username <username>");
        System.out.println("withdraw [username] [currency] [amount] - Withdraw <amount> in <currency> from account with username <username>");
        System.out.println("transfer [username] [currency] [amount] - Transfer <amount> in <currency> to account with username <username>");
        System.out.println("quit - Exit the program");
      }
      else if (command.toLowerCase().equals("quit")) {
        running = false;
      }
      else {
        System.out.println("Please enter a valid command. Type 'help' for a list of commands.");
      }
      scannerCommandLine.close();
    }

    scanner.close();
    System.out.println("Goodbye.");
  }

  // Returns the index of the specified currency pair in the ArrayList, returns -1 if not found
  private int getCurrencyPair(CurrencyPair currencyPair, ArrayList<CurrencyPair> currencyPairs) {
    for (int i = 0; i < currencyPairs.size(); i++) {
      if (currencyPairs.get(i).equals(currencyPair)) {
        return i;
      }
    }
    return -1;
  }

  // Writes currency conversion data to a text file
  private void saveCurrencyData(String currency1, String currency2, BigDecimal conversionRate) {
    try {
      FileWriter fileWriter = new FileWriter(new File("currency.txt"), true);
      fileWriter.write(currency1 + " " + currency2 + " " + conversionRate + "\n");
      fileWriter.close();
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  // Returns the index of the account with the specified username in the ArrayList, returns -1 if not found
  private int getAccount(String username, ArrayList<Account> accounts) {
    for (int i = 0; i < accounts.size(); i++) {
      if (accounts.get(i).getUsername().equals(username)) {
        return i;
      }
    }
    return -1;
  }

  // Changes the balance of the account with the specified username, returns true or false depending on whether the change succeeded
  private boolean changeBalance(String username, BigDecimal amount, boolean deposit) {
    int accountIndex = getAccount(username, accounts);
    if (accountIndex == -1) {
      if (deposit) {
        Account account = new Account(username, amount);
        accounts.add(account);
        saveNewAccountData(username, amount);
        return true;
      }
      else {
        return false;
      }
    }
    else {
      if (deposit) {
        BigDecimal newBalance = accounts.get(accountIndex).getBalance().add(amount);
        accounts.get(accountIndex).setBalance(newBalance);
        updateAccountData(username, newBalance);
        return true;
      }
      else {
        BigDecimal newBalance = accounts.get(accountIndex).getBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
          accounts.get(accountIndex).setBalance(newBalance);
          updateAccountData(username, newBalance);
          return true;
        }
        else {
          return false;
        }
      }
    }
  }

  // Adds a new account to the text file
  private void saveNewAccountData(String username, BigDecimal balance) {
    try {
      FileWriter fileWriter = new FileWriter(new File("account.txt"), true);
      fileWriter.write(username + " " + balance + "\n");
      fileWriter.close();
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  // Updates the account balance of an existing account in the text file
  // Does this by creating a temporary file, copying all lines except the one with the specified username, changing the balance for that line, deleting the old file and renaming the new file to the old file
  private void updateAccountData(String username, BigDecimal balance) {
    try {
      File oldAccount = new File("account.txt");
      File newAccount = new File("temp");
      Scanner scanner = new Scanner(oldAccount);
      FileWriter fileWriter = new FileWriter(newAccount);
      while (scanner.hasNextLine()){
        String line = scanner.nextLine() + "\n";
        String scannedUsername = new Scanner(line).next();
        if (scannedUsername.equals(username)) {
          fileWriter.write(username + " " + balance + "\n");
        }
        else {
          fileWriter.write(line);
        }
      }
      fileWriter.close();
      scanner.close();
      oldAccount.delete();
      newAccount.renameTo(new File("account.txt"));
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  // Converts an amount from one currency to another, returns -1 if the currency pair does not exist
  private BigDecimal convert(String currency1, String currency2, BigDecimal amount) {
    CurrencyPair currencyPair1 = new CurrencyPair(currency1, currency2, BigDecimal.ONE);
    CurrencyPair currencyPair2 = new CurrencyPair(currency2, currency1, BigDecimal.ONE);
    int currencyIndex1 = getCurrencyPair(currencyPair1, currencyPairs);
    int currencyIndex2 = getCurrencyPair(currencyPair2, currencyPairs);
    if (currencyIndex1 >= 0) {
      return currencyPairs.get(currencyIndex1).getConversionRate().multiply(amount);
    }
    else if (currencyIndex2 >= 0) {
      return BigDecimal.ONE.divide(currencyPairs.get(currencyIndex2).getConversionRate(), mathContext).multiply(amount);
    }
    else {
      return BigDecimal.valueOf(-1);
    }
  }
}
