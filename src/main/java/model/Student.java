package model;


import java.util.Date;


public class Student extends User {
    private int studentId;
    private String firstName;
    private String lastName;
    private Address address;
    private Date birthdate;

    /*Constructors*/
    public Student(int studentId, String firstName, String lastName, Address address, Date birthdate, long userId, String username, String password, String email) {
        super(userId, username, password, email);
        this.setStudentId(studentId);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setAddress(address);
        this.setBirthdate(birthdate);
    }

    public Student(String firstName, String lastName, Address address, Date birthdate, long userId, String username, String password, String email) {
        super(userId, username, password, email);
        this.studentId = 0;
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setAddress(address);
        this.setBirthdate(birthdate);
    }

    public Student(int studentId, String firstName, String lastName, String email) {
        super(-1, "", "", email);
        this.setStudentId(studentId);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setAddress(null);
        this.setBirthdate(null);
    }

    /*Methods*/
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
