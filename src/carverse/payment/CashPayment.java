package carverse.payment;

import java.util.Scanner;

public class CashPayment extends Payment {
    @Override public String name() { return "cash"; }

    @Override
    public boolean pay(double amount, Scanner sc) {
        System.out.println("Payment of ₹" + amount + " received via Cash. Thank you!");
        return true;
    }
}
