import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;

class Accounts {
  private final ArrayList<Account> accounts = new ArrayList<>();

  Accounts() {
    try {
      File file = new File("accounts.txt");
      // Load account information, create admin account if first run
      if (file.exists()) {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
          Scanner lineScanner = new Scanner(scanner.nextLine());
          accounts.add(new Account(lineScanner.next(), lineScanner.next(), lineScanner.next(), lineScanner.next(), new BigDecimal(lineScanner.next())));
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
    String hash = null;
    Account account = getAccount(username);
    if (account != null) {
      hash = hash(account.getSalt() + password);
    }
    return hash != null && hash.equals(account.getPassword());
  }

  // Returns the account with the specified username if found and null otherwise
  private Account getAccount(String username) {
    for (Account account : accounts) {
      if (account.getUsername().equals(username)) {
        return account;
      }
    }
    return null;
  }

  BigDecimal getBalance(String username) {
    Account account = getAccount(username);
    if (account != null) {
      return account.getBalance();
    }
    return null;
  }

  String getPreferredCurrency(String username) {
    Account account = getAccount(username);
    if (account != null) {
      return account.getPreferredCurrency();
    }
    return null;
  }

  // Returns true if the account with the specified username exists and false otherwise
  boolean contains(String username) {
    return getAccount(username) != null;
  }

  // Returns a 256-bit salt
  private String salt() {
    byte[] salt = new byte[32];
    new SecureRandom().nextBytes(salt);
    return bytesToHex(salt);
  }

  // Adds a new account with balance 0 to the text file and ArrayList
  boolean addAccount(String username, String password, String preferredCurrency) {
    if (!contains(username)) {
      String salt = salt();
      accounts.add(new Account(username, salt, hash(salt + password), preferredCurrency, BigDecimal.ZERO));
      return write();
    }
    return false;
  }

  // Deletes an account
  boolean deleteAccount(String username) {
    Account account = getAccount(username);
    if (account != null) {
      accounts.remove(account);
      return write();
    }
    return false;
  }

  void printUsernames() {
    for (Account account : accounts) {
      System.out.println(account.getUsername());
    }
  }

  // Changes the balance of the account with the specified username, returns the new balance or null if the change failed
  BigDecimal changeBalance(String username, BigDecimal amount, boolean deposit) {
    Account account = getAccount(username);
    if (account != null) {
      if (deposit) {
        BigDecimal newBalance = FinCalc.round(account.getBalance().add(amount));
        account.setBalance(newBalance);
        write();
        return newBalance;
      } else {
        BigDecimal newBalance = FinCalc.round(account.getBalance().subtract(amount));
        if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
          account.setBalance(newBalance);
          write();
          return newBalance;
        } else {
          return null;
        }
      }
    }
    return null;
  }

  // Updates text file containing accounts
  private boolean write() {
    try {
      FileWriter fileWriter = new FileWriter("accounts.txt");
      for (Account account : accounts) {
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