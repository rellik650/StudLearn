import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <h1>StudLearn - E-learning application prototype</h1>
 * <p>Purpose: Educational-only</p>
 *
 * @author Calin Chiper
 * @version 1.0
 * @since 2016-12-03
 */

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));    //loads the login scene
        primaryStage.setTitle("StudLearn");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 800, 640));   // fixed resolution: 800x640
        primaryStage.show();
    }


}
