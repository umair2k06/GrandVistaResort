package model;// ============================================================
//  model.main3.java  —  Client Profile Directory Module
// ============================================================
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.beans.property.*;

import java.sql.*;
import java.time.LocalDate;

public class main3 {

    private TableView<ClientModel> table = new TableView<>();
    private ObservableList<ClientModel> data = FXCollections.observableArrayList();

    private TextField fFirst, fLast, fEmail, fPhone, fCnic, fNat, fAddr, fCity, fSearch;
    private Label statusLbl;

    @SuppressWarnings("unchecked")
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("👤 Guest Profile Directory");
        stage.initModality(Modality.NONE);

        TableColumn<ClientModel,Integer> colId  = intCol("ID",          "clientId",   55);
        TableColumn<ClientModel,String>  colFN  = stringCol("First Name",  "firstName",  110);
        TableColumn<ClientModel,String>  colLN  = stringCol("Last Name",   "lastName",   110);
        TableColumn<ClientModel,String>  colEm  = stringCol("Email Address","emailAddr",  180);
        TableColumn<ClientModel,String>  colPh  = stringCol("Phone Index", "contactNo",  120);
        TableColumn<ClientModel,String>  colCn  = stringCol("CNIC / Pass", "idCard",     130);
        TableColumn<ClientModel,String>  colNat = stringCol("Nationality", "origin",     110);
        TableColumn<ClientModel,String>  colCt  = stringCol("City Location","homeCity",   100);

        table.getColumns().addAll(colId, colFN, colLN, colEm, colPh, colCn, colNat, colCt);
        table.setItems(data);
        styleTable(table);
        loadGuests("");

        fSearch = Styles.field("Search profiles dynamically...");
        fSearch.setPrefWidth(320);
        fSearch.textProperty().addListener((obs, oldVal, newVal) -> loadGuests(newVal.trim()));

        Button btnAll = Styles.outlineBtn("Show All");
        btnAll.setOnAction(e -> { fSearch.clear(); loadGuests(""); });

        HBox searchBar = new HBox(12, fSearch, btnAll);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        fFirst = Styles.field("First name");
        fLast  = Styles.field("Last name");
        fEmail = Styles.field("email@domain.com");
        fPhone = Styles.field("e.g., 03001234567");
        fCnic  = Styles.field("13-digit format");
        fNat   = Styles.field("Pakistani"); fNat.setText("Pakistani");
        fAddr  = Styles.field("Street residential info");
        fCity  = Styles.field("City residence");

        statusLbl = new Label("");
        statusLbl.setStyle("-fx-text-fill:#38A169; -fx-font-size:12px;");

        Button btnAdd    = Styles.goldBtn("➕ Add Guest");
        Button btnUpdate = Styles.outlineBtn("✏ Update");
        Button btnDelete = Styles.redBtn("🗑 Delete");
        Button btnClear  = Styles.outlineBtn("✖ Clear");
        Button btnView   = Styles.greenBtn("📋 Check Reservations");

        btnAdd.setOnAction(e    -> addGuest());
        btnUpdate.setOnAction(e -> updateGuest());
        btnDelete.setOnAction(e -> deleteGuest());
        btnClear.setOnAction(e  -> clearForm());
        btnView.setOnAction(e   -> viewGuestReservations());

