import java.math.*;

class Account {
  private final String username;
  private final String salt;
  private final String password;
  private final String preferredCurrency;
  private BigDecimal balance;

  Account(String username, String salt, String password, String preferredCurrency, BigDecimal balance) {
    this.username = username;
    this.salt = salt;
    this.password = password;
    this.preferredCurrency = preferredCurrency;
    this.balance = balance;
  }

  String getUsername() {
    return username;
  }

  String getSalt() {
    return salt;
  }

  String getPassword(){
    return password;
  }

  String getPreferredCurrency() {
    return preferredCurrency;
  }

  BigDecimal getBalance() {
    return balance;
  }

  void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}