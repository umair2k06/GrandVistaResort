package Controller;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.EmployeeModel;     
import model.DBconnection;

public class EmployeeController {

    private ObservableList<EmployeeModel> employeeList = FXCollections.observableArrayList();

    public ObservableList<EmployeeModel> getAllEmployees() {
        employeeList.clear();
     
        String sql = "SELECT employee_id, first_name, last_name, job_role, email, phone, salary, work_shift, joining_date FROM Employee ORDER BY job_role, first_name";

        try (Connection con = DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                employeeList.add(new EmployeeModel(
                        rs.getInt("employee_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("job_role"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDouble("salary"),
                        rs.getString("work_shift"),
                        rs.getString("joining_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Employee Sync Query Error: " + e.getMessage());
        }
        return employeeList;
    }

    public boolean addEmployee(String firstName, String lastName, String role, String email, String phone, String joinDate, double salary, String shift) {
        String sql = "INSERT INTO Employee (first_name, last_name, job_role, email, phone, joining_date, salary, work_shift) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, role);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setDate(6, Date.valueOf(joinDate));
            ps.setDouble(7, salary);
            ps.setString(8, shift);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Employee Add Write Error: " + e.getMessage());
            return false;
        }
    }
}
