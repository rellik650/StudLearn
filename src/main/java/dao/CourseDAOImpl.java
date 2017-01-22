package dao;


import model.Course;
import model.Teacher;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CourseDAOImpl implements CourseDAO {
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    public CourseDAOImpl(DataSource dataSource) {
        setDataSource(dataSource);  //injected via Spring
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /*Converts from java.Date to SQL Date*/
    private String getSqlDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String sqlDate = formatter.format(date);
        return sqlDate;
    }

    /*Returns a student (ID) that is enrolled to a course (ID). If the student is not enrolled returns -1 */
    public int getEnrolledStudentId(int courseId) {
        String sql = "SELECT student_id FROM student_course_enrolment WHERE course_id = " + courseId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) {
                    return resultSet.getInt("student_id");
                }
                return -1;
            }
        });
    }

    /*Unenroll a student from a course via registration ID*/
    public void unenroll(int registrationId) {
        String sql = "DELETE FROM student_course_enrolment WHERE registration_id = ?";
        jdbcTemplate.update(sql, registrationId);
    }

    /*Enroll a student to a course*/
    public void enroll(int studentId, int courseId) {
        String sql = "CALL course_enrolment(?, ?)";
        jdbcTemplate.update(sql, studentId, courseId);
    }

    /*Returns a course enrollment registration ID based on student ID and course ID. If not found, returns -1*/
    public int getRegistrationId(int studentId, int courseId) {
        String sql = "SELECT registration_id FROM student_course_enrolment WHERE student_id = " + studentId +
                " AND course_id = " + courseId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) {
                    return resultSet.getInt("registration_id");
                }
                return -1;
            }
        });
    }

    public boolean isSubmitted(int registrationId) {
        String sql = "SELECT registration_id FROM student_test_taken WHERE registration_id = " + registrationId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) {
                    int registrationId = resultSet.getInt("registration_id");
                    if(registrationId != -1) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /*Insert into database as BLOB type, a .PDF File that represents the course */
    public void insertCourseFile(int courseId, String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        InputStream fileStream = new FileInputStream(file);
        LobHandler lobHandler = new DefaultLobHandler();
        String sql = "UPDATE courses SET course_pdf = ? WHERE course_id = " + courseId;
        jdbcTemplate.update(sql,
                new Object[]{
                        new SqlLobValue(fileStream, (int) file.length(), lobHandler),
                },
                new int[]{Types.BLOB});
    }

    /*Returns the course file */
    public InputStream readCourseFile(int courseId) {
        String sql = "SELECT course_pdf FROM courses WHERE course_id = " + courseId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<InputStream>() {
            @Override
            public InputStream extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) {
                    LobHandler lobHandler = new DefaultLobHandler();
                    return lobHandler.getBlobAsBinaryStream(resultSet, "course_pdf");
                }
                return null;
            }
        });
    }

    /*Sets a specification for a assignment*/
    public void setAssignmentSpecification(int courseId, String assignmentSpecification, Date currentDate) {
        String sql = "UPDATE courses SET assignment_specification = ?, deadline = ?  WHERE course_id = ?";
        Date deadline = DateUtils.addMonths(currentDate, 1);
        String sqlDeadline = getSqlDate(deadline);
        jdbcTemplate.update(sql, assignmentSpecification, sqlDeadline, courseId);
    }

    /*Gets the specification for the assignment*/
    public String getAssignmentSpecification(int courseId) {
        String sql = "SELECT assignment_specification, deadline FROM courses WHERE course_id = " + courseId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) {
                    String assignmentSpecification = resultSet.getString("assignment_specification");
                    Date deadline = resultSet.getDate("deadline");
                    String sqlDate = getSqlDate(deadline);
                    return assignmentSpecification + "\nDeadline: " + sqlDate;
                }
                return null;
            }
        });
    }

    /*Inserts a file that represents the submission*/
    public void submit(int registrationId, String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        InputStream fileStream = new FileInputStream(file);
        LobHandler lobHandler = new DefaultLobHandler();
        String sql = "INSERT INTO student_test_taken(registration_id, date_test_taken, submission) " +
                "VALUES (" + registrationId + ", CURDATE(), ?)";
        jdbcTemplate.update(sql,
                new Object[]{
                        new SqlLobValue(fileStream, (int) file.length(), lobHandler),
                },
                new int[]{Types.BLOB});
    }

    /*Find a course by name*/
    public Course findByName(String courseName) {
        String sqlCourseName = "'" + courseName + "'";
        String sql = "SELECT * FROM courses WHERE course_name = " + sqlCourseName;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Course>() {
            @Override
            public Course extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) {
                    int courseId = resultSet.getInt("course_id");
                    String courseName = resultSet.getString("course_name");
                    int teacherId = resultSet.getInt("teacher_id");
                    Teacher teacher = new Teacher(teacherId);
                    return new Course(courseId, courseName, teacher);
                }
                return null;
            }
        });
    }

    /*Gets the file that represents the submission*/
    public InputStream review(int registrationId) {
        String sql = "SELECT submission FROM student_test_taken WHERE registration_id = " + registrationId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<InputStream>() {
            @Override
            public InputStream extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) {
                    LobHandler lobHandler = new DefaultLobHandler();
                    return lobHandler.getBlobAsBinaryStream(resultSet, "submission");
                }
                return null;
            }
        });
    }

    /*Gets the grade of the student enrolled via registration ID*/
    public int getGrade(int registrationId) {
        String sql = "SELECT test_result FROM student_test_taken WHERE registration_id = " + registrationId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) {
                    return resultSet.getInt("test_result");
                }
                return -1;
            }
        });
    }

    /*Sets the grade for student enrolled via registration ID*/
    public void setGrade(int registrationId, int grade) {
        String sql = "call update_grade(?, ?)";
        jdbcTemplate.update(sql, registrationId, grade);
    }

    /*Complete the course*/
    public void completeCourse(int registrationId) {
        String sql = "call finish_course(?)";
        jdbcTemplate.update(sql, registrationId);
    }

    /*CRUD operations*/

    /*Create*/
    public void create(Course course) {
        String sql = "INSERT INTO courses(course_name, teacher_id) VALUES(?, ?)";
        jdbcTemplate.update(sql, course.getCourseName(), course.getTeacher().getTeacherId());
    }

    /*Read*/
    public Course findById(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = " + courseId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Course>() {
            public Course extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                int courseId = resultSet.getInt("course_id");
                String courseName = resultSet.getString("course_name");
                int teacherId = resultSet.getInt("teacher_id");
                Teacher teacher = new Teacher(teacherId);
                Course course = new Course(courseId, courseName, teacher);
                return course;
            }
        });
    }

    /*Read all*/
    public List<Course> findAll() {
        String sql = "SELECT * FROM courses";
        List<Course> courseList = jdbcTemplate.query(sql, new RowMapper<Course>() {
            public Course mapRow(ResultSet resultSet, int i) throws SQLException {
                Course course;
                int courseId = resultSet.getInt("course_id");
                String courseName = resultSet.getString("course_name");
                int teacherId = resultSet.getInt("teacher_id");
                Teacher teacher = new Teacher(teacherId);
                course = new Course(courseId, courseName, teacher);
                return course;
            }
        });
        return courseList;
    }

    /*Update*/
    public void update(Course course) {
        String sql = "UPDATE courses SET course_name = ?, teacher_id = ? WHERE course_id = ?";
        jdbcTemplate.update(sql, course.getCourseName(), course.getTeacher().getTeacherId(), course.getCourseId());
    }

    /*Delete*/
    public void delete(int courseId) {
        //String sql = "DELETE FROM student_course_enrolment WHERE course_id = ?";
        //jdbcTemplate.update(sql, courseId);
        //Delete on cascade
        String sql = "DELETE FROM courses WHERE course_id = ?";
        jdbcTemplate.update(sql, courseId);
    }
}
