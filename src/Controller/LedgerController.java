package controller;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.TransactionModel;   // Direct package-level import
import model.DBconnection;

public class LedgerController {

    private ObservableList<TransactionModel> ledgerList = FXCollections.observableArrayList();

    public ObservableList<TransactionModel> getAllTransactions() {
        ledgerList.clear();
        // Matching the exact View from your main5.java file
        String sql = "SELECT payment_id, booking_id, customer_name, room_number, " +
                "booking_total, amount_paid, payment_method, payment_date, " +
                "payment_status, COALESCE(transaction_reference,'') " +
                "FROM vw_Financial_Transaction_Summary ORDER BY payment_id DESC";

        try (Connection con = DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ledgerList.add(new TransactionModel(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getDouble(5),
                        rs.getDouble(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getString(10)
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ledger View Query Error: " + e.getMessage());
        }
        return ledgerList;
    }

    public boolean postPayment(int bookingId, double amount, String date, String channel, String status, String reference) {
        // Matches your main5.java insert destination table (Ledger)
        String sql = "INSERT INTO Ledger (booking_id, amount_paid, payment_date, payment_method, payment_status, transaction_reference) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ps.setDouble(2, amount);
            ps.setDate(3, Date.valueOf(date)); // Dynamic format matching UI selectors
            ps.setString(4, channel);
            ps.setString(5, status);
            ps.setString(6, reference.isEmpty() ? null : reference.trim());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ledger Write Error: " + e.getMessage());
            return false;
        }
    }
}