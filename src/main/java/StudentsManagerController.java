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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import model.Course;
import model.Teacher;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

public class StudentsManagerController extends Controller implements Initializable {

    /*Injected via FXML*/
    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXTreeTableView<Student> treeTable;

    @FXML
    private JFXButton dowloadButton;

    @FXML
    private JFXButton gradeButton;

    @FXML
    private JFXTextField filterTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        /*Filters the list*/
        filterTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                treeTable.setPredicate(new Predicate<TreeItem<Student>>() {
                    @Override
                    public boolean test(TreeItem<Student> studentTreeItem) {

                        Student item = studentTreeItem.getValue();
                        boolean lastNameFlag = false;
                        boolean courseFlag = false;
                        boolean gradeFlag = false;
                        if (newValue.toLowerCase().contains(item.studentLastName.getValue().toLowerCase())) {
                            lastNameFlag = true;
                        }
                        if (newValue.toLowerCase().contains(item.enrolledCourse.getValue().toLowerCase())) {
                            courseFlag = true;
                        }
                        if (newValue.toLowerCase().contains(item.finalGrade.getValue().toLowerCase())) {
                            gradeFlag = true;
                        }

                        if (newValue.equals("") || newValue.equals(" ")) {
                            return true;
                        } else {
                            return lastNameFlag || courseFlag || gradeFlag;
                        }

                    }
                });
            }
        });

        /*Downloads the file submitted by selected student*/
        dowloadButton.setOnAction(event -> {
            TreeItem<Student> selectedItem = treeTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {

                try {
                    String courseName = selectedItem.getValue().enrolledCourse.getValue().replaceAll("\\d+.*", "");
                    int courseId = getCourseDAO().findByName(courseName).getCourseId();
                    int studentId = Integer.parseInt(selectedItem.getValue().studentId.getValue());
                    int registrationId = getCourseDAO().getRegistrationId(studentId, courseId);
                    InputStream inputStream = getCourseDAO().review(registrationId);

                    String home = System.getProperty("user.home");
                    String lastName = selectedItem.getValue().studentLastName.getValue();
                    String fileName = lastName + "_" + selectedItem.getValue().enrolledCourse.getValue();
                    File targedFile = new File(home + "\\Downloads\\" + fileName + ".zip");
                    FileUtils.copyInputStreamToFile(inputStream, targedFile);

                    ImageView downloadIcon = new ImageView(new Image("images/tick.png"));
                    String message = "The student's submission was downloaded\nDirectory: " + home + "\\Downloads\\";
                    showNotification("Downloaded", message, downloadIcon);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    ImageView uploadIcon = new ImageView(new Image("images/info.png"));
                    String uploadMessage = "Student has not uploaded the submission yet";
                    showNotification("Not uploaded yet", uploadMessage, uploadIcon);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        /*Grade the selected student*/
        gradeButton.setOnAction(event -> {
            TreeItem<Student> selectedItem = treeTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Dialog dialog = new TextInputDialog();
                dialog.setTitle("Final grade");
                dialog.setHeaderText(null);
                dialog.setContentText("Grade: ");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {

                    try {
                        int grade = Integer.parseInt(result.get());
                        int studentId = Integer.parseInt(selectedItem.getValue().studentId.getValue());
                        int courseId = getCourseId(selectedItem.getValue().enrolledCourse.getValue());
                        int registrationId = getCourseDAO().getRegistrationId(studentId, courseId);

                        if (getCourseDAO().isSubmitted(registrationId)) {
                            getCourseDAO().setGrade(registrationId, grade);
                            if (grade > 4) {
                                getCourseDAO().completeCourse(registrationId);
                            }
                            loadData();
                            ImageView gradeIcon = new ImageView(new Image("images/tick.png"));
                            showNotification("Graded", "The student was graded", gradeIcon);
                        } else {
                            String notSubmittedMessage = "You can't grade a student that not submitted any file";
                            ImageView notSubmittedIcon = new ImageView(new Image("images/close.png"));
                            showNotification("Not submitted", notSubmittedMessage, notSubmittedIcon);
                        }

                    } catch (NumberFormatException e) {
                        ImageView invalidIcon = new ImageView(new Image("images/close.png"));
                        showNotification("Invalid input", "Please insert a number", invalidIcon);
                    }

                }
            }
        });
    }

    /*Extract the course ID from the custom course name*/
    private int getCourseId(String courseName) {
        String[] tokens = courseName.split("\\s");
        return Integer.parseInt(tokens[tokens.length - 1]);
    }

    /*Loads the list of students*/
    private void loadData() {
        //Column settings
        JFXTreeTableColumn<Student, String> studentIdColumn = new JFXTreeTableColumn<>("ID");
        studentIdColumn.setPrefWidth(34);
        studentIdColumn.setCellValueFactory(param -> param.getValue().getValue().studentId);

        JFXTreeTableColumn<Student, String> firstNameColumn = new JFXTreeTableColumn<>("First name");
        firstNameColumn.setPrefWidth(131);
        firstNameColumn.setCellValueFactory(param -> param.getValue().getValue().studentFirstName);

        JFXTreeTableColumn<Student, String> lastNameColumn = new JFXTreeTableColumn<>("Last name");
        lastNameColumn.setPrefWidth(131);
        lastNameColumn.setCellValueFactory(param -> param.getValue().getValue().studentLastName);

        JFXTreeTableColumn<Student, String> emailColumn = new JFXTreeTableColumn<>("Email");
        emailColumn.setPrefWidth(161);
        emailColumn.setCellValueFactory(param -> param.getValue().getValue().studentEmail);

        JFXTreeTableColumn<Student, String> courseColumn = new JFXTreeTableColumn<>("Course");
        courseColumn.setPrefWidth(131);
        courseColumn.setCellValueFactory(param -> param.getValue().getValue().enrolledCourse);

        JFXTreeTableColumn<Student, String> gradeColumn = new JFXTreeTableColumn<>("Final Grade");
        gradeColumn.setPrefWidth(101);
        gradeColumn.setCellValueFactory(param -> param.getValue().getValue().finalGrade);

        //ObservableList
        ObservableList<Student> studentObservableList = FXCollections.observableArrayList();

        Teacher teacher = (Teacher) getUserDAO().findById(getCurrentUser().getUserId());
        List<Course> courseList = getCourseDAO().findAll();
        List<model.Student> studentList = getUserDAO().findAllStudents();

        for (int c = 0; c < courseList.size(); c++) {
            if (courseList.get(c).getTeacher().getTeacherId() == teacher.getTeacherId()) {
                for (int s = 0; s < studentList.size(); s++) {
                    int courseId = courseList.get(c).getCourseId();
                    int registrationId = getCourseDAO().getRegistrationId(studentList.get(s).getStudentId(), courseId);
                    if (registrationId != -1) {
                        model.Student student = studentList.get(s);
                        int grade = getCourseDAO().getGrade(registrationId);
                        String finalGrade = "Not graded";
                        if (grade != -1 && grade != 0) {
                            finalGrade = String.valueOf(grade);
                        }
                        String studentId = String.valueOf(student.getStudentId());
                        String studentFirstName = student.getFirstName();
                        String studentLastName = student.getLastName();
                        String email = student.getEmail();
                        String enrolledCourse = courseList.get(c).getCourseName() + " " + String.valueOf(courseId);

                        studentObservableList.add(new Student(studentId, studentFirstName, studentLastName, email,
                                finalGrade, enrolledCourse));
                    }
                }
            }
        }
        final TreeItem<Student> root = new RecursiveTreeItem<>(studentObservableList, RecursiveTreeObject::getChildren);
        treeTable.getColumns().setAll(studentIdColumn, firstNameColumn, lastNameColumn, emailColumn, courseColumn,
                gradeColumn);
        treeTable.setRoot(root);
        treeTable.setShowRoot(false);
    }

    //wrapped class
    class Student extends RecursiveTreeObject<Student> {
        StringProperty studentId;
        StringProperty studentFirstName;
        StringProperty studentLastName;
        StringProperty studentEmail;
        StringProperty finalGrade;
        StringProperty enrolledCourse;

        public Student(String studentId, String firstName, String lastName, String email, String finalGrade, String enrolledCourse) {
            this.studentId = new SimpleStringProperty(studentId);
            this.studentFirstName = new SimpleStringProperty(firstName);
            this.studentLastName = new SimpleStringProperty(lastName);
            this.studentEmail = new SimpleStringProperty(email);
            this.finalGrade = new SimpleStringProperty(finalGrade);
            this.enrolledCourse = new SimpleStringProperty(enrolledCourse);
        }

        public StringProperty getFinalGrade() {
            return this.finalGrade;
        }

        public void setFinalGrade(String finalGrade) {
            this.finalGrade = new SimpleStringProperty(finalGrade);
        }
    }
}
