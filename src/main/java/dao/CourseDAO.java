package dao;

import model.Course;
import model.Student;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;


public interface CourseDAO {

    void create(Course course);

    Course findById(int courseId);

    List<Course> findAll();

    void update(Course course);

    void delete(int courseId);

    int getEnrolledStudentId(int courseId);

    void enroll(int studentId, int courseId);

    void unenroll(int registrationId);

    int getRegistrationId(int studentId, int courseId);

    void insertCourseFile(int courseId, String filePath) throws FileNotFoundException;

    InputStream readCourseFile(int courseId);

    void setAssignmentSpecification(int courseId, String assignmentSpecification, Date currentDate);

    String getAssignmentSpecification(int courseId);

    boolean isSubmitted(int registrationId);

    void submit(int registrationId, String filePath) throws FileNotFoundException;

    InputStream review(int registrationId);

    Course findByName(String courseName);

    int getGrade(int registrationId);

    void setGrade(int registrationId, int grade);

    void completeCourse(int registrationId);
}
