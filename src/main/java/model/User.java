package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    private long userId;
    private String username;
    private String password;
    private String email;

    /*Constructors*/
    public User(long userId, String username, String password, String email) {
        this.setUserId(userId);
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
    }

    public User() {}

    /*Methods*/
    public long getUserId() {
        return this.userId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*Static methods*/
    public static String hashPassword(String password, String algorithm) {
        return hashString(password, algorithm);
    }

    /*Hash a string by using SHA-1/SHA-256/.... algorithm*/
    private static String hashString(String message, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = messageDigest.digest(message.getBytes());
            return convertByteArrayToHexString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    /*Converts an byte array to a hexazecimal and returns a string*/
    private static String convertByteArrayToHexString(byte[] arrayOfBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i < arrayOfBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayOfBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }
}
