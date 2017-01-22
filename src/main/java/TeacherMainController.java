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


public class TeacherMainController extends Controller implements Initializable {

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
    public TeacherMainController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fade(childPane, 0, 100, 1000);
        VBox sideMenu = getSideMenu(menuDrawer, hamburger, "TeacherSideMenu.fxml");

        /*Menu buttons*/
        for (Node node : sideMenu.getChildren()) {
            if (node.getAccessibleText() != null) {
                node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                    switch (node.getAccessibleText()) {
                        case "details":
                            showTeacherDetails();
                            break;
                        case "students":
                            showStudents();
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

    /*Destroys all the data about current user and logs out*/
    private void logOut() {
        setCurrentUser(null);
        changeView(rootPane, "LoginView.fxml");
    }

    /*Shows the list of students*/
    private void showStudents() {
        changeView(childPane, "StudentsManagerView.fxml", getCurrentUser(), new StudentsManagerController());
    }

    /*Shows account details*/
    private void showTeacherDetails() {
        changeView(childPane, "TeacherDetailsView.fxml", getCurrentUser(), new TeacherDetailsController());
    }

    /*Shows the list of courses*/
    private void showCourses() {
        changeView(childPane, "CoursesManagerView.fxml", getCurrentUser(), new CoursesManagerController());
    }

}