        table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) populateForm(n);
        });

        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(10);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color:#121820; -fx-background-radius:10; -fx-border-color:#2D3748; -fx-border-radius:10;");

        addRow(form, 0, "First Name:", fFirst);
        addRow(form, 1, "Last Name:",  fLast);
        addRow(form, 2, "Email Address:", fEmail);
        addRow(form, 3, "Phone Contact:", fPhone);
        addRow(form, 4, "CNIC Number:", fCnic);
        addRow(form, 5, "Nationality:", fNat);
        addRow(form, 6, "Street Address:", fAddr);
        addRow(form, 7, "City Context:",   fCity);

        HBox btns = new HBox(8, btnAdd, btnUpdate);
        HBox btnsSub = new HBox(8, btnDelete, btnClear);

        form.add(btns, 1, 8);
        form.add(btnsSub, 1, 9);
        form.add(btnView, 1, 10);
        form.add(statusLbl, 1, 11);

        VBox rightPane = new VBox(12, searchBar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        rightPane.setPadding(new Insets(0, 0, 0, 12));

        HBox mainLayout = new HBox(form, rightPane);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        mainLayout.setPadding(new Insets(16));
        mainLayout.setStyle("-fx-background-color:#0D1117;");

        Scene scene = new Scene(mainLayout, 1150, 650);
        stage.setScene(scene);
        stage.show();
    }

    private void loadGuests(String query) {
        data.clear();
        String sql = "SELECT * FROM Clients WHERE first_name LIKE ? OR last_name LIKE ? OR id_card LIKE ? OR email_addr LIKE ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String wildcard = "%" + query + "%";
            stmt.setString(1, wildcard); stmt.setString(2, wildcard);
            stmt.setString(3, wildcard); stmt.setString(4, wildcard);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new ClientModel(
                        rs.getInt("client_id"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getString("email_addr"),
                        rs.getString("contact_no"), rs.getString("id_card"),
                        rs.getString("origin"), rs.getString("home_city")
                ));
            }
        } catch (SQLException ex) {
            showStatus("Database load error: " + ex.getMessage(), true);
        }
    }

    private void addGuest() {
        String sql = "INSERT INTO Clients (first_name, last_name, email_addr, contact_no, id_card, origin, home_address, home_city, registered_on) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fFirst.getText().trim());
            stmt.setString(2, fLast.getText().trim());
            stmt.setString(3, fEmail.getText().trim());
            stmt.setString(4, fPhone.getText().trim());
            stmt.setString(5, fCnic.getText().trim());
            stmt.setString(6, fNat.getText().trim());
            stmt.setString(7, fAddr.getText().trim());
            stmt.setString(8, fCity.getText().trim());
            stmt.setDate(9, Date.valueOf(LocalDate.now()));

            stmt.executeUpdate();
            showStatus("Guest added completely!", false);
            loadGuests(""); clearForm();
        } catch (SQLException ex) {
            showStatus("Insertion failed: " + ex.getMessage(), true);
        }
    }

    private void updateGuest() {
        ClientModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showStatus("Select a guest target.", true); return; }

        String sql = "UPDATE Clients SET first_name=?, last_name=?, email_addr=?, contact_no=?, id_card=?, origin=?, home_address=?, home_city=? WHERE client_id=?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fFirst.getText().trim());
            stmt.setString(2, fLast.getText().trim());
            stmt.setString(3, fEmail.getText().trim());
            stmt.setString(4, fPhone.getText().trim());
            stmt.setString(5, fCnic.getText().trim());
            stmt.setString(6, fNat.getText().trim());
            stmt.setString(7, fAddr.getText().trim());
            stmt.setString(8, fCity.getText().trim());
            stmt.setInt(9, selected.getClientId());

            stmt.executeUpdate();
            showStatus("Guest metadata updated!", false);
            loadGuests("");
        } catch (SQLException ex) {
            showStatus("Update conflict: " + ex.getMessage(), true);
        }
    }

    private void deleteGuest() {
        ClientModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showStatus("Select item record.", true); return; }

        String sql = "DELETE FROM Clients WHERE client_id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, selected.getClientId());
            stmt.executeUpdate();
            showStatus("Profile purged successfully.", false);
            loadGuests(""); clearForm();
        } catch (SQLException ex) {
            showStatus("Deletion Blocked: Check dependent reservations.", true);
        }
    }

    private void populateForm(ClientModel m) {
        fFirst.setText(m.getFirstName());
        fLast.setText(m.getLastName());
        fEmail.setText(m.getEmailAddr());
        fPhone.setText(m.getContactNo());
        fCnic.setText(m.getIdCard());
        fNat.setText(m.getOrigin());
        fCity.setText(m.getHomeCity());

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT home_address FROM Clients WHERE client_id = ?")) {
            stmt.setInt(1, m.getClientId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) fAddr.setText(rs.getString("home_address"));
        } catch (SQLException ignored) {}
    }

    private void clearForm() {
        table.getSelectionModel().clearSelection();
        fFirst.clear(); fLast.clear(); fEmail.clear();
        fPhone.clear(); fCnic.clear(); fCity.clear(); fAddr.clear();
        fNat.setText("Pakistani");
    }

    private void viewGuestReservations() {
        ClientModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showStatus("Select a guest profile.", true); return; }
        showStatus("Isolating records for: " + selected.getFirstName(), false);
    }

    private void showStatus(String msg, boolean isError) {
        statusLbl.setText(msg);
        statusLbl.setStyle(isError ? "-fx-text-fill:#E53E3E; -fx-font-size:12px;" : "-fx-text-fill:#38A169; -fx-font-size:12px;");
    }


    private <T> TableColumn<T, String> stringCol(String title, String property, double width) {
        TableColumn<T, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }

    private <T> TableColumn<T, Integer> intCol(String title, String property, double width) {
        TableColumn<T, Integer> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }

    private void addRow(GridPane grid, int row, String labelText, Control inputField) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill:#A0AEC0; -fx-font-weight:bold; -fx-font-size:13px;");
        grid.add(lbl, 0, row);
        grid.add(inputField, 1, row);
    }

    private void styleTable(TableView<?> t) {
        t.setStyle("-fx-background-color:#1A202C; -fx-border-color:#2D3748; -fx-border-radius:5;");
    }
}