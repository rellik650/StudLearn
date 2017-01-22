import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import model.Address;
import model.Student;
import model.User;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class StudentDetailsController extends Controller implements Initializable {

    /*Injected via FXML*/
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label streetLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private JFXTextField firstNameTextField;

    @FXML
    private JFXTextField lastNameTextField;

    @FXML
    private JFXTextField streetTextField;

    @FXML
    private JFXTextField locationTextField;

    @FXML
    private JFXButton editProfileButton;

    @FXML
    private JFXButton deleteAccountButton;

    @FXML
    private HBox editConfirmationPane;

    @FXML
    private JFXButton applyButton;

    @FXML
    private JFXButton cancelButton;

    /*Constructor*/
    public StudentDetailsController() {
        super();
    }

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
            ImageView updatedIcon = new ImageView(new Image("images/tick.png"));
            showNotification("Updated", "User details updated", updatedIcon);
        });

        /*Deletes the account*/
        deleteAccountButton.setOnAction(event -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Delete account");
            confirmation.setContentText("Are you sure you want to permanently delete this account?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if(result.get() == ButtonType.OK) {
                Student student = (Student) getUserDAO().findById(getCurrentUser().getUserId());
                String username = student.getUsername();
                int addressId = student.getAddress().getAddressId();
                getUserDAO().delete(getCurrentUser().getUserId());
                getAddressDAO().delete(addressId);
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
        Student student = (Student) getUserDAO().findById(getCurrentUser().getUserId());
        student.setFirstName(firstNameTextField.getText());
        student.setLastName(lastNameTextField.getText());
        String street = streetTextField.getText();
        int streetNumber = Integer.parseInt(street.replaceAll("[^0-9]", ""));
        String streetName = street.replaceAll("\\d+.*", "");
        String location = locationTextField.getText();
        String[] cityAndState = location.split(",");
        String city = cityAndState[0];
        String state = cityAndState[1];
        Address updatedAddress = new Address(student.getAddress().getAddressId(), streetName, streetNumber, city, state);
        student.setAddress(updatedAddress);
        getUserDAO().update(student);
        getAddressDAO().update(updatedAddress);
        firstNameLabel.setText(firstNameTextField.getText());
        lastNameLabel.setText(lastNameTextField.getText());
        streetLabel.setText(streetTextField.getText());
        locationLabel.setText(locationTextField.getText());
    }

    /*Loads account details*/
    private void loadDetails() {
        User student = getUserDAO().findById(getCurrentUser().getUserId());
        Address address = getAddressDAO().findById(((Student) student).getAddress().getAddressId());
        ((Student) student).setAddress(address);
        firstNameLabel.setText(((Student) student).getFirstName());
        lastNameLabel.setText(((Student) student).getLastName());
        streetLabel.setText(((Student) student).getAddress().toString());
        emailLabel.setText(((Student) student).getEmail());
        String city = ((Student) student).getAddress().getCity();
        String state = ((Student) student).getAddress().getState();
        locationLabel.setText(city + ", " + state);
        usernameLabel.setText(student.getUsername());
        editConfirmationPane.setVisible(false);
    }

    /*Changes from edit mode/show mode*/
    private void setEditable(boolean editable) {
        firstNameTextField.setVisible(editable);
        lastNameTextField.setVisible(editable);
        locationTextField.setVisible(editable);
        streetTextField.setVisible(editable);
        firstNameLabel.setVisible(!editable);
        lastNameLabel.setVisible(!editable);
        locationLabel.setVisible(!editable);
        streetLabel.setVisible(!editable);
        firstNameTextField.setText(firstNameLabel.getText());
        lastNameTextField.setText(lastNameLabel.getText());
        locationTextField.setText(locationLabel.getText());
        streetTextField.setText(streetLabel.getText());
        editConfirmationPane.setVisible(editable);
    }

}
