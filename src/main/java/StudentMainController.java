
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentMainController extends Controller implements Initializable {

    /*Injected via FXML*/
    @FXML
    private AnchorPane rootPane;

    @FXML
    private AnchorPane childPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer menuDrawer;

    /*Constructor*/
    public StudentMainController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fade(childPane, 0, 100, 1000);
        VBox sideMenu = getSideMenu(menuDrawer, hamburger, "StudentSideMenu.fxml");

        /*Menu buttons*/
        for (Node node : sideMenu.getChildren()) {
            if (node.getAccessibleText() != null) {
                node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                    switch (node.getAccessibleText()) {
                        case "details":
                            showStudentDetails();
                            break;
                        case "courses":
                            showCourses();
                            break;
                        case "out":
                            logOut();
                            break;
                    }
                });
            }
        }


    }

    /*Destroys all the date about current user and logs out*/
    private void logOut() {
        setCurrentUser(null);
        changeView(rootPane, "LoginView.fxml");
    }

    /*Shows the course list*/
    private void showCourses() {
        changeView(childPane, "StudentCoursesView.fxml", getCurrentUser(), new StudentCoursesController());
    }

    /*Shows the account details*/
    private void showStudentDetails() {
        changeView(childPane, "StudentDetailsView.fxml", getCurrentUser(), new StudentDetailsController());
    }


}
