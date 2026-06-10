package model;

import javafx.beans.property.*;

public class TransactionModel {
    private final IntegerProperty transactionId  = new SimpleIntegerProperty();
    private final IntegerProperty bookingId      = new SimpleIntegerProperty();
    private final StringProperty  customerName   = new SimpleStringProperty();
    private final StringProperty  roomNo         = new SimpleStringProperty();
    private final DoubleProperty  bookingTotal   = new SimpleDoubleProperty();
    private final DoubleProperty  amountPaid     = new SimpleDoubleProperty();
    private final StringProperty  paymentMethod  = new SimpleStringProperty();
    private final StringProperty  transactionDate = new SimpleStringProperty();
    private final StringProperty  transactionStatus = new SimpleStringProperty();
    private final StringProperty  transactionRef  = new SimpleStringProperty();

    public TransactionModel(int tid, int bid, String customer, String room,
                            double total, double paid, String method,
                            String date, String status, String txRef) {
        transactionId.set(tid); bookingId.set(bid); customerName.set(customer);
        roomNo.set(room); bookingTotal.set(total); this.amountPaid.set(paid);
        this.paymentMethod.set(method); transactionDate.set(date);
        transactionStatus.set(status); this.transactionRef.set(txRef);
    }

    public int    getTransactionId()     { return transactionId.get(); }
    public int    getBookingId()         { return bookingId.get(); }
    public String getCustomerName()      { return customerName.get(); }
    public String getRoomNo()            { return roomNo.get(); }
    public double getBookingTotal()      { return bookingTotal.get(); }
    public double getAmountPaid()        { return amountPaid.get(); }
    public String getPaymentMethod()     { return paymentMethod.get(); }
    public String getTransactionDate()    { return transactionDate.get(); }
    public String getTransactionStatus()  { return transactionStatus.get(); }
    public String getTransactionRef()     { return transactionRef.get(); }

    public IntegerProperty transactionIdProperty()     { return transactionId; }
    public IntegerProperty bookingIdProperty()         { return bookingId; }
    public StringProperty  customerNameProperty()      { return customerName; }
    public StringProperty  roomNoProperty()            { return roomNo; }
    public DoubleProperty  bookingTotalProperty()      { return bookingTotal; }
    public DoubleProperty  amountPaidProperty()        { return amountPaid; }
    public StringProperty  paymentMethodProperty()     { return paymentMethod; }
    public StringProperty  transactionDateProperty()    { return transactionDate; }
    public StringProperty  transactionStatusProperty()  { return transactionStatus; }
    public StringProperty  transactionRefProperty()     { return transactionRef; }
}
