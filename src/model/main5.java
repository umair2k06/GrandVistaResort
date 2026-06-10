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
import java.time.LocalDate;


public class main5 {


    private final ObservableList<TransactionModel> txData = FXCollections.observableArrayList();
    private TableView<TransactionModel> txTable;
    private ComboBox<String> fBookingId, fPayMethod, fTxStatus;
    private TextField fAmount, fTxRef;
    private DatePicker fTxDate;
    private Label txStatusLbl;


    private final ObservableList<EmployeeModel> empData = FXCollections.observableArrayList();
    private TableView<EmployeeModel> empTable;
    private TextField efFirst, efLast, efEmail, efPhone, efSal;
    private ComboBox<String> efRole, efShift;
    private DatePicker efJoining;
    private Label empStatusLbl;


    @SuppressWarnings("unchecked")
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("💳 Financial Transactions");
        stage.initModality(Modality.NONE);


        txTable = new TableView<>(txData);
        styleTable(txTable);

        TableColumn<TransactionModel,Integer> colTid  = tcol("Tx#",        "transactionId",     55);
        TableColumn<TransactionModel,Integer> colBid  = tcol("Book#",      "bookingId",       55);
        TableColumn<TransactionModel,String>  colCu  = tcol("Customer",   "customerName",   150);
        TableColumn<TransactionModel,String>  colRm  = tcol("Room",       "roomNo",         70);
        TableColumn<TransactionModel,Double>  colTot  = tcol("Book Total",  "bookingTotal",   100);
        TableColumn<TransactionModel,Double>  colPaid = tcol("Paid",       "amountPaid",     100);
        TableColumn<TransactionModel,String>  colMth  = tcol("Method",     "paymentMethod",  110);
        TableColumn<TransactionModel,String>  colDt   = tcol("Date",       "transactionDate", 100);
        TableColumn<TransactionModel,String>  colSt   = tcol("Status",     "transactionStatus", 100);
        TableColumn<TransactionModel,String>  colTx   = tcol("Tx Ref",     "transactionRef",  140);

