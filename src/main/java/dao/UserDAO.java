package dao;


import model.Student;
import model.User;

import java.util.List;

public interface UserDAO {
    void create(User user);

    User findById(long userId);

    void update(User user);

    void delete(long userId);

    String getAccountType(long userId);

    long getTeacherUserId(int teacherId);

    List<Student> findAllStudents();

}
