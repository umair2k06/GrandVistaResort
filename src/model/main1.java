package model;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


class Styles {
    static final String BG        = "#08100D";
    static final String SURFACE   = "#0F1A16";
    static final String CARD      = "#162620";
    static final String ACCENT    = "#38A169";
    static final String ACCENT2   = "#68D391";
    static final String TEXT      = "#F0F5F3";
    static final String SUBTEXT   = "#8A9A94";
    static final String RED       = "#E53E3E";
    static final String GREEN     = "#38A169";

    static final String BTN_GOLD =
            "-fx-background-color:#38A169; -fx-text-fill:#F0F5F3; " +
                    "-fx-font-weight:bold; -fx-font-size:13px; -fx-cursor:hand; " +
                    "-fx-background-radius:6; -fx-padding:9 20;";

    static final String BTN_OUTLINE =
            "-fx-background-color:transparent; -fx-text-fill:#38A169; " +
                    "-fx-border-color:#38A169; -fx-border-radius:6; -fx-background-radius:6; " +
                    "-fx-font-size:12px; -fx-cursor:hand; -fx-padding:7 16;";

    static final String BTN_RED =
            "-fx-background-color:#631717; -fx-text-fill:#FFEAEA; " +
                    "-fx-font-weight:bold; -fx-font-size:12px; -fx-cursor:hand; " +
                    "-fx-background-radius:6; -fx-padding:7 16;";

    static final String BTN_GREEN =
            "-fx-background-color:#143A24; -fx-text-fill:#C6F6D5; " +
                    "-fx-font-weight:bold; -fx-font-size:12px; -fx-cursor:hand; " +
                    "-fx-background-radius:6; -fx-padding:7 16;";

    static final String FIELD =
            "-fx-background-color:#08100D; -fx-text-fill:#F0F5F3; " +
                    "-fx-border-color:#1F3A30; -fx-border-radius:6; -fx-background-radius:6; " +
                    "-fx-prompt-text-fill:#3B5449; -fx-font-size:13px; -fx-padding:8 10;";

    static final String LABEL =
            "-fx-text-fill:#8A9A94; -fx-font-size:12px; -fx-font-weight:600;";

    static Label sectionTitle(String txt) {
        Label l = new Label(txt);
        l.setStyle("-fx-text-fill:#38A169; -fx-font-size:16px; -fx-font-weight:bold;");
        return l;
    }

    static Label fieldLabel(String txt) {
        Label l = new Label(txt);
        l.setStyle(LABEL);
        return l;
    }

    static TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(FIELD);
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                tf.setStyle(FIELD + "-fx-border-color: #38A169; -fx-background-color: #0F1A16;");
            } else {
                tf.setStyle(FIELD);
            }
        });
        return tf;
    }

    static Button goldBtn(String txt) {
        Button b = new Button(txt);
        b.setStyle(BTN_GOLD);
        b.setOnMouseEntered(e -> b.setStyle(BTN_GOLD + "-fx-background-color:#48BB78; -fx-scale-x: 1.03; -fx-scale-y: 1.03;"));
        b.setOnMouseExited(e  -> b.setStyle(BTN_GOLD));
        return b;
    }

    static Button outlineBtn(String txt) {
        Button b = new Button(txt);
        b.setStyle(BTN_OUTLINE);
        b.setOnMouseEntered(e -> b.setStyle(BTN_OUTLINE + "-fx-background-color:rgba(56,161,105,0.1);"));
        b.setOnMouseExited(e -> b.setStyle(BTN_OUTLINE));
        return b;
    }

    static Button redBtn(String txt) {
        Button b = new Button(txt);
        b.setStyle(BTN_RED);
        b.setOnMouseEntered(e -> b.setStyle(BTN_RED + "-fx-background-color:#742A2A;"));
        b.setOnMouseExited(e -> b.setStyle(BTN_RED));
        return b;
    }

    static Button greenBtn(String txt) {
        Button b = new Button(txt);
        b.setStyle(BTN_GREEN);
        b.setOnMouseEntered(e -> b.setStyle(BTN_GREEN + "-fx-background-color:#1C4D30;"));
        b.setOnMouseExited(e -> b.setStyle(BTN_GREEN));
        return b;
    }
}

