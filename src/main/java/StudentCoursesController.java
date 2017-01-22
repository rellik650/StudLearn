
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
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Student;
import model.Teacher;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;


public class StudentCoursesController extends Controller implements Initializable {

    /*Injected via FXML*/
    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXTreeTableView<Course> treeTable;

    @FXML
    private JFXButton enrollButton;

    @FXML
    private JFXButton unenrollButton;

    @FXML
    private JFXButton downloadButton;

    @FXML
    private JFXButton viewAssignmentButton;

    @FXML
    private JFXButton viewGradeButton;

    @FXML
    private JFXButton uploadButton;

    @FXML
    private JFXTextField filterTextField;

    /*Constructor*/
    public StudentCoursesController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();

        /*Enroll to selected course*/
        enrollButton.setOnAction(event -> {
            TreeItem<Course> selectedItem = treeTable.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                String status = selectedItem.getValue().enrollementStatus.getValue();

                if (!status.equals("Enrolled")) {
                    String courseId = selectedItem.getValue().courseId.getValue();
                    String courseName = selectedItem.getValue().courseName.getValue();
                    String teacherName = selectedItem.getValue().teacherName.getValue();
                    String grade = selectedItem.getValue().grade.getValue();
                    String enrollmentStatus = "Enrolled";
                    selectedItem.setValue(new Course(courseId, courseName, teacherName, enrollmentStatus, grade));

                    int studentId = ((Student) getUserDAO().findById(getCurrentUser().getUserId())).getStudentId();
                    getCourseDAO().enroll(studentId, Integer.parseInt(courseId));

                    ImageView enrolledIcon = new ImageView(new Image("images/tick.png"));
                    showNotification("Enrolled", "Enrolled to '" + courseName + "' course", enrolledIcon);
                } else {
                    ImageView alreadyEnrolledIcon = new ImageView(new Image("images/info.png"));
                    String alreadyEnrolledMessage = "You are already enrolled to this course";
                    showNotification("Already enrolled", alreadyEnrolledMessage, alreadyEnrolledIcon);
                }
            }
        });

        /*Unenrolls from the selected course*/
        unenrollButton.setOnAction(event -> {
            TreeItem<Course> selectedItem = treeTable.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                String status = selectedItem.getValue().enrollementStatus.getValue();

                if (!status.equals("Not enrolled")) {
                    String courseId = selectedItem.getValue().courseId.getValue();
                    String courseName = selectedItem.getValue().courseName.getValue();
                    String teacherName = selectedItem.getValue().teacherName.getValue();
                    String grade = selectedItem.getValue().grade.getValue();
                    String enrollmentStatus = "Not enrolled";
                    selectedItem.setValue(new Course(courseId, courseName, teacherName, enrollmentStatus, grade));

                    int studentId = ((Student) getUserDAO().findById(getCurrentUser().getUserId())).getStudentId();
                    int registrationId = getCourseDAO().getRegistrationId(studentId, Integer.parseInt(courseId));
                    getCourseDAO().unenroll(registrationId);
                    loadData();
                    ImageView unrolledIcon = new ImageView(new Image("images/tick.png"));
                    String unrolledMessage = "Unenrolled from '" + courseName + "' course";
                    showNotification("Unrolled", unrolledMessage, unrolledIcon);
                } else {
                    ImageView alreadyUnenrolledIcon = new ImageView(new Image("images/info.png"));
                    String alreadyUnenrolledMessage = "You are already unenrolled from this course";
                    showNotification("Already unenrolled", alreadyUnenrolledMessage, alreadyUnenrolledIcon);
                }
            }
        });

        /*Downloads the course file*/
        downloadButton.setOnAction(event -> {
            TreeItem<Course> selectedItem = treeTable.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                int studentId = ((Student) getUserDAO().findById(getCurrentUser().getUserId())).getStudentId();
                int courseId = Integer.parseInt(selectedItem.getValue().courseId.getValue());
                if (getEnrollmentStatus(courseId, studentId).equals("Enrolled")) {

                    try {
                        InputStream inputStream = getCourseDAO().readCourseFile(courseId);
                        String home = System.getProperty("user.home");
                        String fileName = selectedItem.getValue().courseName.getValue();
                        File targedFile = new File(home + "\\Downloads\\" + fileName + ".pdf");
                        FileUtils.copyInputStreamToFile(inputStream, targedFile);

                        ImageView downloadIcon = new ImageView(new Image("images/tick.png"));
                        String downloadMessage = "The course was downloaded\nDirectory: " + home + "\\Downloads\\";
                        showNotification("Downloaded", downloadMessage, downloadIcon);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        ImageView notAvailableIcon = new ImageView(new Image("images/info.png"));
                        String notAvailableMessage = "The course is not available(not uploaded)";
                        showNotification("Not available", notAvailableMessage, notAvailableIcon);
                    }

                } else {
                    String notEnrolledMessage = "To access the course, please enroll first";
                    ImageView notEnrolledIcon = new ImageView(new Image("images/close.png "));
                    showNotification("Not enrolled", notEnrolledMessage, notEnrolledIcon);
                }
            }
        });

        /*Shows assignment specification for the selected course*/
        viewAssignmentButton.setOnAction(event -> {
            TreeItem<Course> selectedItem = treeTable.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                int courseId = Integer.parseInt(selectedItem.getValue().courseId.getValue());
                int studentId = ((Student) getUserDAO().findById(getCurrentUser().getUserId())).getStudentId();

                if (getEnrollmentStatus(courseId, studentId).equals("Enrolled")) {

                    try {
                        String specification = getCourseDAO().getAssignmentSpecification(courseId);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Assignment");
                        alert.setHeaderText(null);
                        alert.setContentText(specification);
                        alert.showAndWait();
                    } catch (NullPointerException e) {
                        ImageView notSetUpIcon = new ImageView(new Image("images/info.png"));
                        showNotification("Not set up", "The exam was not set up. Try later", notSetUpIcon);
                    }

                } else {
                    String notEnrolledMessage = "To access the assignment specification, please enroll first";
                    ImageView notEnrolledIcon = new ImageView(new Image("images/close.png "));
                    showNotification("Not enrolled", notEnrolledMessage, notEnrolledIcon);
                }
            }
        });

        /*Uploads the submission to selected course*/
        uploadButton.setOnAction(event -> {
            TreeItem<Course> selectedItem = treeTable.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                int courseId = Integer.parseInt(selectedItem.getValue().courseId.getValue());
                int studentId = ((Student) getUserDAO().findById(getCurrentUser().getUserId())).getStudentId();

                if (getEnrollmentStatus(courseId, studentId).equals("Enrolled")) {
                    int registrationId = getCourseDAO().getRegistrationId(studentId, courseId);
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Select a course(*.zip)");
                    String description = "ZIP files (*.zip)";
                    String format = "*.zip";
                    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(description, format);
                    fileChooser.getExtensionFilters().add(extensionFilter);
                    File file = fileChooser.showOpenDialog(new Stage());

                    try {
                        String filePath = file.getAbsolutePath();
                        getCourseDAO().submit(registrationId, filePath);
                        ImageView uploadedIcon = new ImageView(new Image("images/tick.png"));
                        showNotification("Uploaded", "Submission uploaded", uploadedIcon);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {

                    }
                } else {
                    ImageView notEnrolledIcon = new ImageView(new Image("images/close.png "));
                    String notEnrolledMessage = "To upload your submission, please enroll first";
                    showNotification("Not enrolled", notEnrolledMessage, notEnrolledIcon);
                }
            }
        });

        //Filters courses names
        filterTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                treeTable.setPredicate(new Predicate<TreeItem<Course>>() {
                    @Override
                    public boolean test(TreeItem<Course> courseTreeItem) {
                        boolean courseNameFlag = false;
                        boolean statusFlag = false;
                        boolean teacherFlag = false;
                        Course item = courseTreeItem.getValue();
                        if (newValue.toLowerCase().contains(item.courseName.getValue().toLowerCase())) {
                            courseNameFlag = true;
                        }
                        if (newValue.equalsIgnoreCase(item.enrollementStatus.getValue().toLowerCase())) {
                            statusFlag = true;
                        }
                        if (newValue.equalsIgnoreCase(item.teacherName.getValue().toLowerCase())) {
                            teacherFlag = true;
                        }

                        if (newValue.equals("") || newValue.equals(" ")) {
                            return true;
                        } else {
                            return courseNameFlag || statusFlag || teacherFlag;
                        }
                    }
                });
            }
        });

    }

    private void loadData() {
        //Columns settings
        JFXTreeTableColumn<Course, String> courseIdColumn = new JFXTreeTableColumn<>("ID");
        courseIdColumn.setPrefWidth(60);
        courseIdColumn.setCellValueFactory(param -> param.getValue().getValue().courseId);

        JFXTreeTableColumn<Course, String> courseNameColumn = new JFXTreeTableColumn<>("Name");
        courseNameColumn.setPrefWidth(157);
        courseNameColumn.setCellValueFactory(param -> param.getValue().getValue().courseName);

        JFXTreeTableColumn<Course, String> teacherNameColumn = new JFXTreeTableColumn<>("Teacher");
        teacherNameColumn.setPrefWidth(157);
        teacherNameColumn.setCellValueFactory(param -> param.getValue().getValue().teacherName);

        JFXTreeTableColumn<Course, String> enrollmentColumn = new JFXTreeTableColumn<>("Status");
        enrollmentColumn.setPrefWidth(157);
        enrollmentColumn.setCellValueFactory(param -> param.getValue().getValue().enrollementStatus);

        JFXTreeTableColumn<Course, String> gradeColumn = new JFXTreeTableColumn<>("Grade");
        gradeColumn.setPrefWidth(157);
        gradeColumn.setCellValueFactory(param -> param.getValue().getValue().grade);


        //Observable list settings
        ObservableList<Course> coursesObservableList = FXCollections.observableArrayList();
        List<model.Course> extractedCourses = getCourseDAO().findAll();

        for (int c = 0; c < extractedCourses.size(); c++) {
            int teacherId = extractedCourses.get(c).getTeacher().getTeacherId();
            Teacher teacher = (Teacher) getUserDAO().findById(getUserDAO().getTeacherUserId(teacherId));
            extractedCourses.get(c).setTeacher(teacher);
            int currentStudentId = ((Student) getUserDAO().findById(getCurrentUser().getUserId())).getStudentId();

            String enrollmentStatus = getEnrollmentStatus(extractedCourses.get(c).getCourseId(), currentStudentId);
            String courseId = String.valueOf(extractedCourses.get(c).getCourseId());
            String courseName = extractedCourses.get(c).getCourseName();
            String teacherFirstName = extractedCourses.get(c).getTeacher().getFirstName();
            String teacherLastName = extractedCourses.get(c).getTeacher().getLastName();
            String teacherFullName = teacherFirstName + " " + teacherLastName;

            int registrationId = getCourseDAO().getRegistrationId(currentStudentId,Integer.parseInt(courseId));
            int grade = getCourseDAO().getGrade(registrationId);
            String finalGrade = "Not Graded";
            if(grade != -1 && grade != 0) {
                finalGrade = String.valueOf(grade);
            }
            coursesObservableList.add(new Course(courseId, courseName, teacherFullName, enrollmentStatus, finalGrade));
        }

        final TreeItem<Course> root = new RecursiveTreeItem<>(coursesObservableList, RecursiveTreeObject::getChildren);
        treeTable.getColumns().setAll(courseIdColumn, courseNameColumn, teacherNameColumn, enrollmentColumn, gradeColumn);
        treeTable.setRoot(root);
        treeTable.setShowRoot(false);
    }

    /*Returns the enrollment status of a student to a course*/
    private String getEnrollmentStatus(int courseId, int studentId) {
        Integer extractedStudentId = getCourseDAO().getEnrolledStudentId(courseId);
        if (extractedStudentId == -1 || extractedStudentId != studentId) {
            return "Not enrolled";
        } else {
            return "Enrolled";
        }
    }

    /*wrapped class*/
    class Course extends RecursiveTreeObject<Course> {
        StringProperty courseId;
        StringProperty courseName;
        StringProperty teacherName;
        StringProperty enrollementStatus;
        StringProperty grade;

        public Course(String courseId, String courseName, String teacherName, String enrollmentStatus, String grade) {
            this.courseId = new SimpleStringProperty(courseId);
            this.courseName = new SimpleStringProperty(courseName);
            this.teacherName = new SimpleStringProperty(teacherName);
            this.enrollementStatus = new SimpleStringProperty(enrollmentStatus);
            this.grade = new SimpleStringProperty(grade);
        }
    }

}
