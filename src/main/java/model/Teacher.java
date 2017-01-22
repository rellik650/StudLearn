package model;



public class Teacher extends User {
    private int teacherId;
    private String firstName;
    private String lastName;
    private String qualification;

    /*Constructors*/
    public Teacher(int teacherId, String firstName, String lastName, String qualification, long userId, String username, String password, String email) {
        super(userId, username, password, email);
        this.setTeacherId(teacherId);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setQualification(qualification);
    }

    public Teacher(String firstName, String lastName, String qualification, long userId, String username, String password, String email) {
        super(userId, username, password, email);
        this.teacherId = 0;
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setQualification(qualification);
    }

    /*Methods*/
    public Teacher(int teacherId) {
        this.setTeacherId(teacherId);
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
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

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
}
