import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import model.Address;
import model.Student;
import model.Teacher;
import model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;


public class CreateAccountController extends Controller implements Initializable {

    /*Injected via FXML*/
    @FXML private AnchorPane rootPane;

    @FXML private JFXRadioButton studentRadioButton;

    @FXML private JFXRadioButton teacherRadioButton;

    @FXML private Pane studentRegistrationPane;

    @FXML private Pane teacherRegistrationPane;

    @FXML private JFXButton studentSubmitButton;

    @FXML private JFXButton studentCancelButton;

    @FXML private JFXButton teacherSubmitButton;

    @FXML private JFXButton teacherCancelButton;

    /*Username details*/

    @FXML private JFXTextField usernameTextField;


    @FXML private JFXPasswordField passwordField;

    @FXML private JFXPasswordField confirmPasswordField;

    @FXML private JFXTextField emailTextField;

    /*Student details*/

    @FXML private JFXTextField studentFirstNameTextField;

    @FXML private JFXTextField studentLastNameTextField;

    @FXML private JFXTextField streetNameTextField;

    @FXML private JFXTextField streetNumberTextField;


    @FXML private JFXTextField cityTextField;

    @FXML private JFXTextField stateTextField;

    @FXML private JFXDatePicker datePicker;

    /*Teacher details*/

    @FXML private JFXTextField teacherFirstNameTextField;

    @FXML private JFXTextField teacherLastNameTextField;

    @FXML private JFXTextField qualificationTextField;

    /*Constructor*/
    public CreateAccountController() {
        super();
    }

    public void initialize(URL location, ResourceBundle resources) {
        studentRegistrationPane.setVisible(true);
        studentRadioButton.setSelected(true);
        teacherRegistrationPane.setVisible(false);

        /*Sets on student registration mode*/
        studentRadioButton.setOnAction(event -> {
            if (studentRadioButton.isSelected()) {
                teacherRadioButton.setSelected(false);
                fade(studentRegistrationPane, 0, 100, 1000);
                studentRegistrationPane.setVisible(true);
                teacherRegistrationPane.setVisible(false);
            }
        });

        /*Sets on teacher registration mode*/
        teacherRadioButton.setOnAction(event -> {
            if (teacherRadioButton.isSelected()) {
                studentRadioButton.setSelected(false);
                fade(teacherRegistrationPane, 0, 100, 1000);
                teacherRegistrationPane.setVisible(true);
                studentRegistrationPane.setVisible(false);
            }
        });

        /*Goes back*/
        studentCancelButton.setOnAction(event -> changeView(rootPane, "LoginView.fxml"));

        /*Goes back*/
        teacherCancelButton.setOnAction(event -> changeView(rootPane, "LoginView.fxml"));

        /*Validate registration form - Student mode*/
        studentSubmitButton.setOnAction(event -> {
            if (!areStudentFieldsValid()) {
                ImageView errorIcon = new ImageView(new Image("images/close.png"));
                showNotification("Empty fields", "All fields must be filled", errorIcon);
            } else {
                registerStudent();
            }
        });

        /*Validate registration form - Teacher mode*/
        teacherSubmitButton.setOnAction(event -> {
            if (!areTeacherFieldsValid()) {
                ImageView errorIcon = new ImageView(new Image("images/close.png"));
                showNotification("Empty fields", "All fields must be filled", errorIcon);
            } else {
                registerTeacher();
            }
        });
    }

    /*Extracts data from the view for a teacher*/
    private User getTeacherData(String password) {
        String firstName = teacherFirstNameTextField.getText();
        String lastName = teacherLastNameTextField.getText();
        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        String qualification = qualificationTextField.getText();
        long userId = username.hashCode();
        String hashedPassword = User.hashPassword(password, "SHA-1");

        if (userId < 0) {
            userId = -userId;
        }

        User teacher = new Teacher(firstName, lastName, qualification, userId, username, hashedPassword, email);
        return teacher;
    }

    /*Extracts address data for a student*/
    private Address getAddressData() {
        int addressId = getAddressDAO().generateId();
        String streetName = streetNameTextField.getText();
        String city = cityTextField.getText();
        String state = stateTextField.getText();
        int streetNumber = 0;

        try {
            streetNumber = Integer.parseInt(streetNumberTextField.getText());
        } catch (NumberFormatException e) {
            showNotification("Invalid input", "Invalid street number", null);

        }

        return new Address(addressId, streetName, streetNumber, city, state);
    }