public class main1 extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Grand Vista Resort");

        Label hotelName = new Label("Grand Vista Resort");
        hotelName.setStyle("-fx-text-fill:" + Styles.ACCENT + "; -fx-font-size:28px; -fx-font-weight:bold; -fx-font-family:'Georgia';");
        Label subtitle = new Label("Sponsored by Zeb Builders");
        subtitle.setStyle("-fx-text-fill:" + Styles.SUBTEXT + "; -fx-font-size:13px;");

        VBox header = new VBox(4, hotelName, subtitle);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30, 20, 20, 20));

        HBox nav = new HBox(16,
                navTile("🛏", "Rooms",         "Manage room inventory",   () -> new main2().show()),
                navTile("👤", "Guests",        "Guest profiles & history", () -> new main3().show()),
                navTile("📋", "Reservations",  "Bookings & check-ins",    () -> new main4().show()),
                navTile("💳", "Payments",      "Billing & transactions",  () -> new main5().show()),
                navTile("👔", "Staff",         "Staff records",           () -> showStaffInfo())
        );
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(10, 30, 10, 30));

        HBox stats = new HBox(16,
                statCard("Available Rooms", getCount("SELECT COUNT(*) FROM Suites WHERE hk_status='Vacant'"), Styles.GREEN),
                statCard("Total Guests",    getCount("SELECT COUNT(*) FROM Clients"), Styles.ACCENT2),
                statCard("Reservations",    getCount("SELECT COUNT(*) FROM Bookings WHERE book_status IN ('Approved','Active')"), "#4299E1"),
                statCard("Staff Members",   getCount("SELECT COUNT(*) FROM Employees"), Styles.SUBTEXT)
        );
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(0, 30, 20, 30));

        Label tip = new Label("💡 Click any tile above to open that module • Adaptive layouts autosave to MySQL database.");
        tip.setStyle("-fx-text-fill:#3B5449; -fx-font-size:11px;");
        HBox tipBar = new HBox(tip);
        tipBar.setAlignment(Pos.CENTER);
        tipBar.setPadding(new Insets(0, 0, 20, 0));

        VBox root = new VBox(header, nav, stats, tipBar);
        root.setStyle("-fx-background-color:" + Styles.BG + ";");

        Scene scene = new Scene(root, 920, 500);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private VBox navTile(String icon, String label, String desc, Runnable action) {
        Label ico  = new Label(icon); ico.setStyle("-fx-font-size:32px;");
        Label lbl  = new Label(label); lbl.setStyle("-fx-text-fill:" + Styles.TEXT + "; -fx-font-size:15px; -fx-font-weight:bold;");
        Label dsc  = new Label(desc);  dsc.setStyle("-fx-text-fill:" + Styles.SUBTEXT + "; -fx-font-size:11px;");
        dsc.setWrapText(true); dsc.setAlignment(Pos.CENTER);

        VBox tile = new VBox(8, ico, lbl, dsc);
        tile.setAlignment(Pos.CENTER);
        tile.setPrefSize(150, 130);
        tile.setStyle("-fx-background-color:" + Styles.SURFACE + "; -fx-background-radius:14; -fx-cursor:hand; -fx-padding:18; -fx-border-color:#1F3A30; -fx-border-radius:14;");

        tile.setOnMouseEntered(e -> tile.setStyle(
                "-fx-background-color:" + Styles.CARD + "; -fx-background-radius:14; -fx-cursor:hand; -fx-padding:18; " +
                        "-fx-border-color:" + Styles.ACCENT + "; -fx-border-radius:14; -fx-border-width:1.5;"
        ));
        tile.setOnMouseExited(e -> tile.setStyle("-fx-background-color:" + Styles.SURFACE + "; -fx-background-radius:14; -fx-cursor:hand; -fx-padding:18; -fx-border-color:#1F3A30; -fx-border-radius:14;"));
        tile.setOnMouseClicked(e -> action.run());
        return tile;
    }

    private VBox statCard(String label, String value, String color) {
        Label val = new Label(value);
        val.setStyle("-fx-text-fill:" + color + "; -fx-font-size:30px; -fx-font-weight:bold;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:" + Styles.SUBTEXT + "; -fx-font-size:12px;");
        VBox card = new VBox(4, val, lbl);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);
        card.setStyle("-fx-background-color:" + Styles.SURFACE + "; -fx-background-radius:10; -fx-padding:18 10; -fx-border-color:#1F3A30; -fx-border-radius:10;");
        return card;
    }

    private String getCount(String sql) {
        try (var st = DBconnection.getConnection().createStatement();
             var rs = st.executeQuery(sql)) {
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) { return "—"; }
        return "—";
    }

    private void showStaffInfo() { new main5().showStaff(); }

    public static void main(String[] args) {
        launch(args);
    }
}