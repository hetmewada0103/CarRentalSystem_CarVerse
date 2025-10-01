package carverse.admin;

import carverse.model.CustomerList; // Linked list structure for customers
import carverse.model.CustomerNode; // Node class for customer linked list
import carverse.db.DBConnect; // DB connection utility
import carverse.main.CarVerse; // To call CarVerse.adminMenu()

import java.sql.*;
import java.util.Scanner;

public class Admin {
    Scanner sc = new Scanner(System.in);

    // Admin login (username + password check from DB)
    public void adminLogin() {
        try (Connection conn = DBConnect.getConnection()) {
            String query = "SELECT * FROM admin WHERE adminname = ? AND password = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                System.out.print("Enter admin username: ");
                pst.setString(1, sc.nextLine());
                System.out.print("Enter password: ");
                pst.setString(2, sc.nextLine());

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("✅ Admin login successful!");
                        CarVerse.adminMenu();
                    } else {
                        System.out.println("❌ Invalid username or password.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    // Add new car (via stored procedure addCar)
    public void addCar() {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "{CALL addCar(?, ?, ?, ?, ?, ?)}";
            try (CallableStatement cs = conn.prepareCall(sql)) {
                System.out.println("\n=== Add New Car ===");
                System.out.print("Enter car model: ");
                String model = sc.nextLine();
                cs.setString(1, model);

                System.out.print("Enter car brand: ");
                String brand = sc.nextLine();
                cs.setString(2, brand);

                System.out.print("Enter car type: ");
                String type = sc.nextLine();
                cs.setString(3, type);

                System.out.print("Enter price per hour: ");
                double pricePerHour = sc.nextDouble();
                cs.setDouble(4, pricePerHour);

                System.out.print("Enter seats: ");
                int seats = sc.nextInt();
                cs.setInt(5, seats);

                boolean availability = true; // always true when adding
                cs.setBoolean(6, availability);
                sc.nextLine();

                int rows = cs.executeUpdate();
                if (rows > 0) {
                    System.out.println("✅ Car added successfully!");
                } else {
                    System.out.println("❌ Failed to add car.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // View all cars (with availability + average rating)
    public void viewAllCars() {
        System.out.println("enter ratings");
        double r = sc.nextDouble();
        try (Connection conn = DBConnect.getConnection()) {
            String query = "SELECT * FROM car";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("\n=============================");
                System.out.println("🚗  Car List");
                System.out.println("=============================");
                System.out.printf("%-5s %-12s %-10s %-10s %-6s %-14s %-12s %-8s\n", "ID", "Model", "Brand", "Type", "Seats", "Price/Hour", "Status", "Rating");
                System.out.println("---------------------------------------------------------------------");

                while (rs.next()) {
                    int id = rs.getInt("car_id");
                    String model = rs.getString("model");
                    String brand = rs.getString("brand");
                    String type = rs.getString("type");
                    int seats = rs.getInt("seats");
                    double price = rs.getDouble("price_per_hour");
                    boolean available = rs.getBoolean("availability");
                    String status = available ? "Available" : "Booked";
                    double avgRating = getAverageRating(conn, id);
                    if(r<=avgRating) {
                        System.out.printf("%-5d %-12s %-10s %-10s %-6d ₹%-13.2f %-12s ⭐%.1f\n",
                                id, model, brand, type, seats, price, status, avgRating);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    // Update car details (brand, model, price, availability)
    public void updateCarDetails() {
        try (Connection conn = DBConnect.getConnection()) {
            System.out.print("Enter Car ID to update: ");
            int carId = sc.nextInt();
            sc.nextLine();
            System.out.println("Choose detail to update:");
            System.out.println("1. Brand");
            System.out.println("2. Model");
            System.out.println("3. Price per Hour");
            System.out.println("4. Availability (true/false)");
            System.out.print("Choose an option (1 to 4): ");
            int choice = CarVerse.getIntInput(1, 4);

            String column;
            if (choice == 1) {
                column = "brand";
                System.out.print("Enter new brand: ");
            } else if (choice == 2) {
                column = "model";
                System.out.print("Enter new model: ");
            } else if (choice == 3) {
                column = "price_per_hour";
                System.out.print("Enter new price/hour: ");
            } else if (choice == 4) {
                column = "availability";
                System.out.print("Enter availability (true/false): ");
            } else {
                System.out.println("Invalid choice.");
                return;
            }
            String newValue = sc.nextLine();
            String query = "UPDATE car SET " + column + " = ? WHERE car_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                if (column.equals("price_per_hour"))
                    ps.setDouble(1, Double.parseDouble(newValue));
                else if (column.equals("availability"))
                    ps.setBoolean(1, Boolean.parseBoolean(newValue));
                else
                    ps.setString(1, newValue);
                ps.setInt(2, carId);

                int r = ps.executeUpdate();
                System.out.println(r > 0 ? "Car updated successfully." : "Car update failed.");
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    // View available cars (with sorting options)
    public void viewAvailableCars() {

        System.out.println("\n=====================================");
        System.out.println("🚘   View Available Cars - Sort By   ");
        System.out.println("=====================================\n");

        System.out.println("1️⃣  Brand (A → Z)");
        System.out.println("2️⃣  Brand (Z → A)");
        System.out.println("3️⃣  Price per Hour (Low → High)");
        System.out.println("4️⃣  Car Type");
        System.out.println("5️⃣  Seats (Low → High)");
        System.out.println("6️⃣  Seats (High → Low)");

        System.out.println("\n-------------------------------------");
        System.out.print("👉 Choose an option (1 - 6): ");

        int choice = CarVerse.getIntInput(1,6);

        String orderBy;
        switch (choice) {
            case 1:
                orderBy = "ORDER BY brand ASC";
                break;
            case 2:
                orderBy = "ORDER BY brand DESC";
                break;
            case 3:
                orderBy = "ORDER BY price_per_hour ASC";
                break;
            case 4:
                orderBy = "ORDER BY type ASC";
                break;
            case 5:
                orderBy = "ORDER BY seats ASC";
                break;
            case 6:
                orderBy = "ORDER BY seats DESC";
                break;
            default:
                System.out.println("Invalid choice. Showing default order.");
                orderBy = "ORDER BY car_id ASC";
                break;
        }

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT * FROM car WHERE availability = 1 " + orderBy;
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("\n==============================");
                System.out.println("🚗  Available Cars for Rent");
                System.out.println("==============================");
                System.out.printf("%-5s %-12s %-10s %-10s %-6s %-14s %-8s\n", "ID", "Model", "Brand", "Type", "Seats", "Price/Hour", "Rating");
                System.out.println("----------------------------------------------------------");

                boolean hasCars = false;
                while (rs.next()) {
                    hasCars = true;
                    int id = rs.getInt("car_id");
                    String model = rs.getString("model");
                    String brand = rs.getString("brand");
                    String type = rs.getString("type");
                    int seats = rs.getInt("seats");
                    double price = rs.getDouble("price_per_hour");
                    double avgRating = getAverageRating(conn, id);
                    System.out.printf("%-5d %-12s %-10s %-10s %-6d ₹%-13.2f ⭐%.1f\n", id, model, brand, type, seats, price, avgRating);
                }

                if (!hasCars) {
                    System.out.println("❌ No cars are currently available.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error displaying available cars: " + e.getMessage());
        }
    }

    // Update car availability (only if not booked)
    public void updateCarAvailability() {
        try (Connection conn = DBConnect.getConnection()) {
            System.out.print("Enter Car ID to update availability: ");
            int carId = sc.nextInt();
            sc.nextLine();

            // Step 1: Check if car exists
            String checkCar = "SELECT * FROM car WHERE car_id = ?";
            try (PreparedStatement checkCarPs = conn.prepareStatement(checkCar)) {
                checkCarPs.setInt(1, carId);
                try (ResultSet carRs = checkCarPs.executeQuery()) {
                    if (!carRs.next()) {
                        System.out.println("❌ Car ID not found.");
                        return;
                    }
                }
            }

            // Step 2: Check if car is currently booked in bookings table
            String checkBooking = "SELECT * FROM bookings WHERE car_id = ? AND status = 'Booked'";
            try (PreparedStatement checkBookingPs = conn.prepareStatement(checkBooking)) {
                checkBookingPs.setInt(1, carId);
                try (ResultSet bookingRs = checkBookingPs.executeQuery()) {
                    if (bookingRs.next()) {
                        System.out.println("❌ This car is currently booked. You cannot change its availability.");
                        return;
                    }
                }
            }

            // Step 3: If not booked, proceed to update availability
            System.out.print("Set availability (true for available, false for not available): ");
            boolean availability = sc.nextBoolean();

            String updateAvailability = "UPDATE car SET availability = ? WHERE car_id = ?";
            try (PreparedStatement updatePs = conn.prepareStatement(updateAvailability)) {
                updatePs.setBoolean(1, availability);
                updatePs.setInt(2, carId);

                int updatedRows = updatePs.executeUpdate();
                if (updatedRows > 0) {
                    System.out.println("✅ Car availability updated successfully.");
                } else {
                    System.out.println("❌ Failed to update car availability.");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // View currently rented cars
    public void viewCurrentlyRentedCars() throws SQLException {
        String sql = """
                    SELECT
                      booking_id,
                      car_id,
                      customer_id,
                      start_datetime,
                      end_datetime,
                      start_location,
                      end_location,
                      total_hours,
                      total_cost,
                      status
                    FROM bookings
                    WHERE status = 'Booked'
                    ORDER BY start_datetime DESC
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n==============================");
            System.out.println("🚘 Currently Rented (from bookings)");
            System.out.println("==============================");
            System.out.printf("%-8s %-6s %-11s %-23s %-23s %-18s %-18s %-10s %-12s %-10s%n",
                    "BookID", "CarID", "CustomerID", "Start", "End", "StartLoc", "EndLoc", "Hours", "Cost", "Status");

            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("%-8d %-6d %-11d %-23s %-23s %-18s %-18s %-10.2f %-12.2f %-10s%n",
                        rs.getInt("booking_id"),
                        rs.getInt("car_id"),
                        rs.getInt("customer_id"),
                        rs.getTimestamp("start_datetime"),
                        rs.getTimestamp("end_datetime"),
                        rs.getString("start_location"),
                        rs.getString("end_location"),
                        rs.getDouble("total_hours"),
                        rs.getDouble("total_cost"),
                        rs.getString("status"));
            }

            if (!any) {
                System.out.println("❌ No cars are currently rented (no active bookings).");
            }
        } catch (SQLException e) {
            System.out.println("❌ DB error: " + e.getMessage());
            throw e;
        }
    }

    // View overdue rentals
    public void viewOverdueRentals() throws SQLException {
        String sql = """
                    SELECT booking_id, customer_id, car_id, start_location, end_location, start_datetime, end_datetime, total_hours,
                      total_cost, status,
                      -- how many whole hours past the end time
                      TIMESTAMPDIFF(HOUR, end_datetime, CURRENT_TIMESTAMP) AS hours_overdue
                    FROM bookings
                    WHERE status = 'Overdue'
                       OR (status = 'Booked' AND end_datetime < CURRENT_TIMESTAMP)
                    ORDER BY end_datetime ASC
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n==============================");
            System.out.println("⏰ Overdue Rentals (from bookings)");
            System.out.println("==============================");
            System.out.printf(
                    "%-8s %-6s %-11s %-23s %-23s %-18s %-18s %-10s %-12s %-10s%n",
                    "BookID", "CarID", "CustomerID", "Start", "End", "StartLoc", "EndLoc", "Hours", "Cost", "Overdue(h)"
            );

            boolean any = false;
            int count = 0;
            while (rs.next()) {
                any = true;
                count++;
                System.out.printf(
                        "%-8d %-6d %-11d %-23s %-23s %-18s %-18s %-10.2f %-12.2f %-10d%n",
                        rs.getInt("booking_id"),
                        rs.getInt("car_id"),
                        rs.getInt("customer_id"),
                        rs.getTimestamp("start_datetime"),
                        rs.getTimestamp("end_datetime"),
                        rs.getString("start_location"),
                        rs.getString("end_location"),
                        rs.getDouble("total_hours"),
                        rs.getDouble("total_cost"),
                        Math.max(0, rs.getInt("hours_overdue"))
                );
            }

            if (!any) {
                System.out.println("✅ No overdue rentals found.");
            } else {
                System.out.println("\nTotal overdue: " + count);
            }
        }
    }

    // Generate reports (cars, rentals, revenue, overdue, cancelled)
    public void generateReports() {
        try (Connection conn = DBConnect.getConnection()) {
            String totalCarsQuery = "SELECT COUNT(*) AS total_cars FROM car";
            String totalRentalsQuery = "SELECT COUNT(*) AS total_rentals FROM bookings;";
            String availableCarsQuery = "SELECT COUNT(*) AS available FROM car WHERE availability = 1";
            String rentedCarsQuery = "SELECT COUNT(*) AS rented FROM bookings WHERE status = 'Booked'";
            String overdueQuery = "SELECT COUNT(*) AS overdue FROM bookings WHERE status = 'Overdue' OR (status = 'Booked' AND end_datetime < CURRENT_TIMESTAMP)";
            String cancelledQuery = "SELECT COUNT(*) AS cancelled FROM bookings WHERE status = 'Cancelled'";
            String ReturnedQuery = "SELECT COUNT(*) AS Returned FROM bookings WHERE status = 'Returned'";
            String totalRevenueQuery = "SELECT IFNULL(SUM(total_price), 0) AS revenue FROM rental WHERE total_price IS NOT NULL";

            int totalCars = 0, totalRentals = 0, availableCars = 0, rentedCars = 0, overdueCount = 0, cancelledCount = 0, returnCount = 0;
            double totalRevenue = 0;

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(totalCarsQuery)) {
                    if (rs.next()) totalCars = rs.getInt("total_cars");
                }
                try (ResultSet rs = stmt.executeQuery(totalRentalsQuery)) {
                    if (rs.next()) totalRentals = rs.getInt("total_rentals");
                }
                try (ResultSet rs = stmt.executeQuery(availableCarsQuery)) {
                    if (rs.next()) availableCars = rs.getInt("available");
                }
                try (ResultSet rs = stmt.executeQuery(rentedCarsQuery)) {
                    if (rs.next()) rentedCars = rs.getInt("rented");
                }
                try (ResultSet rs = stmt.executeQuery(overdueQuery)) {
                    if (rs.next()) overdueCount = rs.getInt("overdue");
                }
                try (ResultSet rs = stmt.executeQuery(cancelledQuery)) {
                    if (rs.next()) cancelledCount = rs.getInt("cancelled");
                }
                try (ResultSet rs = stmt.executeQuery(ReturnedQuery)) {
                    if (rs.next()) returnCount = rs.getInt("Returned");
                }
                try (ResultSet rs = stmt.executeQuery(totalRevenueQuery)) {
                    if (rs.next()) totalRevenue = rs.getDouble("revenue");
                }
            }

            System.out.println("\n=========== REPORTS ===========");
            System.out.println("Total Number of Cars: " + totalCars);
            System.out.println("Total Number of Rentals: " + totalRentals);
            System.out.println("Currently Available Cars: " + availableCars);
            System.out.println("Currently Rented Cars: " + rentedCars);
            System.out.println("Overdue Rentals: " + overdueCount);
            System.out.println("Cancelled Rentals: " + cancelledCount);
            System.out.println("Returned Rentals: " + returnCount);
            System.out.printf("Total Revenue: ₹%.2f\n", totalRevenue);
            System.out.println("===============================\n");
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    // Remove car from DB
    public void removeCar() {
        try (Connection conn = DBConnect.getConnection()) {
            System.out.print("Enter Car ID to remove: ");
            int carId = sc.nextInt();
            sc.nextLine();

            String checkQuery = "SELECT * FROM car WHERE car_id = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
                checkPs.setInt(1, carId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("❌ Car ID not found.");
                        return;
                    }
                    String deleteQuery = "DELETE FROM car WHERE car_id = ?";
                    try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
                        deletePs.setInt(1, carId);

                        int result = deletePs.executeUpdate();
                        if (result > 0) {
                            System.out.println("✅ Car with ID " + carId + " removed successfully.");
                        } else {
                            System.out.println("❌ Failed to remove car. Please check Car ID.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    // Get average rating for car (via SQL function)
    private double getAverageRating(Connection conn, int carId) {
        String sql = "SELECT getAverageRating(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
                return 0.0;
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error fetching rating for car ID " + carId + ": " + e.getMessage());
            return 0.0;
        }
    }

    // View all customers (using linked list)
    public void viewAllCustomer() {
        CustomerList list = new CustomerList();

        list.clear();

        String sql = "SELECT customer_id, name, email, phone_no, address, dob FROM customer";

        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.insertCustomer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_no"),
                        rs.getString("address"),
                        rs.getDate("dob")
                );
            }

            if (list.isEmpty()) {
                System.out.println("\n❌ No customers found.");
                return;
            }

            System.out.println("\n============== All Customers ==============");
            System.out.printf("%-5s %-20s %-25s %-15s %-25s %-12s\n",
                    "ID", "Name", "Email", "Phone", "Address", "DOB");
            System.out.println("--------------------------------------------------------------------------------------------");

            for (CustomerNode cur = list.head(); cur != null; cur = cur.next) {
                System.out.printf("%-5d %-20s %-25s %-15s %-25s %-12s\n",
                        cur.id,
                        cur.name,
                        cur.email,
                        cur.phone,
                        cur.address,
                        cur.dob != null ? cur.dob.toString() : "");
            }
            System.out.println("===========================================\n");

        } catch (Exception e) {
            System.out.println("❌ Error displaying customers: " + e.getMessage());
        }

    }
}