    /*Verifies if user fields are not empty*/
    private boolean areUserFieldsValid() {
        if (usernameTextField.getText().isEmpty()) {
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            return false;
        }
        if (emailTextField.getText().isEmpty()) {
            return false;
        }
        if (confirmPasswordField.getText().isEmpty()) {
            return false;
        }
        return true;
    }

    /*Verifies if user/student fields are not empty*/
    private boolean areStudentFieldsValid() {
        if (studentFirstNameTextField.getText().isEmpty()) {
            return false;
        }
        if (studentLastNameTextField.getText().isEmpty()) {
            return false;
        }
        if (streetNameTextField.getText().isEmpty()) {
            return false;
        }
        if (streetNumberTextField.getText().isEmpty()) {
            return false;
        }
        if (cityTextField.getText().isEmpty()) {
            return false;
        }
        if (stateTextField.getText().isEmpty()) {
            return false;
        }
        if (datePicker.getValue() == null) {
            return false;
        }
        return true && areUserFieldsValid();
    }

    /*Verifies if user/teacher fields are not empty*/
    private boolean areTeacherFieldsValid() {
        if (teacherFirstNameTextField.getText().isEmpty()) {
            return false;
        }
        if (teacherLastNameTextField.getText().isEmpty()) {
            return false;
        }
        if (qualificationTextField.getText().isEmpty()) {
            return false;
        }
        return true && areUserFieldsValid();
    }

    /*Converts the input date from view to java.Date*/
    private Date convertToDate(LocalDate date) {
        Instant instant = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /*Extracts data from view for a student*/
    private User getStudentData(Address address, String password) {
        String firstName = studentFirstNameTextField.getText();
        String lastName = studentLastNameTextField.getText();
        Date birthdate = convertToDate(datePicker.getValue());
        String username = usernameTextField.getText();
        String hashedPassword = User.hashPassword(password, "SHA-1");
        String email = emailTextField.getText();
        long userId = username.hashCode();

        if (userId < 0) {
            userId = -userId;
        }

        User student = new Student(firstName, lastName, address, birthdate,
                userId, username, hashedPassword, email);
        return student;
    }

    /*Creates a student account*/
    private void registerStudent() {
        String password = passwordField.getText();
        String confirmedPassword = confirmPasswordField.getText();

        if (!password.equals(confirmedPassword)) {
            ImageView invalidPasswordIcon = new ImageView(new Image("images/close.png"));
            showNotification("Invalid password", "Passwords must be the same", invalidPasswordIcon);
        } else {

            Address address = getAddressData();
            User student = getStudentData(address, password);

            try {
                getAddressDAO().create(address);
                getUserDAO().create(student);
                ImageView validRegistrationIcon = new ImageView(new Image("images/tick.png"));
                String validRegistrationMessage = "User " + student.getUsername() + " registered";
                showNotification("Registered successfully ", validRegistrationMessage, validRegistrationIcon);
                changeView(rootPane, "LoginView.fxml");
            } catch (DuplicateKeyException e) {
                ImageView duplicationIcon = new ImageView(new Image("images/info.png"));
                String duplicationMessage = "User" + student.getUsername() + " is already registered";
                showNotification("User exists", duplicationMessage, duplicationIcon);
                getAddressDAO().delete(address.getAddressId());
            } catch (DataAccessException e) {
                ImageView icon = new ImageView(new Image("images/info.png"));
                showNotification("Database error", "Cannot access the database. Try later.", icon);
                e.printStackTrace();
            }
        }
    }

    /*Creates a teacher account*/
    private void registerTeacher()  {
        String password = passwordField.getText();
        String confirmedPassword = confirmPasswordField.getText();

        if (!password.equals(confirmedPassword)) {
            ImageView invalidPasswordIcon = new ImageView(new Image("images/close.png"));
            showNotification("Invalid password", "Passwords must be the same", invalidPasswordIcon);
        } else {
            User teacher = getTeacherData(password);

            try {
                getUserDAO().create(teacher);
                ImageView validRegistrationIcon = new ImageView(new Image("images/tick.png"));
                String validRegistrationMessage = "User " + teacher.getUsername() + " registered";
                showNotification("Registered successfully ", validRegistrationMessage, validRegistrationIcon);
                changeView(rootPane, "LoginView.fxml");
            } catch (DuplicateKeyException e) {
                ImageView duplicationIcon = new ImageView(new Image("images/info.png"));
                String duplicationMessage = "User" + teacher.getUsername() + " is already registered";
                showNotification("User exists", duplicationMessage, duplicationIcon);
            } catch (DataAccessException e) {
                ImageView icon = new ImageView(new Image("images/info.png"));
                showNotification("Database error", "Cannot access the database. Try later.", icon);
                e.printStackTrace();
            }
        }
    }
}
