import java.math.*;
import java.util.*;

class FinCalc {
  private final Accounts accounts = new Accounts();
  private final CurrencyPairs currencyPairs = new CurrencyPairs();
  private final Scanner scanner = new Scanner(System.in);
  private final String loginUsername;
  private Scanner lineScanner;

  FinCalc() {
    loginUsername = authenticate();
    if (loginUsername.equals("admin")) {
      admin();
    } else {
      user();
    }
    scanner.close();
    System.out.println("Goodbye.");
  }

  // Login phase
  private String authenticate() {
    while (true) {
      System.out.print("Please enter your username: ");
      String username = scanner.nextLine();
      System.out.print("Please enter your password: ");
      String password = scanner.nextLine();
      if (validateUsernameInput(username) && validatePasswordInput(password) && accounts.authenticate(username, password)) {
        return username;
      } else {
        System.out.println("Incorrect username or password. Please try again.");
      }
    }
  }

  // Admin program
  private void admin() {
    System.out.println("FinCalc 2.0 - ADMIN");
    System.out.println("Type 'help' for a list of commands.");

    while (true) {
      System.out.print(">> ");

      lineScanner = new Scanner(scanner.nextLine());
      String command = lineScanner.next();

      if (command.equalsIgnoreCase("MAINT")) {
        maint();
      } else if (command.equalsIgnoreCase("ADDUSER")) {
        addUser();
      } else if (command.equalsIgnoreCase("DELUSER")) {
        delUser();
      } else if (command.equalsIgnoreCase("ADD")) {
        changeBalance(true, true);
      } else if (command.equalsIgnoreCase("SUB")) {
        changeBalance(false, true);
      } else if (command.equalsIgnoreCase("TRANSFER")) {
        transfer(true);
      } else if (command.equalsIgnoreCase("help")) {
        System.out.println("Use ISO Codes for currencies, e.g. USD, EUR, JPY");
        System.out.println("Conversion rate is the number of units of currency 2 that are equal to one unit of currency 1");
        System.out.println();
        System.out.println("LIST OF COMMANDS:");
        System.out.println("MAINT [currency 1] [currency 2] [conversion rate] - Enter currency conversion data");
        System.out.println("ADDUSER [username] [password] [preferred currency] - Add a new account with <username> and <password> and set the preferred currency");
        System.out.println("DELUSER [username] - Delete account with <username>");
        System.out.println("ADD [username] [currency] [amount] - Add <amount> in <currency> to account with <username>");
        System.out.println("SUB [username] [currency] [amount] - Remove <amount> in <currency> from account with <username>");
        System.out.println("TRANSFER [username 1] [username 2] [currency] [amount] - Transfer <amount> in <currency> from account with <username 1> to account with <username 2>");
        System.out.println("quit - Exit the program");
      } else if (command.equalsIgnoreCase("quit")) {
        break;
      } else {
        System.out.println("Please enter a valid command. Type 'help' for a list of commands.");
      }
      lineScanner.close();
    }
  }

  // Normal user program
  private void user() {
    System.out.println("FinCalc 2.0");
    System.out.println("Type 'help' for a list of commands.");

    while (true) {
      System.out.print(">> ");

      lineScanner = new Scanner(scanner.nextLine());
      String command = lineScanner.next();

      if (command.equalsIgnoreCase("ADD")) {
        changeBalance(true, false);
      } else if (command.equalsIgnoreCase("SUB")) {
        changeBalance(false, false);
      } else if (command.equalsIgnoreCase("TRANSFER")) {
        transfer(false);
      } else if (command.equalsIgnoreCase("help")) {
        System.out.println("Use ISO Codes for currencies, e.g. USD, EUR, JPY");
        System.out.println();
        System.out.println("LIST OF COMMANDS:");
        System.out.println("ADD [currency] [amount] - Add <amount> in <currency> to your account");
        System.out.println("SUB [currency] [amount] - Remove <amount> in <currency> from your account");
        System.out.println("TRANSFER [username] [currency] [amount] - Transfer <amount> in <currency> from your account to account with <username>");
        System.out.println("quit - Exit the program");
      } else if (command.equalsIgnoreCase("quit")) {
        break;
      } else {
        System.out.println("Please enter a valid command. Type 'help' for a list of commands.");
      }
      lineScanner.close();
    }
  }

