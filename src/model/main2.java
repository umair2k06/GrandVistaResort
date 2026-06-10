package model;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.beans.property.*;

import java.sql.*;

public class main2 {

    private TableView<SuiteModel> table = new TableView<>();
    private ObservableList<SuiteModel> data = FXCollections.observableArrayList();

    private TextField fRoomNum, fFloor;
    private ComboBox<String> cbType, cbStatus;
    private CheckBox chkAc, chkWifi, chkTv, chkBalcony;
    private Label statusLbl;

    @SuppressWarnings("unchecked")
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("🏨 Room Inventory Hub");
        stage.initModality(Modality.NONE);

        TableColumn<SuiteModel, Integer> colId    = intCol("ID", "suiteId", 50);
        TableColumn<SuiteModel, String>  colNum   = stringCol("Room #", "doorNumber", 80);
        TableColumn<SuiteModel, Integer> colFloor = intCol("Floor", "floorLevel", 70);
        TableColumn<SuiteModel, String>  colType  = stringCol("Class Type", "tierName", 140);
        TableColumn<SuiteModel, Double>  colPrice = doubleCol("Nightly Rate", "nightlyRate", 110);
        TableColumn<SuiteModel, String>  colStat  = stringCol("Availability", "hkStatus", 110);

        table.getColumns().addAll(colId, colNum, colFloor, colType, colPrice, colStat);
        table.setItems(data);
        loadRooms();


        fRoomNum = Styles.field("e.g., 101");
        fFloor   = Styles.field("e.g., 1");

        cbType = new ComboBox<>();
        cbType.setPrefWidth(200);
        loadRoomTypes();

        cbStatus = new ComboBox<>(FXCollections.observableArrayList("Vacant", "Inhabited", "Service Mode", "Allocated"));
        cbStatus.setValue("Vacant");
        cbStatus.setPrefWidth(200);

        chkAc = new CheckBox("Air Conditioning"); chkAc.setStyle("-fx-text-fill:white;");
        chkWifi = new CheckBox("Free WiFi"); chkWifi.setStyle("-fx-text-fill:white;");
        chkTv = new CheckBox("Smart TV"); chkTv.setStyle("-fx-text-fill:white;");
        chkBalcony = new CheckBox("Terrace Balcony"); chkBalcony.setStyle("-fx-text-fill:white;");

        statusLbl = new Label("");

        Button btnAdd = Styles.goldBtn("➕ Setup Room");
        Button btnUpdate = Styles.outlineBtn("✏ Modify");
        Button btnDelete = Styles.redBtn("🗑 Terminate");

        btnAdd.setOnAction(e -> addRoom());
        btnUpdate.setOnAction(e -> updateRoom());
        btnDelete.setOnAction(e -> deleteRoom());

