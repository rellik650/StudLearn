import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import dao.*;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javafx.util.Duration;

import model.User;
import org.controlsfx.control.Notifications;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;

public class Controller {

    /*Configuration and DAO*/
    private ConfigurableApplicationContext context;
    private UserDAO userDAO;
    private AddressDAO addressDAO;
    private CourseDAO courseDAO;
    private LoginDAO loginDAO;

    /*Model*/
    private User currentUser;

    /*Constructor*/
    public Controller() {
        context =  new ClassPathXmlApplicationContext("applicationContext.xml");
        setUserDAO((UserDAO) context.getBean("UserDAO"));
        setAddressDAO((AddressDAO) context.getBean("AddressDAO"));
        setCourseDAO((CourseDAO) context.getBean("CourseDAO"));
        setLoginDAO((LoginDAO) context.getBean("LoginDAO"));
    }

    /*Methods*/
    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AddressDAO getAddressDAO() {
        return addressDAO;
    }

    public void setAddressDAO(AddressDAO addressDAO) {
        this.addressDAO = addressDAO;
    }

    public CourseDAO getCourseDAO() {
        return courseDAO;
    }

    public void setCourseDAO(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public LoginDAO getLoginDAO() {
        return loginDAO;
    }

    public void setLoginDAO(LoginDAO loginDAO) {
        this.loginDAO = loginDAO;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /*Changes the .fxml view */
    public void changeView(Pane rootPane, String view) {
        AnchorPane pane = null;

        try {
            pane = FXMLLoader.load(getClass().getResource(view));
        } catch (IOException e) {
            e.printStackTrace();
        }

        rootPane.getChildren().setAll(pane);
    }

    /*Overloaded method: Changes the .fxml view by passing user data and sets a specified controller*/
    public void changeView(Pane rootPane, String view, User user, Controller controller) {
            controller.setCurrentUser(user);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
            loader.setController(controller);
            AnchorPane pane = null;

            try {
                pane = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            rootPane.getChildren().setAll(pane);
    }

    /*Fade transition effect applicable on different nodes*/
    public void fade(Node node, int fromValue, int toValue, int duration) {
        FadeTransition fadeTransition = new FadeTransition(new Duration(duration), node);
        fadeTransition.setFromValue(fromValue);
        fadeTransition.setToValue(toValue);
        fadeTransition.setCycleCount(1);
        fadeTransition.setInterpolator(Interpolator.LINEAR);
        fadeTransition.play();
    }

    /*Pops-up a notification in the bottom-right corner of the screen*/
    public void showNotification(String title, String text,Node graphic) {
        Notifications notification = Notifications.create()
                .title(title)
                .text(text)
                .graphic(graphic)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT);
        notification.show();

    }

    /*Returns a side menu with a hamburger button*/
    public VBox getSideMenu(JFXDrawer menuDrawer, JFXHamburger hamburger, String sideMenuView) {
        VBox sideMenu = null;

        try {
            sideMenu = FXMLLoader.load(getClass().getResource(sideMenuView));
            menuDrawer.setSidePane(sideMenu);
            menuDrawer.setOnDrawerClosed(event -> menuDrawer.toBack());
            menuDrawer.setOnDrawerOpening((event) -> menuDrawer.toFront());

            HamburgerBackArrowBasicTransition arrow = new HamburgerBackArrowBasicTransition(hamburger);
            arrow.setRate(-1);
            hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                arrow.setRate(arrow.getRate() * -1);
                arrow.play();

                if(menuDrawer.isShown()) {
                    menuDrawer.close();
                } else {
                    menuDrawer.open();

                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sideMenu;
    }

}
