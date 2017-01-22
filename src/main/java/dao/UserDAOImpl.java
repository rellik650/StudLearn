package dao;


import model.Address;
import model.Student;
import model.Teacher;
import model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    public UserDAOImpl(DataSource dataSource) {
        setDataSource(dataSource);  //injected via Spring
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /*Converts java.Date to SQL Date*/
    private String getSqlDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String sqlDate = formatter.format(date);
        return sqlDate;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /*Gets the account type(Student/Teacher) via user ID*/
    public String getAccountType(long userId) {

        String sql = "SELECT type FROM users WHERE user_id = " + userId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if(resultSet.next()) {
                    return resultSet.getString("type");
                }
                return null;
            }
        });
    }

    /*Gets the user ID by knowing the teacher ID*/
    public long getTeacherUserId(int teacherId) {
        String sql = "SELECT user_id FROM teachers WHERE teacher_id = " + teacherId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if(resultSet.next()) {
                    return resultSet.getLong("user_id");
                }
                return null;
            }
        });
    }

    /*Gets the total number of students*/
    public int getNumberOfStudents() {
        String sql = "SELECT COUNT(*) AS total FROM students";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if(resultSet.next()) {
                    int total = resultSet.getInt("total");
                    return total;
                }
                return null;
            }
        });
    }

    /*Reads all the students*/
    public List<Student> findAllStudents() {
        String sql = "SELECT student_id, first_name, last_name, users.email " +
                "FROM students, users WHERE users.user_id = students.user_id";
        return jdbcTemplate.query(sql, new RowMapper<Student>() {
            @Override
            public Student mapRow(ResultSet resultSet, int i) throws SQLException {
                int studentId = resultSet.getInt("student_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                Student student = new Student(studentId, firstName, lastName, email);
                return student;
            }
        });
    }

    /*CRUD operations*/

    /*Create*/
    public void create(User user) {
        String sql = "INSERT INTO users VALUES(?, ?, ?, ?, NOW(), ?)";
        jdbcTemplate.update(sql, user.getUserId(), user.getUsername(), user.getPassword(), user.getEmail(),
                user.getClass().getSimpleName());
        if(user instanceof Student) {
            sql = "INSERT INTO students(first_name, last_name, address_id, birthdate, user_id) VALUES(?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, ((Student) user).getFirstName(),
                    ((Student) user).getLastName(), ((Student) user).getAddress().getAddressId(),
                    getSqlDate(((Student) user).getBirthdate()), user.getUserId());
        } else if(user instanceof Teacher) {
            sql = "INSERT INTO teachers(first_name, last_name, qualification, user_id) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, ((Teacher) user).getFirstName(),
                    ((Teacher) user).getLastName(), ((Teacher) user).getQualification(), user.getUserId());
        }
    }

    /*Read*/
    public User findById(long userId) {
        String sql;
        String accountType = getAccountType(userId);
        if(accountType.equals("Student")) {
            sql = "SELECT * FROM students, users WHERE students.user_id = users.user_id " +
                    "AND users.user_id = " + userId;
            return jdbcTemplate.query(sql, new ResultSetExtractor<User>() {
                @Override
                public User extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                    if(resultSet.next()) {
                        int studentId = resultSet.getInt("student_id");
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        int addressId = resultSet.getInt("address_id");
                        Date birthdate = resultSet.getDate("birthdate");
                        int userId = resultSet.getInt("user_id");
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String email = resultSet.getString("email");
                        User user = new Student(studentId, firstName, lastName, new Address(addressId), birthdate,
                                userId, username, password, email);
                        return user;
                    }
                    return null;
                }
            });
        } else if(accountType.equals("Teacher")) {
            sql = "SELECT * FROM teachers, users WHERE teachers.user_id = users.user_id " +
                    "AND users.user_id = " + userId;
            return jdbcTemplate.query(sql, new ResultSetExtractor<User>() {

                @Override
                public User extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                    if(resultSet.next()) {
                        int teacherId = resultSet.getInt("teacher_id");
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        String qualification = resultSet.getString("qualification");
                        long userId = resultSet.getLong("user_id");
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String email = resultSet.getString("email");
                        User user = new Teacher(teacherId, firstName, lastName, qualification,
                                userId, username, password, email);
                        return user;
                    }
                    return null;
                }
            });
        }
        return null;
    }

    /*Update*/
    public void update(User user) {
        String sql;
        if(user instanceof Student) {
             sql = "UPDATE students SET first_name = ?, last_name = ?, address_id = ?, birthdate = ? " +
                    "WHERE student_id = ?";
            jdbcTemplate.update(sql, ((Student) user).getFirstName(), ((Student) user).getLastName(),
                    ((Student) user).getAddress().getAddressId(), getSqlDate(((Student) user).getBirthdate()),
                    ((Student) user).getStudentId());
        }  else if(user instanceof Teacher) {
            sql = "UPDATE teachers SET first_name = ?, last_name = ?, qualification = ? WHERE teacher_id = ?";
            jdbcTemplate.update(sql, ((Teacher) user).getFirstName(), ((Teacher) user).getLastName(),
                    ((Teacher) user).getQualification(), ((Teacher) user).getTeacherId());
        }
        sql = "UPDATE users SET username = ?, password = ?, email = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getEmail(), user.getUserId());
    }

    /*Delete*/
    public void delete(long userId) {
        String sql = "";
        String accountType = getAccountType(userId);
        if(accountType.equals("Student")) {
            sql = "DELETE FROM students WHERE user_id = ?";
            jdbcTemplate.update(sql, userId);
        } else if(accountType.equals("Teacher")) {
            sql = "DELETE FROM teachers WHERE user_id = ?";
            jdbcTemplate.update(sql, userId);
        }
        sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

}
