package dao;

public interface LoginDAO {

    boolean validate(String username, String hashedPassword);

    String getAccountType(String username, String hashedPassword);

    void updateLogTime(long userId);
}