  private void maint() {
    String currency1 = readStringToUpperCase();
    String currency2 = readStringToUpperCase();
    BigDecimal conversionRate = readBigDecimalStripTrailingZeros();
    boolean currency1Validated = validateCurrencyInput(currency1);
    boolean currency2Validated = validateCurrencyInput(currency2);

    if (!currency1.equals(currency2) && currency1Validated && currency2Validated && isPositive(conversionRate)) {
      System.out.println(currency1 + "/" + currency2 + "=" + conversionRate.toPlainString());

      CurrencyPair currencyPair = currencyPairs.getCurrencyPairIgnoreOrder(currency1, currency2);
      if (currencyPair != null) {
        System.out.print("Currency pair " + currencyPair.getCurrency1() + "/" + currencyPair.getCurrency2() + "=" + currencyPair.getConversionRate().toPlainString() + " already exists. Overwrite? (y/n): ");
      } else {
        System.out.print("Would you like to save this currency conversion data in the database? (y/n): ");
      }

      while (true) {
        String command = scanner.nextLine();
        if (command.equalsIgnoreCase("y")) {
          if (currencyPairs.addOrUpdateCurrencyPairIgnoreOrder(currency1, currency2, conversionRate)) {
            System.out.println("Currency pair saved.");
          } else {
            System.out.println("Saving failed.");
          }
          break;
        } else if (command.equalsIgnoreCase("n")) {
          System.out.println("Saving canceled.");
          break;
        } else {
          System.out.print("Please enter 'y' for yes or 'n' for no: ");
        }
      }
    } else {
      System.out.println("Please enter valid arguments for MAINT:");
      System.out.println("MAINT [currency 1] [currency 2] [conversion rate]");
    }
  }

  private void addUser() {
    String username = readString();
    String password = readString();
    String preferredCurrency = readStringToUpperCase();

    if (validateUsernameInput(username) && validatePasswordInput(password) && validateCurrencyInput(preferredCurrency)) {
      if (accounts.addAccount(username, password, preferredCurrency)) {
        System.out.println("Account added successfully.");
      } else {
        System.out.println("Account already exists. Please choose another username.");
      }
    } else {
      System.out.println("Please enter valid arguments for ADDUSER:");
      System.out.println("ADDUSER [username] [password] [preferred currency]");
    }
  }

  private void delUser() {
    String username = readString();

    if (validateUsernameInput(username)) {
      if (accounts.deleteAccount(username)) {
        System.out.println("Account deleted successfully.");
      } else {
        System.out.println("Account does not exist.");
      }
    } else {
      System.out.println("Please enter valid arguments for DELUSER:");
      System.out.println("DELUSER [username]");
    }
  }

  private void changeBalance(boolean add, boolean admin) {
    String username = admin ? readString() : loginUsername;
    String currency = readStringToUpperCase();
    BigDecimal amount = readBigDecimal();

    if ((!admin || validateUsernameInput(username)) && validateCurrencyInput(currency) && isPositive(amount)) {
      if (!admin || accounts.contains(username)) {
        String preferredCurrency = accounts.getAccount(username).getPreferredCurrency();
        BigDecimal convertedAmount = currencyPairs.convert(currency, preferredCurrency, amount);
        if (convertedAmount == null) {
          System.out.println("No conversion data from " + currency + " to " + preferredCurrency + ". Please enter conversion data first.");
        } else {
          BigDecimal newBalance = accounts.changeBalance(username, convertedAmount, add);
          if (add || newBalance != null) {
            System.out.println((add ? "Added " : "Removed ") + convertedAmount + " " + preferredCurrency + (admin ? (add ? " to the account of " : " from the account of ") + username + ". Its new balance is " : (add ? " to your account. Your new balance is " : " from your account. Your new balance is ")) + newBalance + " " + preferredCurrency + ".");
          } else {
            System.out.println("Insufficient funds.");
          }
        }
      } else {
        System.out.println("Account does not exist.");
      }
    } else {
      System.out.println(add ? "Please enter valid arguments for ADD:" : "Please enter valid arguments for SUB:");
      System.out.println(add ? (admin ? "ADD [username] [currency] [amount]" : "ADD [currency] [amount]") : (admin ? "SUB [username] [currency] [amount]" : "SUB [currency] [amount]"));
    }
  }

