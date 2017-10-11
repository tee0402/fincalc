class Account {
  private String username;
  private double balance;

  Account(String username, double balance) {
    this.username = username;
    this.balance = balance;
  }

  String getUsername() {
    return username;
  }

  double getBalance() {
    return balance;
  }

  void setBalance(double balance) {
    this.balance = balance;
  }
}
