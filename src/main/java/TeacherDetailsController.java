import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Student;
import model.Teacher;

import java.awt.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class TeacherDetailsController extends Controller implements Initializable {

    /*Injected via FXML*/
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label qualificationLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private JFXTextField firstNameTextField;

    @FXML
    private JFXTextField lastNameTextField;

    @FXML
    private JFXTextField qualificationTextField;

    @FXML
    private JFXButton editProfileButton;

    @FXML
    private HBox editConfirmationPane;

    @FXML
    private JFXButton applyButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXButton deleteAccountButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDetails();
        setEditable(false);

        /*Changes to edit mode*/
        editProfileButton.setOnAction(event -> {
            setEditable(true);
            fade(editConfirmationPane, 0, 100, 1000);
        });

        /*Cancels the edit mode*/
        cancelButton.setOnAction(event -> {
            loadDetails();
            setEditable(false);
        });

        /*Applies the modifications*/
        applyButton.setOnAction(event -> {
            updateDetails();
            setEditable(false);
            showNotification("Updated", "User details updated", new ImageView(new Image("images/tick.png")));
        });

         /*Deletes the account*/
        deleteAccountButton.setOnAction(event -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Delete account");
            confirmation.setContentText("Are you sure you want to permanently delete this account?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if(result.get() == ButtonType.OK) {
                Teacher teacher = (Teacher) getUserDAO().findById(getCurrentUser().getUserId());
                String username = teacher.getUsername();
                getUserDAO().delete(getCurrentUser().getUserId());

                ImageView deleteIcon = new ImageView(new Image("images/tick.png"));
                String deleteMessage = "Account " + username + " was deleted";
                showNotification("Deleted", deleteMessage, deleteIcon);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Account deleted");
                alert.setHeaderText("Please restart the application");
                Optional<ButtonType> confirm = alert.showAndWait();
                if(result.get() == ButtonType.OK) {
                    Stage stage = (Stage) deleteAccountButton.getScene().getWindow();
                    stage.close();
                }
            }
        });
    }

    /*Updates the account details*/
    private void updateDetails() {
        Teacher teacher = (Teacher) getUserDAO().findById(getCurrentUser().getUserId());
        teacher.setFirstName(firstNameTextField.getText());
        teacher.setLastName(lastNameTextField.getText());
        teacher.setQualification(qualificationTextField.getText());
        getUserDAO().update(teacher);
        firstNameLabel.setText(firstNameTextField.getText());
        lastNameLabel.setText(lastNameTextField.getText());
        qualificationLabel.setText(qualificationTextField.getText());
    }

    /*Loads account details*/
    private void loadDetails() {
        Teacher teacher = (Teacher) getUserDAO().findById(getCurrentUser().getUserId());
        usernameLabel.setText(teacher.getUsername());
        firstNameLabel.setText(teacher.getFirstName());
        lastNameLabel.setText(teacher.getLastName());
        emailLabel.setText(teacher.getEmail());
        qualificationLabel.setText(teacher.getQualification());
    }

    /*Changes from edit mode/show mode*/
    void setEditable(boolean editable) {
        firstNameTextField.setVisible(editable);
        lastNameTextField.setVisible(editable);
        qualificationTextField.setVisible(editable);
        editConfirmationPane.setVisible(editable);
        firstNameLabel.setVisible(!editable);
        lastNameLabel.setVisible(!editable);
        qualificationLabel.setVisible(!editable);
        firstNameTextField.setText(firstNameLabel.getText());
        lastNameTextField.setText(lastNameLabel.getText());
        qualificationTextField.setText(qualificationLabel.getText());
    }
}
