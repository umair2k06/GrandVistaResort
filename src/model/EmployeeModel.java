package model;

import javafx.beans.property.*;

public class EmployeeModel {
    private final IntegerProperty employeeId = new SimpleIntegerProperty();
    private final StringProperty  firstName  = new SimpleStringProperty();
    private final StringProperty  lastName   = new SimpleStringProperty();
    private final StringProperty  jobRole    = new SimpleStringProperty();
    private final StringProperty  email      = new SimpleStringProperty();
    private final StringProperty  phone      = new SimpleStringProperty();
    private final DoubleProperty  salary     = new SimpleDoubleProperty();
    private final StringProperty  workShift  = new SimpleStringProperty();
    private final StringProperty  joiningDate = new SimpleStringProperty();

    public EmployeeModel(int id, String fn, String ln, String role, String email,
                         String phone, double salary, String shift, String join) {
        employeeId.set(id); firstName.set(fn); lastName.set(ln);
        this.jobRole.set(role); this.email.set(email); this.phone.set(phone);
        this.salary.set(salary); this.workShift.set(shift); joiningDate.set(join);
    }

    public int    getEmployeeId()  { return employeeId.get(); }
    public String getFirstName()   { return firstName.get(); }
    public String getLastName()    { return lastName.get(); }
    public String getJobRole()     { return jobRole.get(); }
    public String getEmail()       { return email.get(); }
    public String getPhone()       { return phone.get(); }
    public double getSalary()      { return salary.get(); }
    public String getWorkShift()   { return workShift.get(); }
    public String getJoiningDate() { return joiningDate.get(); }

    public IntegerProperty employeeIdProperty()  { return employeeId; }
    public StringProperty  firstNameProperty()   { return firstName; }
    public StringProperty  lastNameProperty()    { return lastName; }
    public StringProperty  jobRoleProperty()     { return jobRole; }
    public StringProperty  emailProperty()       { return email; }
    public StringProperty  phoneProperty()       { return phone; }
    public DoubleProperty  salaryProperty()      { return salary; }
    public StringProperty  workShiftProperty()   { return workShift; }
    public StringProperty  joiningDateProperty() { return joiningDate; }
}