        table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) populateRoomForm(n);
        });

        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(10);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color:#121820; -fx-background-radius:10; -fx-border-color:#2D3748; -fx-border-radius:10;");

        addRow(form, 0, "Room Number:", fRoomNum);
        addRow(form, 1, "Floor Index:", fFloor);
        addRow(form, 2, "Room Class:", cbType);
        addRow(form, 3, "Initial Status:", cbStatus);

        VBox featureBox = new VBox(8, chkAc, chkWifi, chkTv, chkBalcony);
        featureBox.setPadding(new Insets(6, 0, 6, 0));
        form.add(new Label("Amenities:"), 0, 4);
        form.add(featureBox, 1, 4);

        HBox actions = new HBox(8, btnAdd, btnUpdate, btnDelete);
        form.add(actions, 1, 5);
        form.add(statusLbl, 1, 6);

        HBox mainLayout = new HBox(form, table);
        HBox.setHgrow(table, Priority.ALWAYS);
        mainLayout.setPadding(new Insets(16));
        mainLayout.setStyle("-fx-background-color:#0D1117;");

        Scene scene = new Scene(mainLayout, 1000, 550);
        stage.setScene(scene);
        stage.show();
    }

    private void loadRooms() {
        data.clear();
        String sql = "SELECT r.suite_id, r.door_number, r.floor_level, r.hk_status, rt.tier_name, rt.nightly_rate FROM Suites r JOIN Tier_Category rt ON r.category_id = rt.category_id";
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.add(new SuiteModel(
                        rs.getInt("suite_id"), rs.getString("door_number"),
                        rs.getInt("floor_level"), rs.getString("tier_name"),
                        rs.getDouble("nightly_rate"), rs.getString("hk_status")
                ));
            }
        } catch (SQLException ex) {
            showStatus("Fetch failed: " + ex.getMessage(), true);
        }
    }

    private void loadRoomTypes() {
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT tier_name FROM Tier_Category")) {
            while (rs.next()) cbType.getItems().add(rs.getString("tier_name"));
        } catch (SQLException ignored) {}
    }

    private void addRoom() {
        String sql = "INSERT INTO Suites (door_number, floor_level, category_id, hk_status, climate_ctrl, broadband, smart_tv, veranda) VALUES (?, ?, (SELECT category_id FROM Tier_Category WHERE tier_name=?), ?, ?, ?, ?, ?)";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fRoomNum.getText().trim());
            stmt.setInt(2, Integer.parseInt(fFloor.getText().trim()));
            stmt.setString(3, cbType.getValue());
            stmt.setString(4, cbStatus.getValue());
            stmt.setInt(5, chkAc.isSelected() ? 1 : 0);
            stmt.setInt(6, chkWifi.isSelected() ? 1 : 0);
            stmt.setInt(7, chkTv.isSelected() ? 1 : 0);
            stmt.setInt(8, chkBalcony.isSelected() ? 1 : 0);

            stmt.executeUpdate();
            showStatus("Room set up properly.", false);
            loadRooms();
        } catch (Exception ex) {
            showStatus("Save error: " + ex.getMessage(), true);
        }
    }

    private void updateRoom() {
        SuiteModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String sql = "UPDATE Suites SET door_number=?, floor_level=?, category_id=(SELECT category_id FROM Tier_Category WHERE tier_name=?), hk_status=?, climate_ctrl=?, broadband=?, smart_tv=?, veranda=? WHERE suite_id=?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fRoomNum.getText().trim());
            stmt.setInt(2, Integer.parseInt(fFloor.getText().trim()));
            stmt.setString(3, cbType.getValue());
            stmt.setString(4, cbStatus.getValue());
            stmt.setInt(5, chkAc.isSelected() ? 1 : 0);
            stmt.setInt(6, chkWifi.isSelected() ? 1 : 0);
            stmt.setInt(7, chkTv.isSelected() ? 1 : 0);
            stmt.setInt(8, chkBalcony.isSelected() ? 1 : 0);
            stmt.setInt(9, selected.getSuiteId());

            stmt.executeUpdate();
            showStatus("Room metadata synchronized.", false);
            loadRooms();
        } catch (Exception ex) {
            showStatus("Update failed: " + ex.getMessage(), true);
        }
    }

    private void deleteRoom() {
        SuiteModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Suites WHERE suite_id=?")) {
            stmt.setInt(1, selected.getSuiteId());
            stmt.executeUpdate();
            showStatus("Room deleted successfully.", false);
            loadRooms();
        } catch (SQLException ex) {
            showStatus("Clear operational reservations first.", true);
        }
    }

    private void populateRoomForm(SuiteModel m) {
        fRoomNum.setText(m.getDoorNumber());
        fFloor.setText(String.valueOf(m.getFloorLevel()));
        cbType.setValue(m.getTierName());
        cbStatus.setValue(m.getHkStatus());

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Suites WHERE suite_id=?")) {
            stmt.setInt(1, m.getSuiteId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                chkAc.setSelected(rs.getInt("climate_ctrl") == 1);
                chkWifi.setSelected(rs.getInt("broadband") == 1);
                chkTv.setSelected(rs.getInt("smart_tv") == 1);
                chkBalcony.setSelected(rs.getInt("veranda") == 1);
            }
        } catch (SQLException ignored) {}
    }

    private void showStatus(String msg, boolean isError) {
        statusLbl.setText(msg);
        statusLbl.setStyle(isError ? "-fx-text-fill:#E53E3E;" : "-fx-text-fill:#38A169;");
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

    private <T> TableColumn<T, Double> doubleCol(String title, String property, double width) {
        TableColumn<T, Double> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }

    private void addRow(GridPane grid, int row, String labelText, Control inputField) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill:#A0AEC0; -fx-font-weight:bold;");
        grid.add(lbl, 0, row);
        grid.add(inputField, 1, row);
    }
}