  private void transfer(boolean admin) {
    String sourceUsername = admin ? readString() : loginUsername;
    String destinationUsername = readString();
    String currency = readStringToUpperCase();
    BigDecimal amount = readBigDecimal();

    if ((!admin || validateUsernameInput(sourceUsername)) && validateUsernameInput(destinationUsername) && validateCurrencyInput(currency) && isPositive(amount)) {
      if ((!admin || accounts.contains(sourceUsername)) && accounts.contains(destinationUsername)) {
        String sourcePreferredCurrency = accounts.getAccount(sourceUsername).getPreferredCurrency();
        String destinationPreferredCurrency = accounts.getAccount(destinationUsername).getPreferredCurrency();
        BigDecimal convertedSourceAmount = currencyPairs.convert(currency, sourcePreferredCurrency, amount);
        BigDecimal convertedDestinationAmount = currencyPairs.convert(currency, destinationPreferredCurrency, amount);
        if (convertedSourceAmount == null) {
          System.out.println("No conversion data from " + currency + " to " + sourcePreferredCurrency + ". Please enter conversion data first.");
        }
        if (convertedDestinationAmount == null) {
          System.out.println("No conversion data from " + currency + " to " + destinationPreferredCurrency + ". Please enter conversion data first.");
        }
        if (convertedSourceAmount != null && convertedDestinationAmount != null) {
          BigDecimal newSourceBalance = accounts.changeBalance(sourceUsername, convertedSourceAmount, false);
          if (newSourceBalance != null) {
            BigDecimal newDestinationBalance = accounts.changeBalance(destinationUsername, convertedDestinationAmount, true);
            System.out.println("Transferred " + convertedSourceAmount + " " + sourcePreferredCurrency + "/" + convertedDestinationAmount + " " + destinationPreferredCurrency + (admin ? " from the account of " + sourceUsername + " to the account of " : " from your account to the account of ") + destinationUsername + (admin ? ". Their new balances are " : ". Your new balance is ") + newSourceBalance + " " + sourcePreferredCurrency + (admin ? " and " : ". Its new balance is ") + newDestinationBalance + " " + destinationPreferredCurrency + (admin ? " respectively." : "."));
          } else {
            System.out.println("Insufficient funds.");
          }
        }
      } else {
        System.out.println(admin ? "Account(s) does not exist." : "Account does not exist.");
      }
    } else {
      System.out.println("Please enter valid arguments for TRANSFER:");
      System.out.println(admin ? "TRANSFER [username 1] [username 2] [currency] [amount]" : "TRANSFER [username] [currency] [amount]");
    }
  }

  private String readString() {
    return lineScanner.hasNext() ? lineScanner.next() : "";
  }

  private String readStringToUpperCase() {
    return lineScanner.hasNext() ? lineScanner.next().toUpperCase() : "";
  }

  private BigDecimal readBigDecimal() {
    if (lineScanner.hasNextBigDecimal()) {
      return lineScanner.nextBigDecimal();
    } else if (lineScanner.hasNext()) {
      String bigDecimalInput = lineScanner.next().replaceAll("[^0-9.]", "");
      return bigDecimalInput.matches("^[0-9.]$") ? new BigDecimal(bigDecimalInput) : BigDecimal.ZERO;
    } else {
      return BigDecimal.ZERO;
    }
  }

  private BigDecimal readBigDecimalStripTrailingZeros() {
    if (lineScanner.hasNextBigDecimal()) {
      return lineScanner.nextBigDecimal().stripTrailingZeros();
    } else if (lineScanner.hasNext()) {
      String bigDecimalInput = lineScanner.next().replaceAll("[^0-9.]", "");
      return bigDecimalInput.matches("^[0-9.]$") ? new BigDecimal(bigDecimalInput).stripTrailingZeros() : BigDecimal.ZERO;
    } else {
      return BigDecimal.ZERO;
    }
  }

  private boolean validateUsernameInput(String username) {
    return username.matches("^[a-zA-Z]+[a-zA-Z0-9]*$");
  }

  private boolean validatePasswordInput(String password) {
    return password.matches("^\\S+$");
  }

  private boolean validateCurrencyInput(String currency) {
    if (currency.matches("^[A-Z]{3}$")) {
      try {
        Currency.getInstance(currency);
        return true;
      } catch (IllegalArgumentException e) {
        System.out.println(currency + " is not a valid currency. Please refer to ISO 4217 for currency codes.");
        return false;
      }
    } else {
      return false;
    }
  }

  private boolean isPositive(BigDecimal bigDecimal) {
    return bigDecimal.compareTo(BigDecimal.ZERO) > 0;
  }

  // Round amount to two decimal places
  static BigDecimal round(BigDecimal amount) {
    return amount.setScale(2, RoundingMode.HALF_EVEN);
  }
}