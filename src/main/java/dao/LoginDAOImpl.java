package dao;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAOImpl implements LoginDAO {
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    public LoginDAOImpl(DataSource dataSource) {
        setDataSource(dataSource);  //injected via Spring
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /*Validates username and password when the user try to log in*/
    public boolean validate(String username, String hashedPassword) {
        String sql = "SELECT username, password FROM users";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                while(resultSet.next()) {
                    String extractedUsername = resultSet.getString("username");
                    String extractedPassword = resultSet.getString("password");
                    if(extractedUsername.equals(username) && hashedPassword.equals(extractedPassword)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /*Gets the account type(Student/Teacher) by providing account login information*/
    public String getAccountType(String username, String hashedPassword) {

        username = "'" + username + "'";
        hashedPassword = "'" + hashedPassword + "'";

        String sql = "SELECT type FROM users WHERE username = " + username + " AND password = " + hashedPassword;
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

    /*Updates the log time*/
    public void updateLogTime(long userId) {
        String sql = "CALL login(?)";
        jdbcTemplate.update(sql, userId);
    }


}
