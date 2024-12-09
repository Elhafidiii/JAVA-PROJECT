package com.example.manic_time;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.sql.*;
import java.time.LocalDate;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class tachesController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField taskField;

    @FXML
    private ListView<Task> todayTasksListView;

    @FXML
    private ListView<Task> otherTasksListView;

    private ObservableList<Task> todayTasks = FXCollections.observableArrayList();
    private ObservableList<Task> otherTasks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        todayTasksListView.setItems(todayTasks);
        todayTasksListView.setCellFactory(param -> new TaskCell());

        otherTasksListView.setItems(otherTasks);
        otherTasksListView.setCellFactory(param -> new TaskCell());

        loadTasksFromDatabase();
    }

    @FXML
    protected void onAddTaskClick() {
        LocalDate selectedDate = datePicker.getValue();
        String taskDescription = taskField.getText();

        if (selectedDate != null && taskDescription != null && !taskDescription.isEmpty()) {
            Task newTask = new Task(selectedDate, taskDescription);
            saveTaskToDatabase(selectedDate, taskDescription);

            if (selectedDate.isEqual(LocalDate.now())) {
                todayTasks.add(newTask);
            } else {
                otherTasks.add(newTask);
            }

            taskField.clear();
            datePicker.setValue(null);
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

    public ObservableList<Task> getTodayTasks() {
        return todayTasks;
    }

    public void loadTasksFromDatabase() {
        String selectQuery = "SELECT task_date, task_description FROM tasks";

        try (Connection connection = DatabaseConnection.getConnection();
             var statement = connection.createStatement();
             var resultSet = statement.executeQuery(selectQuery)) {

            todayTasks.clear();
            otherTasks.clear();
            LocalDate today = LocalDate.now();

            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("task_date").toLocalDate();
                String description = resultSet.getString("task_description");
                Task task = new Task(date, description);

                if (date.isEqual(today)) {
                    todayTasks.add(task);
                } else {
                    otherTasks.add(task);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading tasks from database: " + e.getMessage());
        }
    }


    @FXML
    protected void onDeleteTaskClick() {
        Task selectedTask = todayTasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            selectedTask = otherTasksListView.getSelectionModel().getSelectedItem();
        }

        if (selectedTask != null) {
            if (todayTasks.contains(selectedTask)) {
                todayTasks.remove(selectedTask);
            } else if (otherTasks.contains(selectedTask)) {
                otherTasks.remove(selectedTask);
            }
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
    protected void onMarkAsCompleteClick() {
        Task selectedTask = todayTasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            selectedTask = otherTasksListView.getSelectionModel().getSelectedItem();
        }

        if (selectedTask != null) {
            selectedTask.setCompleted(true);
            todayTasksListView.refresh();
            otherTasksListView.refresh();
        } else {
            System.out.println("Please select a task to mark as complete!");
        }
    }

    @FXML
    protected void onMarkAsIncompleteClick() {
        Task selectedTask = todayTasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            selectedTask = otherTasksListView.getSelectionModel().getSelectedItem();
        }

        if (selectedTask != null) {
            selectedTask.setCompleted(false);
            todayTasksListView.refresh();
            otherTasksListView.refresh();
        } else {
            System.out.println("Please select a task to mark as incomplete!");
        }
    }

    // Task class
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
            return task + (completed ? " (Completed)" : " (Pending)");
        }
    }

    // Custom cell class
    private static class TaskCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);

            if (task != null) {
                setText(task.toString());
                if (task.isCompleted()) {
                    setStyle("-fx-background-color: blue; -fx-text-fill: white;");
                } else {
                    setStyle("-fx-background-color: white; -fx-text-fill: black;");
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








