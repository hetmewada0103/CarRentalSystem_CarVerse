package carverse.rental;

import carverse.db.DBConnect;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Rental {

    // Calculates rental cost using stored procedure (calculate_rental_cost).
    public static Map<String, Double> costCalculator(
            LocalDateTime start,
            LocalDateTime expectedEnd,
            LocalDateTime actualReturn,
            double pricePerHour,
            double lateFeePerHour) throws SQLException {

        Map<String, Double> result = new HashMap<>();

        String sql = "{CALL calculate_rental_cost(?,?,?,?,?,?,?,?,?,?)}";

        try (Connection conn = DBConnect.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            // IN parameters
            cs.setTimestamp(1, Timestamp.valueOf(start));
            cs.setTimestamp(2, Timestamp.valueOf(expectedEnd));
            cs.setTimestamp(3, Timestamp.valueOf(actualReturn));
            cs.setDouble(4, pricePerHour);
            cs.setDouble(5, lateFeePerHour);

            // OUT parameters
            cs.registerOutParameter(6, Types.INTEGER);   // total_hours
            cs.registerOutParameter(7, Types.INTEGER);   // late_hours
            cs.registerOutParameter(8, Types.DECIMAL);   // rental_cost
            cs.registerOutParameter(9, Types.DECIMAL);   // late_fee
            cs.registerOutParameter(10, Types.DECIMAL);  // total_cost

            // Execute procedure
            cs.execute();

            // Fetch results
            result.put("totalHours", (double) cs.getInt(6));
            result.put("lateHours", (double) cs.getInt(7));
            result.put("rentalCost", cs.getDouble(8));
            result.put("lateFee", cs.getDouble(9));
            result.put("totalCost", cs.getDouble(10));
        }
        return result;
    }
}
