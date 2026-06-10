package model;

import javafx.beans.property.*;

public class SuiteModel {
    private final IntegerProperty suiteId     = new SimpleIntegerProperty();
    private final StringProperty  doorNumber  = new SimpleStringProperty();
    private final IntegerProperty floorLevel  = new SimpleIntegerProperty();
    private final StringProperty  tierName    = new SimpleStringProperty();
    private final DoubleProperty  nightlyRate = new SimpleDoubleProperty();
    private final StringProperty  hkStatus    = new SimpleStringProperty();

    public SuiteModel(int id, String num, int floor, String type, double price, String status) {
        this.suiteId.set(id); this.doorNumber.set(num); this.floorLevel.set(floor);
        this.tierName.set(type); this.nightlyRate.set(price); this.hkStatus.set(status);
    }

    public int    getSuiteId()    { return suiteId.get(); }
    public String getDoorNumber() { return doorNumber.get(); }
    public int    getFloorLevel() { return floorLevel.get(); }
    public String getTierName()   { return tierName.get(); }
    public double getNightlyRate() { return nightlyRate.get(); }
    public String getHkStatus()   { return hkStatus.get(); }

    public IntegerProperty suiteIdProperty()     { return suiteId; }
    public StringProperty  doorNumberProperty() { return doorNumber; }
    public IntegerProperty floorLevelProperty() { return floorLevel; }
    public StringProperty  tierNameProperty()   { return tierName; }
    public DoubleProperty  nightlyRateProperty() { return nightlyRate; }
    public StringProperty  hkStatusProperty()   { return hkStatus; }
}
