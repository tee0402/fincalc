import java.math.*;
import java.util.*;
import java.io.*;
import java.security.*;

class FinCalc {
  private ArrayList<CurrencyPair> currencyPairs;
  private ArrayList<Account> accounts;
  private MathContext mathContext;

  // Initializes variables and calls the functions for loading data
  FinCalc() {
    currencyPairs = new ArrayList<>();
    accounts = new ArrayList<>();
    mathContext = new MathContext(2);
    loadCurrencyData();
    loadAccountData();
  }

  // Loads currency pairs into ArrayList
  private void loadCurrencyData() {
    try {
      File file = new File("currency.txt");
      if (file.exists()) {
        Scanner fileReader = new Scanner(file);
        while (fileReader.hasNextLine()) {
          String line = fileReader.nextLine();
          Scanner lineScanner = new Scanner(line);
          currencyPairs.add(new CurrencyPair(lineScanner.next(), lineScanner.next(), new BigDecimal(lineScanner.next())));
        }
        fileReader.close();
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  // Loads account information into ArrayList
  private void loadAccountData() {
    try {
      File file = new File("account.txt");
      if (file.exists()) {
        Scanner fileReader = new Scanner(file);
        while (fileReader.hasNextLine()) {
          String line = fileReader.nextLine();
          Scanner lineScanner = new Scanner(line);
          accounts.add(new Account(lineScanner.next(), lineScanner.next(), lineScanner.next(), lineScanner.next(), new BigDecimal(lineScanner.next())));
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
      byte[] hash = digest.digest(text.getBytes("UTF-8"));
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

  // Authenticates a username-password pair
  private boolean authenticate(String username, String password) {
    if (getAccount(username) >= 0) {
      String hash = hash(accounts.get(getAccount(username)).getSalt() + password);
      if (hash != null && hash.equals(accounts.get(getAccount(username)).getPassword())) {
        return true;
      }
    }
    return false;
  }

  // Runs the command line application
  void run() {
    boolean running = true;
    boolean authenticated = false;
    Scanner scanner = new Scanner(System.in);
    String loginUsername = "";

    // Login phase
    while (!authenticated) {
      System.out.print("Please enter your username: ");
      loginUsername = scanner.nextLine();
      System.out.print("Please enter your password: ");
      String loginPassword = scanner.nextLine();
      if (authenticate(loginUsername, loginPassword)) {
        authenticated = true;
      }
      else {
        System.out.println("Incorrect username or password. Please try again.");
      }
    }

    // Admin program
    if (loginUsername.equals("admin")) {
      System.out.println("FinCalc 2.0 - ADMIN");
      System.out.println("Type 'help' for a list of commands.");

      while (running) {
        System.out.print(">> ");

        String inputLine = scanner.nextLine();
        Scanner scannerInputLine = new Scanner(inputLine);
        String command = scannerInputLine.next();

        if (command.toUpperCase().equals("MAINT")) {
          maint(scanner, scannerInputLine);
        }
        else if (command.toUpperCase().equals("ADDUSER")) {
          addUser(scannerInputLine);
        }
        else if (command.toUpperCase().equals("DELUSER")) {
          delUser(scannerInputLine);
        }
        else if (command.toUpperCase().equals("ADD")) {
          add(scannerInputLine, loginUsername);
        }
        else if (command.toUpperCase().equals("SUB")) {
          sub(scannerInputLine, loginUsername);
        }
        else if (command.toLowerCase().equals("transfer")) {
          transfer(scannerInputLine, loginUsername);
        }
        else if (command.toLowerCase().equals("help")) {
          System.out.println("Use ISO Codes for currencies, e.g. USD, EUR, JPY");
          System.out.println("Conversion rate is the number of units of currency 2 that are equal to one unit of currency 1");
          System.out.println();
          System.out.println("LIST OF COMMANDS:");
          System.out.println("MAINT [currency 1] [currency 2] [conversion rate] - Enter currency conversion data");
          System.out.println("ADDUSER [username] [password] [preferred currency] - Add a new account with username <username> and password <password> and set the preferred currency");
          System.out.println("DELUSER [username] - Delete account with username <username>");
          System.out.println("ADD [currency] [amount] - Add <amount> in <currency> to account with username <username>");
          System.out.println("SUB [currency] [amount] - Remove <amount> in <currency> from account with username <username>");
          System.out.println("transfer [username] [currency] [amount] - Transfer <amount> in <currency> to account with username <username>");
          System.out.println("quit - Exit the program");
        }
        else if (command.toLowerCase().equals("quit")) {
          running = false;
        }
        else {
          System.out.println("Please enter a valid command. Type 'help' for a list of commands.");
        }
        scannerInputLine.close();
      }
    }
    // Normal user program
    else {
      System.out.println("FinCalc 2.0");
      System.out.println("Type 'help' for a list of commands.");

      while (running) {
        System.out.print(">> ");

        String inputLine = scanner.nextLine();
        Scanner scannerInputLine = new Scanner(inputLine);
        String command = scannerInputLine.next();

        if (command.toUpperCase().equals("MAINT")) {
          maint(scanner, scannerInputLine);
        }
        else if (command.toUpperCase().equals("ADD")) {
          add(scannerInputLine, loginUsername);
        }
        else if (command.toUpperCase().equals("SUB")) {
          sub(scannerInputLine, loginUsername);
        }
        else if (command.toLowerCase().equals("transfer")) {
          transfer(scannerInputLine, loginUsername);
        }
        else if (command.toLowerCase().equals("help")) {
          System.out.println("Use ISO Codes for currencies, e.g. USD, EUR, JPY");
          System.out.println("Conversion rate is the number of units of currency 2 that are equal to one unit of currency 1");
          System.out.println();
          System.out.println("LIST OF COMMANDS:");
          System.out.println("MAINT [currency 1] [currency 2] [conversion rate] - Enter currency conversion data");
          System.out.println("ADD [currency] [amount] - Add <amount> in <currency> to account with username <username>");
          System.out.println("SUB [currency] [amount] - Remove <amount> in <currency> from account with username <username>");
          System.out.println("transfer [username] [currency] [amount] - Transfer <amount> in <currency> to account with username <username>");
          System.out.println("quit - Exit the program");
        }
        else if (command.toLowerCase().equals("quit")) {
          running = false;
        }
        else {
          System.out.println("Please enter a valid command. Type 'help' for a list of commands.");
        }
        scannerInputLine.close();
      }
    }
    scanner.close();
    System.out.println("Goodbye.");
  }
  
  private void maint(Scanner scanner, Scanner scannerInputLine) {
    String currency1 = "";
    String currency2 = "";
    BigDecimal conversionRate = BigDecimal.ZERO;

    if (scannerInputLine.hasNext()) {
      currency1 = scannerInputLine.next().toUpperCase();
    }
    if (scannerInputLine.hasNext()) {
      currency2 = scannerInputLine.next().toUpperCase();
    }
    if (scannerInputLine.hasNextBigDecimal()) {
      conversionRate = scannerInputLine.nextBigDecimal();
    }
    else if (scannerInputLine.hasNext()) {
      String conversionRateInput = scannerInputLine.next().replaceAll("[^0-9.]", "");
      if (conversionRateInput.matches("^[0-9]+.?[0-9]*$")) {
        conversionRate = new BigDecimal(conversionRateInput);
      }
    }

    if (currency1.matches("^[A-Z]{3}$") && currency2.matches("^[A-Z]{3}$") && conversionRate.compareTo(BigDecimal.ZERO) > 0) {
      if (currency1.equals(currency2)) {
        System.out.println("Currency 1 and currency 2 must be different.");
      }
      else {
        boolean boolVerified = false;

        System.out.println(currency1 + "/" + currency2 + "=" + conversionRate);
        System.out.print("Would you like to save this currency conversion data in the database? (y/n): ");

        while (!boolVerified) {
          String command = scanner.nextLine();
          switch (command) {
            case "y":
              boolVerified = true;
              CurrencyPair currencyPair = new CurrencyPair(currency1, currency2, conversionRate);
              if (getCurrencyPair(currencyPair) == -1) {
                saveCurrencyData(currency1, currency2, conversionRate);
                currencyPairs.add(currencyPair);
                System.out.println("Currency pair saved.");
              } else {
                System.out.println("Saving failed. Currency pair already exists.");
              }
              break;
            case "n":
              boolVerified = true;
              System.out.println("Saving canceled.");
              break;
            default:
              System.out.print("Please enter 'y' for yes or 'n' for no: ");
          }
        }
      }
    }
    else {
      System.out.println("Please enter valid arguments for MAINT:");
      System.out.println("MAINT [currency 1] [currency 2] [conversion rate]");
    }
  }

  private void addUser(Scanner scannerInputLine) {
    String username = "";
    String password = "";
    String preferredCurrency = "";

    if (scannerInputLine.hasNext()) {
      username = scannerInputLine.next();
    }
    if (scannerInputLine.hasNext()) {
      password = scannerInputLine.next();
    }
    if (scannerInputLine.hasNext()) {
      preferredCurrency = scannerInputLine.next().toUpperCase();
    }

    if (username.matches("^[a-zA-Z]+[a-zA-Z0-9]*$") && password.matches("^\\S+$") && preferredCurrency.matches("^[A-Z]{3}$")) {
      if (saveNewAccount(username, password, preferredCurrency)) {
        System.out.println("Account added successfully.");
      }
      else {
        System.out.println("Account already exists. Please choose another username.");
      }
    }
    else {
      System.out.println("Please enter valid arguments for ADDUSER:");
      System.out.println("ADDUSER [username] [password] [preferred currency]");
    }
  }

  private void delUser(Scanner scannerInputLine) {
    String username = "";

    if (scannerInputLine.hasNext()) {
      username = scannerInputLine.next();
    }

    if (username.matches("^[a-zA-Z]+[a-zA-Z0-9]*$") && !username.equals("admin")) {
      if (deleteAccount(username)) {
        System.out.println("Account deleted successfully.");
      }
      else {
        System.out.println("Account does not exist.");
      }
    }
    else {
      System.out.println("Please enter valid arguments for DELUSER:");
      System.out.println("DELUSER [username]");
    }
  }
  
  private void add(Scanner scannerInputLine, String loginUsername) {
    String currency = "";
    BigDecimal amount = BigDecimal.ZERO;

    if (scannerInputLine.hasNext()) {
      currency = scannerInputLine.next().toUpperCase();
    }
    if (scannerInputLine.hasNextBigDecimal()) {
      amount = scannerInputLine.nextBigDecimal();
    }
    else if (scannerInputLine.hasNext()) {
      String amountInput = scannerInputLine.next().replaceAll("[^0-9.]", "");
      if (amountInput.matches("^[0-9]+.?[0-9]*$")) {
        amount = new BigDecimal(amountInput);
      }
    }

    if (currency.matches("^[A-Z]{3}$") && amount.compareTo(BigDecimal.ZERO) > 0) {
      String preferredCurrency = accounts.get(getAccount(loginUsername)).getPreferredCurrency();
      BigDecimal convertedAmount = convert(currency, preferredCurrency, amount);
      if (convertedAmount.compareTo(BigDecimal.ZERO) > 0) {
        changeBalance(loginUsername, convertedAmount, true);
        System.out.println("Added " + convertedAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN) + " " + preferredCurrency + " to your account. Your new balance is " + accounts.get(getAccount(loginUsername)).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN) + " " + preferredCurrency + ".");
      }
      else {
        System.out.println("No conversion data to " + preferredCurrency + ". Please enter conversion data first.");
      }
    }
    else {
      System.out.println("Please enter valid arguments for ADD:");
      System.out.println("ADD [currency] [amount]");
    }
  }

  private void sub(Scanner scannerInputLine, String loginUsername) {
    String currency = "";
    BigDecimal amount = BigDecimal.ZERO;

    if (scannerInputLine.hasNext()) {
      currency = scannerInputLine.next().toUpperCase();
    }
    if (scannerInputLine.hasNextBigDecimal()) {
      amount = scannerInputLine.nextBigDecimal();
    }
    else if (scannerInputLine.hasNext()) {
      String amountInput = scannerInputLine.next().replaceAll("[^0-9.]", "");
      if (amountInput.matches("^[0-9]+.?[0-9]*$")) {
        amount = new BigDecimal(amountInput);
      }
    }

    if (currency.matches("^[A-Z]{3}$") && amount.compareTo(BigDecimal.ZERO) > 0) {
      String preferredCurrency = accounts.get(getAccount(loginUsername)).getPreferredCurrency();
      BigDecimal convertedAmount = convert(currency, preferredCurrency, amount);
      if (convertedAmount.compareTo(BigDecimal.ZERO) > 0) {
        if (changeBalance(loginUsername, convertedAmount, false)) {
          System.out.println("Removed " + convertedAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN) + " " + preferredCurrency + " from your account. Your new balance is " + accounts.get(getAccount(loginUsername)).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN) + " " + preferredCurrency + ".");
        }
        else {
          System.out.println("Insufficient funds.");
        }
      }
      else {
        System.out.println("No conversion data to " + preferredCurrency + ". Please enter conversion data first.");
      }
    }
    else {
      System.out.println("Please enter valid arguments for SUB:");
      System.out.println("SUB [currency] [amount]");
    }
  }

  private void transfer(Scanner scannerInputLine, String loginUsername) {
    String username = "";
    String currency = "";
    BigDecimal amount = BigDecimal.ZERO;

    if (scannerInputLine.hasNext()) {
      username = scannerInputLine.next();
    }
    if (scannerInputLine.hasNext()) {
      currency = scannerInputLine.next().toUpperCase();
    }
    if (scannerInputLine.hasNextBigDecimal()) {
      amount = scannerInputLine.nextBigDecimal();
    }
    else if (scannerInputLine.hasNext()) {
      String amountInput = scannerInputLine.next().replaceAll("[^0-9.]", "");
      if (amountInput.matches("^[0-9]+.?[0-9]*$")) {
        amount = new BigDecimal(amountInput);
      }
    }

    if (username.matches("^[a-zA-Z]+[a-zA-Z0-9]*$") && !username.equals(loginUsername) && currency.matches("^[A-Z]{3}$") && amount.compareTo(BigDecimal.ZERO) > 0) {
      if (getAccount(username) >= 0) {
        String sourcePreferredCurrency = accounts.get(getAccount(loginUsername)).getPreferredCurrency();
        String destinationPreferredCurrency = accounts.get(getAccount(username)).getPreferredCurrency();
        BigDecimal convertedSourceAmount = convert(currency, sourcePreferredCurrency, amount);
        BigDecimal convertedDestinationAmount = convert(currency, destinationPreferredCurrency, amount);
        if (convertedSourceAmount.compareTo(BigDecimal.ZERO) <= 0) {
          System.out.println("No conversion data to " + sourcePreferredCurrency + ". Please enter conversion data first.");
        }
        else if (convertedDestinationAmount.compareTo(BigDecimal.ZERO) <= 0) {
          System.out.println("No conversion data to " + destinationPreferredCurrency + ". Please enter conversion data first.");
        }
        else {
          if (changeBalance(loginUsername, convertedSourceAmount, false)) {
            changeBalance(username, convertedDestinationAmount, true);
            System.out.println("Transferred " + convertedSourceAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN) + " " + sourcePreferredCurrency + "/" + convertedDestinationAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN) + " " + destinationPreferredCurrency + " from your account to the account of " + username + ". Your new balance is " + accounts.get(getAccount(loginUsername)).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN) + " " + sourcePreferredCurrency + ". Their new balance is " + accounts.get(getAccount(username)).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN) + " " + destinationPreferredCurrency + ".");
          }
          else {
            System.out.println("Insufficient funds.");
          }
        }
      }
      else {
        System.out.println("Account does not exist.");
      }
    }
    else {
      System.out.println("Please enter valid arguments for transfer:");
      System.out.println("transfer [username] [currency] [amount]");
    }
  }

  // Returns the index of the specified currency pair in the ArrayList, returns -1 if not found
  private int getCurrencyPair(CurrencyPair currencyPair) {
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
  private int getAccount(String username) {
    for (int i = 0; i < accounts.size(); i++) {
      if (accounts.get(i).getUsername().equals(username)) {
        return i;
      }
    }
    return -1;
  }

  // Adds a new account with balance 0 to the text file and ArrayList
  private boolean saveNewAccount(String username, String password, String preferredCurrency) {
    try {
      if (getAccount(username) >=  0) {
        return false;
      }
      else {
        FileWriter fileWriter = new FileWriter(new File("account.txt"), true);
        String salt = salt();
        String hash = hash(salt + password);
        fileWriter.write(username + " " + salt + " " + hash + " " + preferredCurrency + " " + 0 + "\n");
        fileWriter.close();
        accounts.add(new Account(username, salt, hash, preferredCurrency, BigDecimal.ZERO));
        return true;
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  // Deletes an account from the text file and ArrayList
  private boolean deleteAccount(String username) {
    try {
      if (getAccount(username) >= 0) {
        File oldAccount = new File("account.txt");
        File newAccount = new File("temp");
        Scanner scanner = new Scanner(oldAccount);
        FileWriter fileWriter = new FileWriter(newAccount);
        while (scanner.hasNextLine()){
          String line = scanner.nextLine();
          String[] lineItems = line.split(" ");
          if (!lineItems[0].equals(username)) {
            fileWriter.write(line + "\n");
          }
        }
        fileWriter.close();
        scanner.close();
        oldAccount.delete();
        newAccount.renameTo(new File("account.txt"));
        accounts.remove(getAccount(username));
        return true;
      }
      else {
        return false;
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  // Changes the balance of the account with the specified username, returns true or false depending on whether the change succeeded
  private boolean changeBalance(String username, BigDecimal amount, boolean deposit) {
    int accountIndex = getAccount(username);
    if (deposit) {
      BigDecimal newBalance = accounts.get(accountIndex).getBalance().add(amount);
      accounts.get(accountIndex).setBalance(newBalance);
      updateAccountBalance(username, newBalance);
      return true;
    }
    else {
      BigDecimal newBalance = accounts.get(accountIndex).getBalance().subtract(amount);
      if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
        accounts.get(accountIndex).setBalance(newBalance);
        updateAccountBalance(username, newBalance);
        return true;
      }
      else {
        return false;
      }
    }
  }

  // Updates the account balance of an existing account in the text file
  // Does this by creating a temporary file, copying all lines except the one with the specified username, changing the balance for that line, deleting the old file and renaming the new file to the old file
  private void updateAccountBalance(String username, BigDecimal balance) {
    try {
      File oldAccount = new File("account.txt");
      File newAccount = new File("temp");
      Scanner scanner = new Scanner(oldAccount);
      FileWriter fileWriter = new FileWriter(newAccount);
      while (scanner.hasNextLine()){
        String line = scanner.nextLine();
        String[] lineItems = line.split(" ");
        if (lineItems[0].equals(username)) {
          fileWriter.write(username + " " + lineItems[1] + " " + lineItems[2] + " " + lineItems[3] + " " + balance + "\n");
        }
        else {
          fileWriter.write(line + "\n");
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
    if (currency1.equals(currency2)) {
      return amount;
    }
    CurrencyPair currencyPair1 = new CurrencyPair(currency1, currency2, BigDecimal.ONE);
    CurrencyPair currencyPair2 = new CurrencyPair(currency2, currency1, BigDecimal.ONE);
    int currencyIndex1 = getCurrencyPair(currencyPair1);
    int currencyIndex2 = getCurrencyPair(currencyPair2);
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
