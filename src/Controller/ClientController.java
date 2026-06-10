package controller;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ClientModel;     
import model.main1;            // Imports main1 to access DBconnection

public class ClientController {

    private ObservableList<ClientModel> clientList = FXCollections.observableArrayList();

    public ObservableList<ClientModel> getAllClients() {
        clientList.clear();
        String sql = "SELECT client_id, first_name, last_name, email_addr, contact_no, id_card, origin, home_city FROM Clients";

        // Accesses DBconnection from inside your model package
        try (Connection con = model.DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                clientList.add(new ClientModel(
                        rs.getInt("client_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email_addr"),
                        rs.getString("contact_no"),
                        rs.getString("id_card"),
                        rs.getString("origin"),
                        rs.getString("home_city")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Client Query Error: " + e.getMessage());
        }
        return clientList;
    }

    public boolean addClient(String firstName, String lastName, String email, String phone, String cnic, String nationality, String address, String city) {
        String sql = "INSERT INTO Clients (first_name, last_name, email_addr, contact_no, id_card, origin, home_address, home_city, registered_on) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURDATE())";

        try (Connection con = model.DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, cnic.isEmpty() ? null : cnic);
            ps.setString(6, nationality.isEmpty() ? "Pakistani" : nationality);
            ps.setString(7, address);
            ps.setString(8, city);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Client Insert Error: " + e.getMessage());
            return false;
        }
    }
}