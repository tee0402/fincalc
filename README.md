# FinCalc
FinCalc is a financial calculator capable of adding and subtracting money from a user's account. The account value is stored in USD. It takes currency conversion data as input from you and allows you to add or subtract amounts in foreign currencies if you have the currency conversion data of that currency to USD.

The easiest way to run this program is to clone this repository to your favorite Java IDE, such as IntelliJ IDEA, and run main.java. Otherwise, if you have JDK installed, you should download the src folder from the repository. Then open up your command line prompt and cd into that downloaded folder on your computer. Run <code>javac Main.java FinCalc.java Account.java CurrencyPair.java</code> and then run <code>java Main.java</code>. The program should run. You may get an error with the javac command on Windows, in which case you need to set the path variable using <code>set path=%path%;C:\Program Files\Java\jdk_1.8.0_131\bin</code> or wherever you have the JDK installed.

Once the application has started, type 'help' to see a list of commands. 'MAINT [currency 1] [currency 2] [conversion rate]' is used to enter in currency conversion data to USD. 'deposit [username] [currency] [amount]' is used to deposit into a user's account. 'withdraw [username] [currency] [amount]' is used to withdraw from a user's account. 'quit' exits the program.

For currencies, only three letter currency ISO Codes can be entered. For usernames, there is no restriction on the format. The only caveat is that they are case-sensitive, so john and John are different users. For conversion rates and currency amounts, any special characters such as dollar signs are removed prior to processing. Commas are also removed, as they are assumed to be thousands separators and decimal points are assumed to be represented by a period, so any currencies using commas as decimals points or periods as thousands separators are not supported. Currencies using spaces as thousands separators are also not supported, as there is no way to distinguish between a thousands separator and a separator of arguments.

# Example Run
Here is what a typical use of the program would look like:
