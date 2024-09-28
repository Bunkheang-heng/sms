import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class StudentUI {
    private final StudentDAO studentDAO = new StudentDAO(); // Use your StudentDAO
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private final TableView<Student> tableView = new TableView<>();

    public BorderPane getRoot() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Add button bar
        HBox buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(10));

        Button addButton = new Button("Add Student");
        Button updateButton = new Button("Update Student");
        Button deleteButton = new Button("Delete Student");

        buttonBar.getChildren().addAll(addButton, updateButton, deleteButton);

        // Table for displaying students
        TableColumn<Student, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());

        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<Student, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        tableView.getColumns().addAll(idColumn, nameColumn, emailColumn);
        tableView.setItems(studentList);

        // Refresh table data
        refreshStudentList();

        // Add all components to the layout
        root.setTop(buttonBar);
        root.setCenter(tableView);

        // Set button actions
        addButton.setOnAction(e -> showAddStudentDialog());
        updateButton.setOnAction(e -> showUpdateStudentDialog());
        deleteButton.setOnAction(e -> deleteSelectedStudent());

        return root;
    }

    private void refreshStudentList() {
        studentList.clear();
        List<Student> students = studentDAO.getAllStudents();
        studentList.addAll(students);
    }

    private void showAddStudentDialog() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add Student");

        // Create name and email fields
        TextField nameField = new TextField();
        TextField emailField = new TextField();

        VBox vbox = new VBox(10, new Label("Name:"), nameField, new Label("Email:"), emailField);
        vbox.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(vbox);

        // Add buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Handle adding the student
        dialog.setResultConverter(button -> {
            if (button == addButtonType) {
                String name = nameField.getText();
                String email = emailField.getText();
                Student student = new Student(0, name, email); // ID is auto-generated
                studentDAO.addStudent(student);
                refreshStudentList(); // Refresh the table
                return student;
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showUpdateStudentDialog() {
        Student selectedStudent = tableView.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("Please select a student to update.");
            return;
        }

        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Update Student");

        // Create name and email fields pre-filled with selected student's info
        TextField nameField = new TextField(selectedStudent.getName());
        TextField emailField = new TextField(selectedStudent.getEmail());

        VBox vbox = new VBox(10, new Label("Name:"), nameField, new Label("Email:"), emailField);
        vbox.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(vbox);

        // Add buttons
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Handle updating the student
        dialog.setResultConverter(button -> {
            if (button == updateButtonType) {
                selectedStudent.setName(nameField.getText());
                selectedStudent.setEmail(emailField.getText());
                studentDAO.updateStudent(selectedStudent);
                refreshStudentList(); // Refresh the table
                return selectedStudent;
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void deleteSelectedStudent() {
        Student selectedStudent = tableView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            studentDAO.deleteStudent(selectedStudent.getId());
            refreshStudentList();
        } else {
            showAlert("Please select a student to delete.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
