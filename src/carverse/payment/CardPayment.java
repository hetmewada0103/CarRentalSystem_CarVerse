package carverse.payment;

import java.util.Scanner;

public class CardPayment extends Payment {
    @Override public String name() { return "card"; }

    @Override
    public boolean pay(double amount, Scanner sc) {
        String cardNumber;
        while (true) {
            System.out.print("Enter your 12-digit Card Number: ");
            cardNumber = sc.nextLine().trim();
            if (cardNumber.matches("\\d{12}")) {
                System.out.println("Processing card payment...");
                System.out.println("Payment successful via Card. Thank you!");
                return true;
            } else {
                System.out.println("Invalid card number! Please enter a 12-digit number.");
            }
        }
    }
}
