package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClientModel {
    private final IntegerProperty clientId    = new SimpleIntegerProperty();
    private final StringProperty  firstName   = new SimpleStringProperty();
    private final StringProperty  lastName    = new SimpleStringProperty();
    private final StringProperty  emailAddr   = new SimpleStringProperty();
    private final StringProperty  contactNo   = new SimpleStringProperty();
    private final StringProperty  idCard      = new SimpleStringProperty();
    private final StringProperty  origin      = new SimpleStringProperty();
    private final StringProperty  homeCity    = new SimpleStringProperty();

    public ClientModel(int id, String fn, String ln, String em, String ph, String cn, String nat, String city) {
        clientId.set(id); firstName.set(fn); lastName.set(ln);
        emailAddr.set(em); contactNo.set(ph); idCard.set(cn);
        origin.set(nat); this.homeCity.set(city);
    }

    public int    getClientId()   { return clientId.get(); }
    public String getFirstName()  { return firstName.get(); }
    public String getLastName()   { return lastName.get(); }
    public String getEmailAddr()  { return emailAddr.get(); }
    public String getContactNo()  { return contactNo.get(); }
    public String getIdCard()     { return idCard.get(); }
    public String getOrigin()     { return origin.get(); }
    public String getHomeCity()   { return homeCity.get(); }

    public IntegerProperty clientIdProperty()    { return clientId; }
    public StringProperty  firstNameProperty()  { return firstName; }
    public StringProperty  lastNameProperty()   { return lastName; }
    public StringProperty  emailAddrProperty()  { return emailAddr; }
    public StringProperty  contactNoProperty()  { return contactNo; }
    public StringProperty  idCardProperty()     { return idCard; }
    public StringProperty  originProperty()     { return origin; }
    public StringProperty  homeCityProperty()   { return homeCity; }
}
