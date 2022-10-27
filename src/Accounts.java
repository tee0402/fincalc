import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

class Accounts {
  private final Map<String, Account> accounts = new HashMap<>();

  Accounts() {
    try {
      File file = new File("accounts.txt");
      // Load account information, create admin account if first run
      if (file.exists()) {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
          Scanner lineScanner = new Scanner(scanner.nextLine());
          String username = lineScanner.next();
          accounts.put(username, new Account(username, lineScanner.next(), lineScanner.next(), lineScanner.next(), new BigDecimal(lineScanner.next())));
        }
        scanner.close();
      } else if (file.createNewFile()) {
        addAccount("admin", "admin", "USD");
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  // Converts a byte array to a hexadecimal string
  private String bytesToHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte b : bytes) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }

  // Hashes a string using SHA-256 and returns the hash in hexadecimal form
  private String hash(String text) {
    try {
      return bytesToHex(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  // Authenticates a username-password pair
  boolean authenticate(String username, String password) {
    Account account = getAccount(username);
    return account != null && Objects.equals(hash(account.getSalt() + password), account.getPassword());
  }

  // Returns the account with the specified username if found and null otherwise
  private Account getAccount(String username) {
    return accounts.get(username);
  }

  BigDecimal getBalance(String username) {
    Account account = getAccount(username);
    return account == null ? null : account.getBalance();
  }

  String getPreferredCurrency(String username) {
    Account account = getAccount(username);
    return account == null ? null : account.getPreferredCurrency();
  }

  // Returns true if the account with the specified username exists and false otherwise
  boolean contains(String username) {
    return accounts.containsKey(username);
  }

  // Returns a 256-bit salt
  private String salt() {
    byte[] salt = new byte[32];
    new SecureRandom().nextBytes(salt);
    return bytesToHex(salt);
  }

  // Adds a new account with balance 0 to the text file and ArrayList
  boolean addAccount(String username, String password, String preferredCurrency) {
    if (contains(username)) {
      return false;
    }
    String salt = salt();
    accounts.put(username, new Account(username, salt, hash(salt + password), preferredCurrency, BigDecimal.ZERO));
    return write();
  }

  // Deletes an account
  boolean deleteAccount(String username) {
    if (!contains(username)) {
      return false;
    }
    accounts.remove(username);
    return write();
  }

  void printUsernames() {
    for (String username : accounts.keySet()) {
      System.out.println(username);
    }
  }

  // Changes the balance of the account with the specified username, returns the new balance or null if the change failed
  BigDecimal changeBalance(String username, BigDecimal amount, boolean deposit) {
    Account account = getAccount(username);
    if (account == null) {
      return null;
    }
    BigDecimal oldBalance = account.getBalance();
    BigDecimal newBalance = FinCalc.round(deposit ? oldBalance.add(amount) :  oldBalance.subtract(amount));
    if (!deposit && newBalance.compareTo(BigDecimal.ZERO) < 0) {
      return null;
    }
    account.setBalance(newBalance);
    write();
    return newBalance;
  }

  // Updates text file containing accounts
  private boolean write() {
    try {
      FileWriter fileWriter = new FileWriter("accounts.txt");
      for (Account account : accounts.values()) {
        fileWriter.write(account.getUsername() + " " + account.getSalt() + " " + account.getPassword() + " " + account.getPreferredCurrency() + " " + account.getBalance() + "\n");
      }
      fileWriter.close();
      return true;
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  private static class Account {
    private final String username;
    private final String salt;
    private final String password;
    private final String preferredCurrency;
    private BigDecimal balance;

    private Account(String username, String salt, String password, String preferredCurrency, BigDecimal balance) {
      this.username = username;
      this.salt = salt;
      this.password = password;
      this.preferredCurrency = preferredCurrency;
      this.balance = balance;
    }

    private String getUsername() {
      return username;
    }

    private String getSalt() {
      return salt;
    }

    private String getPassword(){
      return password;
    }

    private String getPreferredCurrency() {
      return preferredCurrency;
    }

    private BigDecimal getBalance() {
      return balance;
    }
    private void setBalance(BigDecimal balance) {
      this.balance = balance;
    }
  }
}