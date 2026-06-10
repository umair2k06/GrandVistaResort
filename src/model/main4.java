package model;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class main4 {

    private final ObservableList<BookingModel> data = FXCollections.observableArrayList();
    private TableView<BookingModel> table;
    private ComboBox<String> fGuest, fRoom, fStaff, fStatus, filterStatus;
    private DatePicker fCheckIn, fCheckOut;
    private TextField fAdults, fChildren;
    private Label statusLbl, amountLbl;

    @SuppressWarnings("unchecked")
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("📋 Reservation Management");

        table = new TableView<>(data);
        table.setStyle("-fx-background-color:#121820; -fx-border-color:#2D3748;");
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<BookingModel,String> colSt = col("Status","status",100);
        colSt.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setStyle(""); return; }
                setText(s);
                setStyle(switch(s) {
                    case "Approved"   -> "-fx-text-fill:#3182CE; -fx-font-weight:bold;";
                    case "Active"     -> "-fx-text-fill:#38A169; -fx-font-weight:bold;";
                    case "Completed"  -> "-fx-text-fill:#A0AEC0; -fx-font-weight:bold;";
                    case "Revoked"    -> "-fx-text-fill:#E53E3E; -fx-font-weight:bold;";
                    default           -> "";
                });
            }
        });

        table.getColumns().addAll(
                col("ID","resId",50), col("Guest","guestName",140),
                col("Room","roomNumber",65), col("Type","roomType",130),
                col("Check-In","checkIn",95), col("Check-Out","checkOut",95),
                col("Nights","nights",60), colSt,
                col("Amount","amount",95), col("Staff","handledBy",120)
        );
        loadReservations("All");


        filterStatus = new ComboBox<>(FXCollections.observableArrayList(
                "All","Approved","Active","Completed","Revoked"));
        filterStatus.setValue("All");
        filterStatus.setStyle(Styles.FIELD);
        filterStatus.setOnAction(e -> loadReservations(filterStatus.getValue()));
        Button btnRefresh = Styles.outlineBtn("⟳ Refresh");
        btnRefresh.setOnAction(e -> loadReservations(filterStatus.getValue()));
        HBox filterBar = new HBox(10, Styles.fieldLabel("Filter:"), filterStatus, btnRefresh);
        filterBar.setAlignment(Pos.CENTER_LEFT);


        fGuest = styledCombo(); fRoom = styledCombo(); fStaff = styledCombo();
        fStatus = new ComboBox<>(FXCollections.observableArrayList(
                "Approved","Active","Completed","Revoked"));
        fStatus.setValue("Approved"); fStatus.setStyle(Styles.FIELD); fStatus.setMaxWidth(Double.MAX_VALUE);

        loadGuestsCombo(); loadRoomsCombo(); loadStaffCombo();

        fCheckIn  = datePicker(LocalDate.now());
        fCheckOut = datePicker(LocalDate.now().plusDays(1));
        fAdults   = Styles.field("1"); fAdults.setText("1");
        fChildren = Styles.field("0"); fChildren.setText("0");

        amountLbl = new Label("Estimated: PKR 0");
        amountLbl.setStyle("-fx-text-fill:#FC8181; -fx-font-weight:bold; -fx-font-size:13px;");
        statusLbl = new Label(""); statusLbl.setStyle("-fx-text-fill:#38A169; -fx-font-size:12px;");


        Runnable calc = () -> {
            if (fRoom.getValue() == null || fCheckIn.getValue() == null || fCheckOut.getValue() == null) return;
            long n = ChronoUnit.DAYS.between(fCheckIn.getValue(), fCheckOut.getValue());
            if (n <= 0) { amountLbl.setText("Invalid dates!"); return; }
            try {
                String rNum = fRoom.getValue().contains("|") ? fRoom.getValue().split("\\|")[1].trim() : fRoom.getValue();
                PreparedStatement ps = DBconnection.getConnection().prepareStatement(
                        "SELECT rt.nightly_rate FROM Suites r JOIN Tier_Category rt ON r.category_id=rt.category_id WHERE r.door_number=?");
                ps.setString(1, rNum);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) amountLbl.setText(String.format("Estimated (%d nights): PKR %.0f", n, rs.getDouble(1)*n));
            } catch (Exception ignored) {}
        };
        fRoom.setOnAction(e -> calc.run());
        fCheckIn.setOnAction(e -> calc.run());
        fCheckOut.setOnAction(e -> calc.run());

        Button btnBook   = Styles.goldBtn("➕ Make Booking");
        Button btnCancelRes = Styles.redBtn("✖ Cancel Reservation");
        btnBook.setOnAction(e -> makeReservation());
        btnCancelRes.setOnAction(e -> cancelSelected());


        Button btnCheckIn  = Styles.greenBtn("✅ Check-In");
        Button btnCheckOut = Styles.outlineBtn("🚪 Check-Out");
        btnCheckIn.setOnAction(e  -> updateStatus("Active",    "Approved"));
        btnCheckOut.setOnAction(e -> updateStatus("Completed", "Active"));

        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(10); form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color:#121820; -fx-background-radius:10; -fx-border-color:#2D3748; -fx-border-radius:10;");
        form.setPrefWidth(360);

        addRow(form, 0, "Guest:",     fGuest);
        addRow(form, 1, "Room:",      fRoom);
        addRow(form, 2, "Check-In:",  fCheckIn);
        addRow(form, 3, "Check-Out:", fCheckOut);
        addRow(form, 4, "Adults:",    fAdults);
        addRow(form, 5, "Children:",  fChildren);
        addRow(form, 6, "Staff:",     fStaff);
        form.add(amountLbl, 1, 7);
        form.add(new HBox(8, btnBook, btnCancelRes), 1, 8);
        form.add(new HBox(8, btnCheckIn, btnCheckOut), 1, 9);
        form.add(statusLbl, 1, 10);

        VBox left = new VBox(10, filterBar, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox root = new HBox(20, left, form);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#0B0F14;");
        HBox.setHgrow(left, Priority.ALWAYS);

        stage.setScene(new Scene(root, 1200, 640));
        stage.show();
    }

    private void loadReservations(String filter) {
        data.clear();
        String sql = "SELECT r.booking_id, CONCAT(g.first_name,' ',g.last_name), rm.door_number, " +
                "rt.tier_name, r.check_in, r.check_out, DATEDIFF(r.check_out,r.check_in), " +
                "r.book_status, r.grand_total, COALESCE(CONCAT(s.first_name,' ',s.last_name),'—') " +
                "FROM Bookings r " +
                "JOIN Clients g ON r.client_id=g.client_id " +
                "JOIN Suites rm ON r.suite_id=rm.suite_id " +
                "JOIN Tier_Category rt ON rm.category_id=rt.category_id " +
                "LEFT JOIN Employees s ON r.employee_id=s.employee_id";
        if (!"All".equals(filter)) sql += " WHERE r.book_status='" + filter + "'";
        sql += " ORDER BY r.booking_id DESC";
        try (Connection con = DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                data.add(new BookingModel(rs.getInt(1),rs.getString(2),rs.getString(3),
                        rs.getString(4),rs.getString(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getDouble(9),rs.getString(10)));
        } catch (Exception e) { flash("Load error: " + e.getMessage(), true); }
    }

    private void makeReservation() {
        if (fGuest.getValue()==null || fRoom.getValue()==null || fStaff.getValue()==null) {
            flash("Please select Guest, Room and Staff.", true); return;
        }
        try (Connection con = DBconnection.getConnection()) {
            int gId = extractId(fGuest.getValue());
            int sId = extractId(fStaff.getValue());
            String rNum = fRoom.getValue().contains("|") ? fRoom.getValue().split("\\|")[1].trim() : fRoom.getValue();

            // Get suite_id
            PreparedStatement rmPs = con.prepareStatement(
                    "SELECT suite_id FROM Suites WHERE door_number=? AND hk_status='Vacant'");
            rmPs.setString(1, rNum);
            ResultSet rmRs = rmPs.executeQuery();
            if (!rmRs.next()) { flash("Room is not available!", true); return; }
            int roomId = rmRs.getInt(1);

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Bookings (client_id,suite_id,employee_id,check_in,check_out,adults,children,book_status,created_at) " +
                            "VALUES(?,?,?,?,?,?,?,'Approved',CURDATE())");
            ps.setInt(1, gId); ps.setInt(2, roomId); ps.setInt(3, sId);
            ps.setDate(4, Date.valueOf(fCheckIn.getValue()));
            ps.setDate(5, Date.valueOf(fCheckOut.getValue()));
            ps.setInt(6, Integer.parseInt(fAdults.getText().trim()));
            ps.setInt(7, Integer.parseInt(fChildren.getText().trim()));
            ps.executeUpdate();
            flash("✔ Reservation confirmed! Room auto-set to Reserved.", false);
            loadReservations("All"); loadRoomsCombo();
        } catch (Exception e) { flash("Error: " + e.getMessage(), true); }
    }

    private void updateStatus(String newStatus, String requiredStatus) {
        BookingModel sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { flash("Select a reservation first.", true); return; }
        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Bookings SET book_status=? WHERE booking_id=? AND book_status=?")) {
            ps.setString(1, newStatus); ps.setInt(2, sel.getResId()); ps.setString(3, requiredStatus);
            int rows = ps.executeUpdate();
            if (rows > 0) { flash("✔ Status updated to: " + newStatus, false); loadReservations(filterStatus.getValue()); }
            else flash("Cannot update — check current status.", true);
        } catch (Exception e) { flash("Error: " + e.getMessage(), true); }
    }

    private void cancelSelected() {
        BookingModel sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { flash("Select a reservation first.", true); return; }
        updateStatus("Revoked", "Approved");
    }

    private void loadGuestsCombo() {
        fGuest.getItems().clear();
        try (Connection con = DBconnection.getConnection();
             ResultSet rs = con.createStatement().executeQuery(
                     "SELECT client_id, CONCAT(first_name,' ',last_name) FROM Clients ORDER BY first_name")) {
            while (rs.next()) fGuest.getItems().add(rs.getInt(1) + " | " + rs.getString(2));
        } catch (Exception e) { System.out.println(e.getMessage()); }
    }

    private void loadRoomsCombo() {
        fRoom.getItems().clear();
        try (Connection con = DBconnection.getConnection();
             ResultSet rs = con.createStatement().executeQuery(
                     "SELECT door_number FROM Suites WHERE hk_status='Vacant' ORDER BY door_number")) {
            while (rs.next()) fRoom.getItems().add(rs.getString(1));
        } catch (Exception e) { System.out.println(e.getMessage()); }
    }

    private void loadStaffCombo() {
        fStaff.getItems().clear();
        try (Connection con = DBconnection.getConnection();
             ResultSet rs = con.createStatement().executeQuery(
                     "SELECT employee_id, CONCAT(first_name,' ',last_name), role FROM Employees ORDER BY first_name")) {
            while (rs.next())
                fStaff.getItems().add(rs.getInt(1) + " | " + rs.getString(2) + " (" + rs.getString(3) + ")");
        } catch (Exception e) { System.out.println(e.getMessage()); }
    }

    private int extractId(String val) {
        if (val == null) return 0;
        try { return Integer.parseInt(val.split("\\|")[0].trim()); } catch (Exception e) { return 0; }
    }

    private ComboBox<String> styledCombo() {
        ComboBox<String> cb = new ComboBox<>();
        cb.setStyle(Styles.FIELD); cb.setMaxWidth(Double.MAX_VALUE); return cb;
    }

    private DatePicker datePicker(LocalDate val) {
        DatePicker dp = new DatePicker(val);
        dp.setStyle(Styles.FIELD); dp.setMaxWidth(Double.MAX_VALUE); return dp;
    }

    private <T> TableColumn<BookingModel,T> col(String title, String prop, int width) {
        TableColumn<BookingModel,T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width); return c;
    }

    private void addRow(GridPane g, int row, String lbl, javafx.scene.Node field) {
        Label l = new Label(lbl); l.setStyle("-fx-text-fill:#A0AEC0; -fx-font-weight:bold;");
        g.add(l, 0, row); g.add(field, 1, row); GridPane.setHgrow(field, Priority.ALWAYS);
    }

    private void flash(String msg, boolean error) {
        statusLbl.setText(msg);
        statusLbl.setStyle("-fx-text-fill:" + (error?"#E53E3E":"#38A169") + "; -fx-font-size:12px;");
    }
}