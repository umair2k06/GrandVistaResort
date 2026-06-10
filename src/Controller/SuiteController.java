package Controller;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.SuiteModel;       // Direct package reference
import model.main1;

public class SuiteController {

    private ObservableList<SuiteModel> suiteList = FXCollections.observableArrayList();

    public ObservableList<SuiteModel> getAllSuites() {
        suiteList.clear();
        String sql = "SELECT suite_id, door_number, floor_level, hk_status FROM Suites";

        try (Connection con = model.DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                suiteList.add(new SuiteModel(
                        rs.getInt("suite_id"),
                        rs.getString("door_number"),
                        rs.getInt("floor_level"),
                        "Standard", // Default value for presentation since UI updates this via DB joins
                        0.0,
                        rs.getString("hk_status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Suite Query Error: " + e.getMessage());
        }
        return suiteList;
    }

    public boolean addSuite(String doorNum, int floor, int catId, String status) {
        String sql = "INSERT INTO Suites (door_number, floor_level, category_id, hk_status) VALUES (?, ?, ?, ?)";

        try (Connection con = model.DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, doorNum);
            ps.setInt(2, floor);
            ps.setInt(3, catId);
            ps.setString(4, status);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Suite Insert Error: " + e.getMessage());
            return false;
        }
    }
}