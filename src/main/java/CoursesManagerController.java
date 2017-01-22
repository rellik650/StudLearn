import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Teacher;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class CoursesManagerController extends Controller implements Initializable {

    /*Injected via FXML*/
    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXTreeTableView<Course> treeTable;

    @FXML
    private JFXButton uploadButton;

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton removeButton;

    @FXML
    private JFXButton setSpecificationButton;

    @FXML
    private JFXTextField filterTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();

        /*Filters the course list*/
        filterTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                treeTable.setPredicate(new Predicate<TreeItem<Course>>() {
                    @Override
                    public boolean test(TreeItem<Course> courseTreeItem) {
                        Course item = courseTreeItem.getValue();

                        boolean courseNameFlag = item.courseName.getValue().contains(newValue);
                        if(newValue.equals("") || newValue.equals(" ")) {
                            return true;
                        }
                        return courseNameFlag;
                    }
                });
            }
        });

        /*Uploads a course file*/
        uploadButton.setOnAction(event -> {
            TreeItem<Course> selectedItem = treeTable.getSelectionModel().getSelectedItem();

            try {
                if (selectedItem != null) {
                    int courseId = Integer.parseInt(selectedItem.getValue().courseId.getValue());
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Select a course(*.pdf)");
                    String description = "PDF files (*.pdf)";
                    String format = "*.pdf";
                    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(description, format);
                    fileChooser.getExtensionFilters().add(extensionFilter);
                    File file = fileChooser.showOpenDialog(new Stage());
                    String filePath = file.getAbsolutePath();
                    getCourseDAO().insertCourseFile(courseId, filePath);
                    ImageView uploadIcon = new ImageView(new Image("images/tick.png"));
                    showNotification("Uploaded", "Course uploaded", uploadIcon);
                }
            } catch (NullPointerException | IOException e) {
                ImageView notSelectedIcon = new ImageView(new Image("images/info.png"));
                showNotification("Not selected", "Select a course", notSelectedIcon);
                e.printStackTrace();
            }
        });

        /*Removes the selected course*/
        removeButton.setOnAction(event -> {
            TreeItem<Course> selectedItem = treeTable.getSelectionModel().getSelectedItem();
            if(selectedItem != null) {
                try {
                    treeTable.getRoot().getChildren().remove(treeTable.getSelectionModel().getSelectedIndex());
                    int courseId = Integer.parseInt(selectedItem.getValue().courseId.getValue());
                    getCourseDAO().delete(courseId);
                    ImageView deleteIcon = new ImageView(new Image("images/tick.png"));
                    showNotification("Deleted", "Course deleted", deleteIcon);
                } catch (NullPointerException e) {
                    ImageView notSelectedIcon = new ImageView(new Image("images/info.png"));
                    showNotification("Not selected", "Select a course", notSelectedIcon);
                    e.printStackTrace();
                }
            }
        });

        /*Adds a course*/
        addButton.setOnAction(event -> {
            Dialog dialog = new TextInputDialog();
            dialog.setTitle("Course settings");
            dialog.setHeaderText("");
            dialog.setGraphic(new ImageView(new Image("images/studLearnLogo.png")));
            dialog.setContentText("Course name: ");
            Optional<String> result = dialog.showAndWait();
            String courseName = "N/A";

            if (result.isPresent()) {
                courseName = result.get();
                Teacher teacher = (Teacher) getUserDAO().findById(getCurrentUser().getUserId());
                model.Course course = new model.Course(courseName, teacher);
                getCourseDAO().create(course);
                loadData();
                ImageView addedIcon = new ImageView(new Image("images/tick.png"));
                showNotification("Added", "Course " + courseName + " added", addedIcon);
            }

        });

        setSpecificationButton.setOnAction(event -> {
            TreeItem<Course> selectedItem = treeTable.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                Dialog dialog = new TextInputDialog();
                dialog.setTitle("Exam settings");
                dialog.setHeaderText("");
                dialog.setGraphic(new ImageView(new Image("images/studLearnLogo.png")));
                dialog.setContentText("Exam specification: ");
                Optional<String> result = dialog.showAndWait();
                String assignmentSpecification = "Not specified";
                if (result.isPresent()) {
                    assignmentSpecification = result.get();
                    Date currentDate = new Date();
                    int courseId = Integer.parseInt(selectedItem.getValue().courseId.getValue());
                    getCourseDAO().setAssignmentSpecification(courseId, assignmentSpecification, currentDate);
                    ImageView setUpIcon = new ImageView(new Image("images/tick.png"));
                    showNotification("Done", "The exam was set up", setUpIcon);
                }

            }

        });
    }

    /*Loads the list of courses*/
    private void loadData() {
        //Column settings
        JFXTreeTableColumn<Course, String> courseIdColumn = new JFXTreeTableColumn<>("ID");
        courseIdColumn.setPrefWidth(100);
        courseIdColumn.setCellValueFactory(param -> param.getValue().getValue().courseId);

        JFXTreeTableColumn<Course, String> courseNameColumn = new JFXTreeTableColumn<>("Course");
        courseNameColumn.setPrefWidth(591);
        courseNameColumn.setCellValueFactory(param -> param.getValue().getValue().courseName);

        ObservableList<Course> courseObservableList = FXCollections.observableArrayList();
        List<model.Course> courseList = getCourseDAO().findAll();
        Teacher teacher = (Teacher) getUserDAO().findById(getCurrentUser().getUserId());

        for (int c = 0; c < courseList.size(); c++) {
            if (courseList.get(c).getTeacher().getTeacherId() == teacher.getTeacherId()) {
                model.Course course = courseList.get(c);
                courseObservableList.add(new Course(String.valueOf(course.getCourseId()), course.getCourseName()));
            }
        }

        final TreeItem<Course> root = new RecursiveTreeItem<>(courseObservableList, RecursiveTreeObject::getChildren);
        treeTable.getColumns().setAll(courseIdColumn, courseNameColumn);
        treeTable.setRoot(root);
        treeTable.setShowRoot(false);
    }

    /*wrapped class*/
    class Course extends RecursiveTreeObject<Course> {
        private StringProperty courseId;
        private StringProperty courseName;

        Course(String courseId, String courseName) {
            this.courseId = new SimpleStringProperty(courseId);
            this.courseName = new SimpleStringProperty(courseName);
        }

        public StringProperty getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = new SimpleStringProperty(courseName);
        }


    }
}
