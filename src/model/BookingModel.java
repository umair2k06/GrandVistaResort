package model;

import javafx.beans.property.*;

public class BookingModel {
    private final IntegerProperty resId      = new SimpleIntegerProperty();
    private final StringProperty  guestName  = new SimpleStringProperty();
    private final StringProperty  roomNumber = new SimpleStringProperty();
    private final StringProperty  roomType   = new SimpleStringProperty();
    private final StringProperty  checkIn    = new SimpleStringProperty();
    private final StringProperty  checkOut   = new SimpleStringProperty();
    private final IntegerProperty nights     = new SimpleIntegerProperty();
    private final StringProperty  status     = new SimpleStringProperty();
    private final DoubleProperty  amount     = new SimpleDoubleProperty();
    private final StringProperty  handledBy  = new SimpleStringProperty();

    public BookingModel(int id, String guest, String room, String type,
                        String ci, String co, int nights, String status,
                        double amount, String handler) {
        resId.set(id); guestName.set(guest); roomNumber.set(room); roomType.set(type);
        checkIn.set(ci); checkOut.set(co); this.nights.set(nights);
        this.status.set(status); this.amount.set(amount); handledBy.set(handler);
    }

    public int    getResId()      { return resId.get(); }
    public String getGuestName()  { return guestName.get(); }
    public String getRoomNumber() { return roomNumber.get(); }
    public String getRoomType()   { return roomType.get(); }
    public String getCheckIn()    { return checkIn.get(); }
    public String getCheckOut()   { return checkOut.get(); }
    public int    getNights()     { return nights.get(); }
    public String getStatus()     { return status.get(); }
    public double getAmount()     { return amount.get(); }
    public String getHandledBy()  { return handledBy.get(); }

    public IntegerProperty resIdProperty()      { return resId; }
    public StringProperty  guestNameProperty()  { return guestName; }
    public StringProperty  roomNumberProperty() { return roomNumber; }
    public StringProperty  roomTypeProperty()   { return roomType; }
    public StringProperty  checkInProperty()    { return checkIn; }
    public StringProperty  checkOutProperty()   { return checkOut; }
    public IntegerProperty nightsProperty()     { return nights; }
    public StringProperty  statusProperty()     { return status; }
    public DoubleProperty  amountProperty()     { return amount; }
    public StringProperty  handledByProperty()  { return handledBy; }
}
