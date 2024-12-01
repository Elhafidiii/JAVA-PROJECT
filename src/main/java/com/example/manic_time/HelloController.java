package com.example.manic_time;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.layout.BorderPane;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class HelloController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField taskField;

    @FXML
    private ListView<Task> tasksListView;  // ListView instead of TableView

    private ObservableList<Task> tasksList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tasksListView.setItems(tasksList);
        tasksListView.setCellFactory(param -> new TaskCell());  // Custom cell for task display

        loadTasksFromDatabase();
    }

    @FXML
    protected void onAddTaskClick() {
        LocalDate selectedDate = datePicker.getValue();
        String task = taskField.getText();

        if (selectedDate != null && task != null && !task.isEmpty()) {
            Task newTask = new Task(selectedDate, task);
            tasksList.add(newTask);
            saveTaskToDatabase(selectedDate, task);
            datePicker.setValue(null);
            taskField.clear();
        } else {
            System.out.println("Please select a date and enter a task!");
        }
    }

    private void saveTaskToDatabase(LocalDate date, String taskDescription) {
        String insertQuery = "INSERT INTO tasks (task_date, task_description) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             var preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setDate(1, java.sql.Date.valueOf(date));
            preparedStatement.setString(2, taskDescription);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting into database: " + e.getMessage());
        }
    }

    private void loadTasksFromDatabase() {
        String selectQuery = "SELECT task_date, task_description FROM tasks";

        try (Connection connection = DatabaseConnection.getConnection();
             var statement = connection.createStatement();
             var resultSet = statement.executeQuery(selectQuery)) {

            tasksList.clear();

            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("task_date").toLocalDate();
                String description = resultSet.getString("task_description");

                tasksList.add(new Task(date, description));
            }
        } catch (SQLException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    public static class Task {
        private final LocalDate date;
        private final String task;
        private boolean completed;

        public Task(LocalDate date, String task) {
            this.date = date;
            this.task = task;
            this.completed = false;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getTask() {
            return task;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        @Override
        public String toString() {
            return date + " - " + task + (completed ? " (Completed)" : " (Pending)");
        }
    }

    @FXML
    protected void onDeleteTaskClick() {
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            tasksList.remove(selectedTask);
            deleteTaskFromDatabase(selectedTask);
        } else {
            System.out.println("Please select a task to delete!");
        }
    }

    private void deleteTaskFromDatabase(Task task) {
        String deleteQuery = "DELETE FROM tasks WHERE task_date = ? AND task_description = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             var preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setDate(1, java.sql.Date.valueOf(task.getDate()));
            preparedStatement.setString(2, task.getTask());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting task from database: " + e.getMessage());
        }
    }

    @FXML
    protected void onModifyTaskClick() {
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            System.out.println("Modify task: " + selectedTask.getTask());
        } else {
            System.out.println("Please select a task to modify!");
        }
    }

    @FXML
    protected void onMarkAsCompleteClick() {
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setCompleted(true);
            tasksListView.refresh();
        } else {
            System.out.println("Please select a task to mark as complete!");
        }
    }

    @FXML
    protected void onMarkAsIncompleteClick() {
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setCompleted(false);
            tasksListView.refresh();
        } else {
            System.out.println("Please select a task to mark as incomplete!");
        }
    }

    // Custom ListCell to change task color based on completion status
    private static class TaskCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);

            if (task != null) {
                setText(task.toString());

                // Change color based on completion status
                if (task.isCompleted()) {
                    setStyle("-fx-background-color: green ; -fx-text-fill: white;");
                } else {
                    setStyle("-fx-background-color: red; -fx-text-fill: white;");
                }

            } else {
                setText(null);
                setStyle(null);
            }
        }
    }

    @FXML
    private BorderPane mainPane; // L'élément BorderPane de votre vue principale

    @FXML
    protected void onTMSystemClick() {
        try {
            // Charger la vue TM System
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tm-system-view.fxml"));
            Parent tmSystemView = loader.load();

            // Remplacer le contenu central
            mainPane.setCenter(tmSystemView);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la vue TM System.");
        }
    }


    @FXML
    protected void onDashboardClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard-view.fxml"));
            Parent dashboardView = loader.load();
            mainPane.setCenter(dashboardView); // Charger la vue "Dashboard"
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}








