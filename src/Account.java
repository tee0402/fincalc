import java.math.*;

class Account {
  private String username;
  private BigDecimal balance;

  Account(String username, BigDecimal balance) {
    this.username = username;
    this.balance = balance;
  }

  String getUsername() {
    return username;
  }

  BigDecimal getBalance() {
    return balance;
  }

  void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
