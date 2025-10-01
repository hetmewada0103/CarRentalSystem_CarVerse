package carverse.payment;

import java.util.Scanner;

public abstract class Payment {
    /** lowercase identifier to store in DB (e.g., "cash", "card", "upi") */
    public abstract String name();

    /** Run the payment flow. Return true if payment succeeded. */
    public abstract boolean pay(double amount, Scanner sc);
}
