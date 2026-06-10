package Controller;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.BookingModel;
import model.DBconnection;

public class BookingController {

    private ObservableList<BookingModel> bookingList = FXCollections.observableArrayList();

    public ObservableList<BookingModel> getAllBookings(String filter) {
        bookingList.clear();

     
        String sql = "SELECT r.booking_id, CONCAT(g.first_name,' ',g.last_name), rm.door_number, " +
                "rt.tier_name, r.check_in, r.check_out, DATEDIFF(r.check_out,r.check_in), " +
                "r.book_status, r.grand_total, COALESCE(CONCAT(s.first_name,' ',s.last_name),'—') " +
                "FROM Bookings r " +
                "JOIN Clients g ON r.client_id=g.client_id " +
                "JOIN Suites rm ON r.suite_id=rm.suite_id " +
                "JOIN Tier_Category rt ON rm.category_id=rt.category_id " +
                "LEFT JOIN Employees s ON r.employee_id=s.employee_id";

        if (!"All".equals(filter)) {
            sql += " WHERE r.book_status='" + filter + "'";
        }
        sql += " ORDER BY r.booking_id DESC";

        try (Connection con = DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                bookingList.add(new BookingModel(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getDouble(9),
                        rs.getString(10)
                ));
            }
        } catch (SQLException e) {
            System.err.println("Booking Fetch Error: " + e.getMessage());
        }
        return bookingList;
    }

    public boolean createBooking(int clientId, int suiteId, int employeeId, String checkIn, String checkOut, int adults, int children) {
        String sql = "INSERT INTO Bookings (client_id, suite_id, employee_id, check_in, check_out, adults, children, book_status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'Approved', CURDATE())";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ps.setInt(2, suiteId);
            ps.setInt(3, employeeId);
            ps.setDate(4, Date.valueOf(checkIn));
            ps.setDate(5, Date.valueOf(checkOut));
            ps.setInt(6, adults);
            ps.setInt(7, children);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Booking Insertion Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBookingStatus(String newStatus, int bookingId, String requiredStatus) {
        String sql = "UPDATE Bookings SET book_status=? WHERE booking_id=? AND book_status=?";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, bookingId);
            ps.setString(3, requiredStatus);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Booking Status Update Error: " + e.getMessage());
            return false;
        }
    }
}
