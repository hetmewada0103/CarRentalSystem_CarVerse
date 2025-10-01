package carverse.main;

import carverse.admin.Admin; // Admin-related features
import carverse.customer.Customer; // Customer-related feature
import carverse.db.DBConnect; // Database connection utility

import java.sql.*;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CarVerse {
    static Scanner sc = new Scanner(System.in);
    static Admin admin = new Admin();
    static Customer customer = new Customer();

    // Main menu (entry point)
    public static void main(String[] args) {
        int choice;
        do {
            System.out.println();
            System.out.println("\n=====================================");
            System.out.println("🚗✨   Welcome to CarVerse   ✨🚗");
            System.out.println("=====================================\n");

            System.out.println("1️⃣  User Login");
            System.out.println("2️⃣  User Registration");
            System.out.println("3️⃣  Admin Login");
            System.out.println("4️⃣  Exit");

            System.out.println("\n-------------------------------------");
            System.out.print("👉 Choose an option (1 - 4): ");
            choice = getIntInput(1, 4);
            switch (choice) {
                case 1:
                    customer.customerLogin(); // login existing customer
                    break;
                case 2:
                    customer.customerRegistartion(); // new customer registration
                    break;
                case 3:
                    admin.adminLogin(); // admin login
                    break;
                case 4:
                    System.out.println("Good Bye>>>"); // exit program
            }
        } while (choice != 4);
    }

    // Admin menu options
    public static void adminMenu() throws SQLException {
        int choice;
        do {
            System.out.println();
            System.out.println("\n=====================================");
            System.out.println("🛠️   CarVerse - Admin Menu   🛠️");
            System.out.println("=====================================\n");

            System.out.println("1️⃣  Add Car");
            System.out.println("2️⃣  View All Cars");
            System.out.println("3️⃣  Update Car Details");
            System.out.println("4️⃣  View Available Cars");
            System.out.println("5️⃣  Update Car Availability");
            System.out.println("6️⃣  Currently Rented Cars");
            System.out.println("7️⃣  View Overdue Rentals");
            System.out.println("8️⃣  Generate Reports");
            System.out.println("9️⃣  Remove Car");
            System.out.println("🔟  View All Customers");
            System.out.println("1️⃣1️⃣ Logout");

            System.out.println("\n-------------------------------------");
            System.out.print("👉 Choose an option (1 - 11): ");
            choice = getIntInput(1, 11);

            switch (choice) {
                case 1:
                    admin.addCar();                // add new car
                    break;
                case 2:
                    admin.viewAllCars();           // view all cars
                    break;
                case 3:
                    admin.updateCarDetails();      // update car details
                    break;
                case 4:
                    admin.viewAvailableCars();     // view available cars
                    break;
                case 5:
                    admin.viewAllCars();           // update car availability
                    admin.updateCarAvailability();
                    break;
                case 6:
                    admin.viewCurrentlyRentedCars(); // view cars currently rented
                    break;
                case 7:
                    admin.viewOverdueRentals();     // view overdue rentals
                    break;
                case 8:
                    admin.generateReports();       // generate reports
                    break;
                case 9:
                    admin.removeCar();             // remove car
                    break;
                case 10:
                    admin.viewAllCustomer();      // view all customers
                    break;
                case 11:
                    System.out.println("Admin logged out.");  // logout
                    System.out.println();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 11);
    }

    // Customer menu options
    public static void customerMenu() throws SQLException {
        int choice;
        do {
            System.out.println();
            System.out.println("\n=====================================");
            System.out.println("👤   CarVerse - Customer Menu   👤");
            System.out.println("=====================================\n");

            System.out.println("1️⃣  Search Available Cars");
            System.out.println("2️⃣  Book a Car");
            System.out.println("3️⃣  View My Bookings");
            System.out.println("4️⃣  Cancel Booking");
            System.out.println("5️⃣  Return Car");
            System.out.println("6️⃣  Rate a Car");
            System.out.println("7️⃣  Update Profile");
            System.out.println("8️⃣  Logout");

            System.out.println("\n-------------------------------------");
            System.out.print("👉 Choose an option (1 - 8): ");
            choice = getIntInput(1, 8);

            switch (choice) {
                case 1:
                    admin.viewAvailableCars(); // search available cars
                    break;
                case 2:
                    admin.viewAvailableCars(); // book a car
                    customer.bookCar();
                    break;
                case 3:
                    customer.viewMyBookings(); // view customer bookings
                    break;
                case 4:
                    customer.cancelBooking(); // cancel booking
                    break;
                case 5:
                    customer.returnCar();     // return rented car
                    break;
                case 6:
                    customer.giveRating();    // give rating to car
                    break;
                case 7:
                    customer.updateProfile(); // update customer profile
                    break;
                case 8:
                    System.out.println("Logged out."); // logout
                    System.out.println();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 8);
    }

    // Utility: get valid integer input
    public static int getIntInput(int min, int max) {
        while (true) {
            try {
                int choice = sc.nextInt();
                sc.nextLine(); // clear buffer
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.println("❌ Please enter a number between " + min + " and " + max);
                    System.out.println("Enter your choice");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
                sc.nextLine();// clear wrong input
                System.out.print("Enter your choice: ");
            }
        }
    }

    // Static block: auto-update overdue bookings
    static {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = """
                UPDATE bookings
                SET status = 'Overdue'
                WHERE status = 'Booked'
                  AND end_datetime < NOW()
            """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int rows = ps.executeUpdate(); // mark expired bookings as overdue
                if (rows > 0) {
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to update overdue bookings: " + e.getMessage());
        }
    }
}
