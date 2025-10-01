package carverse.payment;

import java.util.Scanner;

public class UpiPayment extends Payment {

    // Return payment method name
    @Override
    public String name() {
        return "upi";
    }

    // Handle UPI payment process
    @Override
    public boolean pay(double amount, Scanner sc) {
        String upiId;
        while (true) {
            System.out.print("Enter your UPI ID (e.g., example@upi): ");
            upiId = sc.nextLine().trim();
            if (upiId.contains("@") && upiId.indexOf('@') != 0 && upiId.indexOf('@') != upiId.length() - 1) {
                System.out.println("Processing UPI payment...");
                System.out.println("✅ Payment successful via UPI. Thank you!");
                return true;
            } else {
                System.out.println("❌ Invalid UPI ID! Enter a valid format (e.g., example@upi).");
            }
        }
    }
}