        colSt.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setStyle(""); return; }
                setText(s);
                setStyle(switch (s) {
                    case "Completed" -> "-fx-text-fill:#38A169; -fx-font-weight:bold;";
                    case "Pending"   -> "-fx-text-fill:#D69E2E; -fx-font-weight:bold;";
                    case "Refunded"  -> "-fx-text-fill:#3182CE; -fx-font-weight:bold;";
                    case "Failed"    -> "-fx-text-fill:#E53E3E; -fx-font-weight:bold;";
                    default          -> "";
                });
            }
        });
        txTable.getColumns().addAll(colTid,colBid,colCu,colRm,colTot,colPaid,colMth,colDt,colSt,colTx);

        loadTransactions();

        Label summaryLbl = new Label();
        summaryLbl.setStyle("-fx-text-fill:#D69E2E; -fx-font-size:13px; -fx-font-weight:bold;");
        updateSummary(summaryLbl);

        Button btnRef = Styles.outlineBtn("⟳ Refresh");
        btnRef.setOnAction(e -> { loadTransactions(); updateSummary(summaryLbl); });

        HBox topBar = new HBox(16, summaryLbl, btnRef);
        topBar.setAlignment(Pos.CENTER_LEFT);


        fBookingId = styledCombo();
        populateBookings();

        fPayMethod = new ComboBox<>(FXCollections.observableArrayList(
                "Cash","Credit Card","Debit Card","Bank Transfer","Online"));
        fPayMethod.setValue("Cash");
        fPayMethod.setStyle(Styles.FIELD);
        fPayMethod.setMaxWidth(Double.MAX_VALUE);

        fTxStatus = new ComboBox<>(FXCollections.observableArrayList(
                "Completed","Pending","Refunded","Failed"));
        fTxStatus.setValue("Completed");
        fTxStatus.setStyle(Styles.FIELD);
        fTxStatus.setMaxWidth(Double.MAX_VALUE);

        fAmount  = Styles.field("Amount paid");
        fTxRef   = Styles.field("Transaction ref (optional)");
        fTxDate  = styledDatePicker();
        fTxDate.setValue(LocalDate.now());

        txStatusLbl = new Label("");
        txStatusLbl.setStyle("-fx-text-fill:#38A169; -fx-font-size:12px;");

        fBookingId.setOnAction(e -> {
            if (fBookingId.getValue() == null) return;
            try {
                int bid = extractId(fBookingId.getValue());
                PreparedStatement ps = DBconnection.getConnection()
                        .prepareStatement("SELECT total_amount FROM Booking WHERE booking_id=?");
                ps.setInt(1, bid);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) fAmount.setText(String.format("%.0f", rs.getDouble(1)));
            } catch (Exception ignored) {}
        });

        Button btnAdd    = Styles.goldBtn("➕ Record Payment");
        Button btnUpdate = Styles.outlineBtn("✏ Update");
        Button btnDelete = Styles.redBtn("🗑 Delete");
        Button btnClear  = Styles.outlineBtn("✖ Clear");

        btnAdd.setOnAction(e    -> addTransaction(summaryLbl));
        btnUpdate.setOnAction(e -> updateTransaction(summaryLbl));
        btnDelete.setOnAction(e -> deleteTransaction(summaryLbl));
        btnClear.setOnAction(e  -> clearTxForm());

        txTable.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
            if (n != null) populateTxForm(n);
        });

        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(10);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color:#121820; -fx-background-radius:10; " +
                "-fx-border-color:#2D3748; -fx-border-radius:10;");
        form.setPrefWidth(330);

        addRow(form, 0, "Booking:",     fBookingId);
        addRow(form, 1, "Amount:",      fAmount);
        addRow(form, 2, "Method:",      fPayMethod);
        addRow(form, 3, "Date:",        fTxDate);
        addRow(form, 4, "Status:",      fTxStatus);
        addRow(form, 5, "Tx Ref:",      fTxRef);

        form.add(new HBox(8, btnAdd, btnUpdate),    1, 6);
        form.add(new HBox(8, btnDelete, btnClear),  1, 7);
        form.add(txStatusLbl,                          1, 8);

        VBox left = new VBox(12, topBar, txTable);
        VBox.setVgrow(txTable, Priority.ALWAYS);

        VBox right = new VBox(10, Styles.sectionTitle("Record Payment"), form);
        right.setPrefWidth(350);
        right.setStyle("-fx-background-color:#0B0F14; -fx-padding:5;");

        HBox root = new HBox(20, left, right);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#0B0F14;");
        HBox.setHgrow(left, Priority.ALWAYS);

        stage.setScene(new Scene(root, 1160, 600));
        stage.show();
    }

    @SuppressWarnings("unchecked")
    public void showStaff() {
        Stage stage = new Stage();
        stage.setTitle("👔 Employee Management");
        stage.initModality(Modality.NONE);


        empTable = new TableView<>(empData);
        styleTable(empTable);

        TableColumn<EmployeeModel,Integer> cId  = ecol("ID",        "employeeId",   50);
        TableColumn<EmployeeModel,String>  cFN  = ecol("First",     "firstName", 100);
        TableColumn<EmployeeModel,String>  cLN  = ecol("Last",      "lastName",  100);
        TableColumn<EmployeeModel,String>  cRl  = ecol("Role",      "jobRole",   120);
        TableColumn<EmployeeModel,String>  cEm  = ecol("Email",     "email",     190);
        TableColumn<EmployeeModel,String>  cPh  = ecol("Phone",     "phone",     120);
        TableColumn<EmployeeModel,Double>  cSal = ecol("Salary",    "salary",    110);
        TableColumn<EmployeeModel,String>  cSh  = ecol("Shift",     "workShift",  80);
        TableColumn<EmployeeModel,String>  cHd  = ecol("Join Date", "joiningDate", 100);


        cRl.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setStyle(""); return; }
                setText(s);
                setStyle(switch (s) {
                    case "Manager"      -> "-fx-text-fill:#D69E2E; -fx-font-weight:bold;";
                    case "Receptionist" -> "-fx-text-fill:#3182CE; -fx-font-weight:bold;";
                    case "Chef"         -> "-fx-text-fill:#ED8936; -fx-font-weight:bold;";
                    case "Security"     -> "-fx-text-fill:#E53E3E; -fx-font-weight:bold;";
                    case "Housekeeping" -> "-fx-text-fill:#38A169; -fx-font-weight:bold;";
                    default             -> "";
                });
            }
        });

        empTable.getColumns().addAll(cId, cFN, cLN, cRl, cEm, cPh, cSal, cSh, cHd);


        loadEmployees();


        efFirst = Styles.field("First name");
        efLast  = Styles.field("Last name");
        efEmail = Styles.field("email@hotel.pk");
        efPhone = Styles.field("0300-XXXXXXX");
        efSal   = Styles.field("e.g. 55000");
        efJoining = styledDatePicker();
        efJoining.setValue(LocalDate.now());

        efRole = new ComboBox<>(FXCollections.observableArrayList(
                "Manager","Receptionist","Housekeeping","Security","Chef"));
        efRole.setValue("Receptionist");
        efRole.setStyle(Styles.FIELD);
        efRole.setMaxWidth(Double.MAX_VALUE);

        efShift = new ComboBox<>(FXCollections.observableArrayList("Morning","Evening","Night"));
        efShift.setValue("Morning");
        efShift.setStyle(Styles.FIELD);
        efShift.setMaxWidth(Double.MAX_VALUE);

        empStatusLbl = new Label("");
        empStatusLbl.setStyle("-fx-text-fill:#38A169; -fx-font-size:12px;");


        empTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n == null) return;
            efFirst.setText(n.getFirstName());
            efLast.setText(n.getLastName());
            efEmail.setText(n.getEmail());
            efPhone.setText(n.getPhone());
            efSal.setText(String.format("%.0f", n.getSalary()));
            efRole.setValue(n.getJobRole());
            efShift.setValue(n.getWorkShift());
            try { efJoining.setValue(LocalDate.parse(n.getJoiningDate())); } catch (Exception ignored) {}
        });

        Button sBtnAdd    = Styles.goldBtn("➕ Add Employee");
        Button sBtnUpdate = Styles.outlineBtn("✏ Update");
        Button sBtnDelete = Styles.redBtn("🗑 Delete");
        Button sBtnClear  = Styles.outlineBtn("✖ Clear");
        Button sBtnRefresh = Styles.outlineBtn("⟳ Refresh");

        sBtnAdd.setOnAction(e    -> addEmployee());
        sBtnUpdate.setOnAction(e -> updateEmployee());
        sBtnDelete.setOnAction(e -> deleteEmployee());
        sBtnClear.setOnAction(e  -> clearEmpForm());
        sBtnRefresh.setOnAction(e -> loadEmployees());

        GridPane sForm = new GridPane();
        sForm.setHgap(12); sForm.setVgap(10);
        sForm.setPadding(new Insets(16));
        sForm.setStyle("-fx-background-color:#121820; -fx-background-radius:10; " +
                "-fx-border-color:#2D3748; -fx-border-radius:10;");
        sForm.setPrefWidth(330);

        addRow(sForm, 0, "First Name:", efFirst);
        addRow(sForm, 1, "Last Name:",  efLast);
        addRow(sForm, 2, "Role:",       efRole);
        addRow(sForm, 3, "Email:",      efEmail);
        addRow(sForm, 4, "Phone:",      efPhone);
        addRow(sForm, 5, "Salary:",     efSal);
        addRow(sForm, 6, "Shift:",      efShift);
        addRow(sForm, 7, "Join Date:",  efJoining);

        sForm.add(new HBox(8, sBtnAdd, sBtnUpdate),    1, 8);
        sForm.add(new HBox(8, sBtnDelete, sBtnClear),  1, 9);
        sForm.add(empStatusLbl,                           1, 10);

        VBox sLeft = new VBox(10, sBtnRefresh, empTable);
        VBox.setVgrow(empTable, Priority.ALWAYS);

        VBox sRight = new VBox(10, Styles.sectionTitle("Employee Details"), sForm);
        sRight.setPrefWidth(350);
        sRight.setStyle("-fx-background-color:#0B0F14; -fx-padding:5;");

        HBox sRoot = new HBox(20, sLeft, sRight);
        sRoot.setPadding(new Insets(20));
        sRoot.setStyle("-fx-background-color:#0B0F14;");
        HBox.setHgrow(sLeft, Priority.ALWAYS);

        stage.setScene(new Scene(sRoot, 1180, 600));
        stage.show();
    }


    private void loadEmployees() {
        empData.clear();
        String sql = "SELECT employee_id, first_name, last_name, job_role, email, phone, " +
                "salary, work_shift, joining_date FROM Employee ORDER BY job_role, first_name";
        try (Connection con = DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                empData.add(new EmployeeModel(
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
            System.out.println("Employees loaded: " + empData.size() + " records");
        } catch (SQLException e) {
            System.out.println("Employee load error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addEmployee() {
        if (efFirst.getText().isBlank() || efSal.getText().isBlank() || efEmail.getText().isBlank()) {
            flashEmp("Please fill all required fields.", true); return;
        }
        String sql = "INSERT INTO Employee (first_name,last_name,job_role,email,phone,joining_date,salary,work_shift) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, efFirst.getText().trim());
            ps.setString(2, efLast.getText().trim());
            ps.setString(3, efRole.getValue());
            ps.setString(4, efEmail.getText().trim());
            ps.setString(5, efPhone.getText().trim());
            ps.setDate(6, Date.valueOf(efJoining.getValue()));
            ps.setDouble(7, Double.parseDouble(efSal.getText().trim()));
            ps.setString(8, efShift.getValue());
            ps.executeUpdate();
            flashEmp("✔ Employee added successfully!", false);
            loadEmployees();
            clearEmpForm();
        } catch (Exception ex) {
            flashEmp("Error: " + ex.getMessage(), true);
        }
    }

    private void updateEmployee() {
        EmployeeModel sel = empTable.getSelectionModel().getSelectedItem();
        if (sel == null) { flashEmp("Select an employee first.", true); return; }
        String sql = "UPDATE Employee SET first_name=?,last_name=?,job_role=?,email=?,phone=?,joining_date=?,salary=?,work_shift=? WHERE employee_id=?";
        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, efFirst.getText().trim());
            ps.setString(2, efLast.getText().trim());
            ps.setString(3, efRole.getValue());
            ps.setString(4, efEmail.getText().trim());
            ps.setString(5, efPhone.getText().trim());
            ps.setDate(6, Date.valueOf(efJoining.getValue()));
            ps.setDouble(7, Double.parseDouble(efSal.getText().trim()));
            ps.setString(8, efShift.getValue());
            ps.setInt(9, sel.getEmployeeId());
            ps.executeUpdate();
            flashEmp("✔ Employee updated!", false);
            loadEmployees();
        } catch (Exception ex) {
            flashEmp("Error: " + ex.getMessage(), true);
        }
    }

    private void deleteEmployee() {
        EmployeeModel sel = empTable.getSelectionModel().getSelectedItem();
        if (sel == null) { flashEmp("Select an employee first.", true); return; }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + sel.getFirstName() + " " + sel.getLastName() + "?",
                ButtonType.YES, ButtonType.NO);
        conf.setTitle("Confirm Delete");
        conf.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try (Connection con = DBconnection.getConnection();
                     PreparedStatement ps = con.prepareStatement("DELETE FROM Employee WHERE employee_id=?")) {
                    ps.setInt(1, sel.getEmployeeId());
                    ps.executeUpdate();
                    flashEmp("✔ Employee deleted.", false);
                    loadEmployees();
                    clearEmpForm();
                } catch (SQLException ex) {
                    flashEmp("Cannot delete: employee has linked entity dependencies.", true);
                }
            }
        });
    }

    private void clearEmpForm() {
        efFirst.clear(); efLast.clear(); efEmail.clear();
        efPhone.clear(); efSal.clear();
        efRole.setValue("Receptionist");
        efShift.setValue("Morning");
        efJoining.setValue(LocalDate.now());
        empStatusLbl.setText("");
        if (empTable != null) empTable.getSelectionModel().clearSelection();
    }

    private void flashEmp(String msg, boolean error) {
        empStatusLbl.setText(msg);
        empStatusLbl.setStyle("-fx-text-fill:" + (error ? "#E53E3E" : "#38A169") + "; -fx-font-size:12px;");
    }


    void loadTransactions() {
        txData.clear();
        String sql = "SELECT payment_id, booking_id, customer_name, room_number, " +
                "booking_total, amount_paid, payment_method, payment_date, " +
                "payment_status, COALESCE(transaction_reference,'') " +
                "FROM vw_Financial_Transaction_Summary ORDER BY payment_id DESC";
        try (Connection con = DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                txData.add(new TransactionModel(
                        rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
                        rs.getDouble(5), rs.getDouble(6), rs.getString(7),
                        rs.getString(8), rs.getString(9), rs.getString(10)
                ));
            }
            System.out.println("Transactions loaded: " + txData.size() + " records");
        } catch (SQLException e) {
            System.out.println("Transaction load error: " + e.getMessage());
        }
    }

    private void addTransaction(Label summaryLbl) {
        if (fBookingId.getValue() == null || fAmount.getText().isBlank()) {
            flash("Booking and amount are required.", true);
            return;
        }


        int selectedBookingId = extractId(fBookingId.getValue());
        if (selectedBookingId == 0) {
            flash("Error: Invalid Booking Selection ID format.", true);
            return;
        }


        String sql = "INSERT INTO Ledger (booking_id, amount_paid, payment_date, payment_method, payment_status, transaction_reference) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, selectedBookingId);
            ps.setDouble(2, Double.parseDouble(fAmount.getText().trim()));
            ps.setDate(3, Date.valueOf(fTxDate.getValue()));
            ps.setString(4, fPayMethod.getValue());
            ps.setString(5, fTxStatus.getValue());
            ps.setString(6, fTxRef.getText().isBlank() ? null : fTxRef.getText().trim());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                flash("✔ Ledger Transaction recorded successfully!", false);
                loadTransactions();
                updateSummary(summaryLbl);
                clearTxForm();
            } else {
                flash("Error: Transaction could not be written to Ledger.", true);
            }
        } catch (SQLException e) {

            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            flash("Database Error: " + e.getMessage(), true);
        }
    }
    private void updateTransaction(Label summaryLbl) {
        TransactionModel sel = txTable.getSelectionModel().getSelectedItem();
        if (sel == null) { flash("Select a transaction record first.", true); return; }
        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Payment SET amount_paid=?,payment_date=?,payment_method=?,payment_status=?,transaction_reference=? WHERE payment_id=?")) {
            ps.setDouble(1, Double.parseDouble(fAmount.getText().trim()));
            ps.setDate(2, Date.valueOf(fTxDate.getValue()));
            ps.setString(3, fPayMethod.getValue());
            ps.setString(4, fTxStatus.getValue());
            ps.setString(5, fTxRef.getText().isBlank() ? null : fTxRef.getText().trim());
            ps.setInt(6, sel.getTransactionId());
            ps.executeUpdate();
            flash("✔ Transaction updated!", false);
            loadTransactions(); updateSummary(summaryLbl);
        } catch (Exception e) { flash("Error: " + e.getMessage(), true); }
    }

    private void deleteTransaction(Label summaryLbl) {
        TransactionModel sel = txTable.getSelectionModel().getSelectedItem();
        if (sel == null) { flash("Select a transaction record first.", true); return; }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete transaction #" + sel.getTransactionId() + "?", ButtonType.YES, ButtonType.NO);
        conf.setTitle("Confirm Delete");
        conf.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try (Connection con = DBconnection.getConnection();
                     PreparedStatement ps = con.prepareStatement("DELETE FROM Payment WHERE payment_id=?")) {
                    ps.setInt(1, sel.getTransactionId());
                    ps.executeUpdate();
                    flash("✔ Transaction deleted.", false);
                    loadTransactions(); updateSummary(summaryLbl); clearTxForm();
                } catch (SQLException e) { flash("Error: " + e.getMessage(), true); }
            }
        });
    }

    private void updateSummary(Label lbl) {
        try (Connection con = DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT COUNT(*), SUM(amount_paid), SUM(booking_total) " +
                             "FROM vw_Financial_Transaction_Summary WHERE payment_status='Completed'")) {
            if (rs.next())
                lbl.setText(String.format(
                        "%d Completed  |  Revenue: PKR %.0f  |  Projected: PKR %.0f",
                        rs.getInt(1), rs.getDouble(2), rs.getDouble(3)));
        } catch (Exception e) { lbl.setText(""); }
    }

    private void populateBookings() {
        fBookingId.getItems().clear();
        String sql = "SELECT b.booking_id, CONCAT(c.first_name,' ',c.last_name), rm.room_number " +
                "FROM Booking b JOIN Customer c ON b.customer_id=c.customer_id " +
                "JOIN Room rm ON b.room_id=rm.room_id " +
                "WHERE b.booking_status IN ('Confirmed','Checked-In') ORDER BY b.booking_id DESC";
        try (Connection con = DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                fBookingId.getItems().add(rs.getInt(1) + " – " + rs.getString(2) + " (Rm " + rs.getString(3) + ")");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void populateTxForm(TransactionModel p) {
        fBookingId.getItems().stream()
                .filter(s -> s.startsWith(p.getBookingId() + " – "))
                .findFirst().ifPresent(fBookingId::setValue);
        fAmount.setText(String.format("%.0f", p.getAmountPaid()));
        fPayMethod.setValue(p.getPaymentMethod());
        fTxStatus.setValue(p.getTransactionStatus());
        fTxRef.setText(p.getTransactionRef());
        try { fTxDate.setValue(LocalDate.parse(p.getTransactionDate())); } catch (Exception ignored) {}
    }

    private void clearTxForm() {
        fBookingId.setValue(null); fAmount.clear();
        fPayMethod.setValue("Cash"); fTxStatus.setValue("Completed");
        fTxRef.clear(); fTxDate.setValue(LocalDate.now());
        txStatusLbl.setText("");
        if (txTable != null) txTable.getSelectionModel().clearSelection();
    }

    private void flash(String msg, boolean error) {
        txStatusLbl.setText(msg);
        txStatusLbl.setStyle("-fx-text-fill:" + (error ? "#E53E3E" : "#38A169") + "; -fx-font-size:12px;");
    }


    private int extractId(String val) {
        if (val == null || !val.contains(" – ")) return 0;
        return Integer.parseInt(val.split(" – ")[0].trim());
    }

    private ComboBox<String> styledCombo() {
        ComboBox<String> cb = new ComboBox<>();
        cb.setStyle(Styles.FIELD);
        cb.setMaxWidth(Double.MAX_VALUE);
        return cb;
    }

    private DatePicker styledDatePicker() {
        DatePicker dp = new DatePicker();
        dp.setStyle(Styles.FIELD);
        dp.setMaxWidth(Double.MAX_VALUE);
        return dp;
    }

    private <T> TableColumn<TransactionModel,T> tcol(String title, String prop, int width) {
        TableColumn<TransactionModel,T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private <T> TableColumn<EmployeeModel,T> ecol(String title, String prop, int width) {
        TableColumn<EmployeeModel,T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void addRow(GridPane g, int row, String lbl, javafx.scene.Node field) {
        Label label = new Label(lbl);
        label.setStyle("-fx-text-fill:#A0AEC0; -fx-font-weight:bold;");
        g.add(label, 0, row);
        g.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    private void styleTable(TableView<?> tv) {
        tv.setStyle("-fx-background-color:#121820; -fx-border-color:#2D3748; " +
                "-fx-border-radius:8; -fx-background-radius:8;");
        tv.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }
}