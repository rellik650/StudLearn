package model;


public class Course {
    private int courseId;
    private String courseName;
    private Teacher teacher;

    /*Constructors*/
    public Course(int courseId, String courseName, Teacher teacher) {
        this.setCourseId(courseId);
        this.setCourseName(courseName);
        this.setTeacher(teacher);
    }

    public Course(String courseName, Teacher teacher) {
        this.setCourseId(0);
        this.setCourseName(courseName);
        this.setTeacher(teacher);
    }

    /*Methods*/
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }


}
