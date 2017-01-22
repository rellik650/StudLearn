import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;


public class LoginController extends Controller implements Initializable {

    /*Injected via FXML*/
    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXButton createAccountButton;

    @FXML
    private ImageView mainLogo;

    @FXML
    private JFXButton loginButton;

    @FXML
    private JFXTextField usernameTextField;

    @FXML
    private JFXPasswordField passwordField;

    /*Constructor*/
    public LoginController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fade(mainLogo, 0, 100, 3300);

        /*Goes to registration scene*/
        createAccountButton.setOnAction(event -> changeView(rootPane, "CreateAccountView.fxml"));

        /*Validates login information and goes to the main scene*/
        loginButton.setOnAction(event -> {
            boolean valid = validate(usernameTextField.getText(), passwordField.getText());
            String accountType = getAccountType(usernameTextField.getText(), passwordField.getText());

            if (valid && accountType.equals("Student")) {
                logAsStudent(usernameTextField.getText());
            } else if (valid && accountType.equals("Teacher")) {
                logAsTeacher(usernameTextField.getText());
            } else {
                ImageView errorIcon = new ImageView(new Image("images/close.png"));
                showNotification("Invalid user", "Username or password are wrong", errorIcon);
            }
        });
    }

    /*Returns the type of the account*/
    private String getAccountType(String username, String password) {
        String hashedPassword = User.hashPassword(password, "SHA-1");
        return getLoginDAO().getAccountType(username, hashedPassword);
    }

    /*Validates the account*/
    private boolean validate(String username, String password) {
        String hashedPassword = User.hashPassword(password, "SHA-1");
        return getLoginDAO().validate(username, hashedPassword);
    }

    /*Changes the scene to student main scene*/
    private void logAsStudent(String username) {
        ImageView validationIcon = new ImageView(new Image("images/tick.png"));
        String validationMessage = "You have successfully logged in as a student.\nUsername: " + username;
        showNotification("Logged", validationMessage, validationIcon);

        long userId = username.hashCode();
        if (userId < 0) {
            userId = -userId;
        }

        getLoginDAO().updateLogTime(userId);
        setCurrentUser(getUserDAO().findById(userId));
        changeView(rootPane, "StudentMainView.fxml", getCurrentUser(), new StudentMainController());
    }

    /*Changes the scene to teacher main scene*/
    private void logAsTeacher(String username) {
        String validationMessage = "You have successfully logged in as a teacher.\nUsername: " + username;
        ImageView validationIcon = new ImageView(new Image("images/tick.png"));
        showNotification("Logged", validationMessage, validationIcon);

        long userId = username.hashCode();
        if (userId < 0) {
            userId = -userId;
        }

        getLoginDAO().updateLogTime(userId);
        setCurrentUser(getUserDAO().findById(userId));
        changeView(rootPane, "TeacherMainView.fxml", getCurrentUser(), new TeacherMainController());
    }


}

