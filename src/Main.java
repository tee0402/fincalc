import java.util.*;
import java.io.*;

public class Main {
  public static void main(String[] args) {
    boolean running = true;
    ArrayList<CurrencyPair> currencyPairs = new ArrayList<>();
    try {
      File file = new File("currency.txt");
      if (file.exists()){
        Scanner fileReader = new Scanner(file);
        while (fileReader.hasNextLine()) {
          String line = fileReader.nextLine();
          Scanner lineScanner = new Scanner(line);
          CurrencyPair currencyPair = new CurrencyPair(lineScanner.next(), lineScanner.next(), Double.valueOf(lineScanner.next()));
          currencyPairs.add(currencyPair);
        }
        fileReader.close();
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
    Scanner scanner = new Scanner(System.in);

    System.out.println("FinCalc 1.0");
    System.out.println("Type 'help' for a list of commands.");

    while (running) {
      System.out.print(">> ");

      String command = scanner.nextLine();

      if (command.toUpperCase().startsWith("MAINT")) {
        String currency1 = "";
        String currency2 = "";
        double conversionRate = 0d;
        Scanner maintScanner = new Scanner(command);
        maintScanner.next();

        if (maintScanner.hasNext()) {
          currency1 = maintScanner.next().toUpperCase();
        }
        if (maintScanner.hasNext()) {
          currency2 = maintScanner.next().toUpperCase();
        }
        if (maintScanner.hasNextInt() || maintScanner.hasNextDouble()) {
          conversionRate = Double.valueOf(maintScanner.next());
        }

        if (currency1.length() == 3 && currency1.matches("[a-zA-Z]+$") && currency2.length() == 3 && currency2.matches("[a-zA-Z]+$") && conversionRate > 0) {
          boolean boolVerified = false;

          System.out.println(currency1 + "/" + currency2 + "=" + conversionRate);
          System.out.print("Would you like to save this currency conversion data in the database? (y/n): ");

          while (!boolVerified) {
            command = scanner.nextLine();
            switch (command) {
              case "y":
                boolVerified = true;
                CurrencyPair currencyPair = new CurrencyPair(currency1, currency2, conversionRate);
                if (!existsCurrencyPair(currencyPair, currencyPairs)) {
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
        maintScanner.close();
      }
      else if (command.toLowerCase().startsWith("deposit")) {
        
      }
      else if (command.toLowerCase().startsWith("withdraw")) {

      }
      else if (command.toLowerCase().equals("help")) {
        System.out.println("LIST OF COMMANDS:");
        System.out.println("MAINT [currency 1] [currency 2] [conversion rate] - Enter currency conversion data");
        System.out.println("  - Use ISO Codes for currencies, e.g. USD, EUR, JPY");
        System.out.println("  - Conversion rate is the number of units of currency 2 that are equal to one unit of currency 1");
        System.out.println("quit - Exit the program");
      }
      else if (command.toLowerCase().equals("quit")) {
        running = false;
      }
      else {
          System.out.println("Please enter a valid command. Type 'help' for a list of commands.");
      }
    }

    scanner.close();
    System.out.println("Goodbye.");
  }

  private static boolean existsCurrencyPair(CurrencyPair currencyPair, ArrayList<CurrencyPair> currencyPairs) {
    for (CurrencyPair pair: currencyPairs) {
      if (pair.equals(currencyPair)) {
        return true;
      }
    }
    return false;
  }

  private static void saveCurrencyData(String currency1, String currency2, double conversionRate) {
    try {
      FileWriter fileWriter = new FileWriter(new File("currency.txt"), true);
      fileWriter.write(currency1 + " " + currency2 + " " + conversionRate + "\n");
      fileWriter.close();
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}