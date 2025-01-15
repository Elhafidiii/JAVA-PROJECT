package com.example.manic_time;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class TacheController {

    @FXML
    DatePicker datePicker;

    @FXML
    TextField tasktitleField;

    @FXML
    TextField taskdescriptionField;

    @FXML
    private Button addTaskButton;

    @FXML
    TableView<Tache> daytasktable; // Tableau des tâches d'aujourd'hui

    @FXML
    private TableView<Tache> othertasktable; // Tableau des autres tâches

    @FXML
    private TableColumn<Tache, String> titlecolomday;

    @FXML
    private TableColumn<Tache, String> descriptioncolomday;

    @FXML
    private TableColumn<Tache, Boolean> statuscolomday;

    @FXML
    private TableColumn<Tache, String> titlecolomother;

    @FXML
    private TableColumn<Tache, String> descriptioncolomother;

    @FXML
    private TableColumn<Tache, LocalDate> datecolomother;

    @FXML
    private TableColumn<Tache, Boolean> statusCheckCol; // Colonne pour le checkbox

    private ObservableList<Tache> todayTasks = FXCollections.observableArrayList();
    private ObservableList<Tache> otherTasks = FXCollections.observableArrayList();

    // Méthode appelée lors du clic sur le bouton "Add Task"
    @FXML
    public void onAddTaskClick() {
        // Récupérer les valeurs des champs
        LocalDate date = datePicker.getValue();
        String title = tasktitleField.getText();
        String description = taskdescriptionField.getText();

        // Vérifier si les champs ne sont pas vides
        if (date == null || title.isEmpty() || description.isEmpty()) {
            return; // Ne rien faire si un champ est vide
        }

        // Ajouter la tâche dans la base de données
        addTaskToDatabase(date, title, description);

        // Réinitialiser les champs après l'ajout
        datePicker.setValue(null);
        tasktitleField.clear();
        taskdescriptionField.clear();

        // Recharger les tâches
        loadTasks();
    }

    @FXML
    public void onModifyTaskClick() {
        Tache selectedTask = daytasktable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            selectedTask = othertasktable.getSelectionModel().getSelectedItem();
        }

        if (selectedTask != null) {
            String newTitle = tasktitleField.getText();
            String newDescription = taskdescriptionField.getText();
            LocalDate newDate = datePicker.getValue();

            if (newTitle.isEmpty() || newDescription.isEmpty() || newDate == null) {
                // Vérifier si les champs sont remplis
                return; // Ne rien faire si un champ est vide
            }

            // Mettre à jour la tâche dans la base de données
            updateTaskInDatabase(selectedTask.getId(), newTitle, newDescription, newDate);

            // Recharger les tâches
            loadTasks();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a task to modify.", ButtonType.OK);
            alert.showAndWait();
        }
    }


    @FXML
    public void onDeleteTaskClick() {
        // Vérifier si une tâche est sélectionnée dans daytasktable
        Tache selectedTask = daytasktable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            // Si aucune tâche sélectionnée dans daytasktable, vérifier othertasktable
            selectedTask = othertasktable.getSelectionModel().getSelectedItem();
        }

        if (selectedTask != null) {
            // Supprimer la tâche de la base de données
            deleteTaskFromDatabase(selectedTask.getId());

            // Retirer la tâche des listes observables pour mettre à jour l'interface
            todayTasks.remove(selectedTask);
            otherTasks.remove(selectedTask);

            // Recharger les données dans les tableaux
            loadTasks();
        } else {
            // Afficher un message si aucune tâche n'est sélectionnée
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a task to delete.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Méthode pour ajouter la tâche à la base de données
    private void addTaskToDatabase(LocalDate date, String title, String description) {
        String insertQuery = "INSERT INTO tache (titre, description, dateTermine, utilisateur_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertQuery)) {

            // Définir les paramètres de la requête
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            stmt.setInt(4, loginController.currentUserId); // Associe la tâche à l'utilisateur connecté

            // Exécuter la requête d'insertion
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTaskInDatabase(int taskId, String title, String description, LocalDate date) {
        String updateQuery = "UPDATE tache SET titre = ?, description = ?, dateTermine = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            stmt.setInt(4, taskId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void deleteTaskFromDatabase(int taskId) {
        String deleteQuery = "DELETE FROM tache WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {

            stmt.setInt(1, taskId);

            // Exécuter la requête de suppression
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete the task.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Méthode pour charger les tâches dans les tableaux
    void loadTasks() {
        LocalDate today = LocalDate.now();

        todayTasks.clear();
        otherTasks.clear();

        String selectQuery = "SELECT id, titre, description, dateTermine, estTermine FROM tache WHERE utilisateur_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(selectQuery)) {

            stmt.setInt(1, loginController.currentUserId); // Filtre les tâches par utilisateur connecté

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("titre");
                String description = rs.getString("description");
                LocalDate dateTermine = rs.getDate("dateTermine").toLocalDate();
                boolean isCompleted = rs.getBoolean("estTermine");

                Tache task = new Tache(id, title, description, dateTermine, isCompleted);

                if (dateTermine.isEqual(today)) {
                    todayTasks.add(task);
                } else {
                    otherTasks.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Mettre à jour les tableaux avec les données
        daytasktable.setItems(todayTasks);
        othertasktable.setItems(otherTasks);
    }

    // Méthode appelée lors de la modification du statut des tâches (checkbox)
    @FXML
    public void onStatusChange() {
        for (Tache task : othertasktable.getItems()) {
            if (task.getCheckBox()) {
                updateTaskStatusInDatabase(task);
            }
        }
    }

    // Méthode pour mettre à jour le statut de la tâche dans la base de données
    private void updateTaskStatusInDatabase(Tache task) {
        String updateQuery = "UPDATE tache SET estTermine = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

            stmt.setBoolean(1, task.isCompleted()); // Mettre à jour selon la nouvelle valeur
            stmt.setInt(2, task.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Initialiser les colonnes des tableaux
    @FXML
    public void initialize() {

        // Dans la méthode initialize(), ajoutez les écouteurs pour la sélection des tâches
        daytasktable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tasktitleField.setText(newValue.getTitre());
                taskdescriptionField.setText(newValue.getDescription());
                datePicker.setValue(newValue.getDateTermine());
            }
        });

        othertasktable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tasktitleField.setText(newValue.getTitre());
                taskdescriptionField.setText(newValue.getDescription());
                datePicker.setValue(newValue.getDateTermine());
            }
        });


        // Configurer les colonnes de daytasktable
        titlecolomday.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptioncolomday.setCellValueFactory(new PropertyValueFactory<>("description"));
        statuscolomday.setCellValueFactory(new PropertyValueFactory<>("estTermine"));

        // Configuration du CheckBox pour daytasktable
        statuscolomday.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Tache task = getTableView().getItems().get(getIndex());
                    boolean newStatus = checkBox.isSelected();
                    task.setCheckBox(newStatus);
                    updateTaskStatusInDatabase(task);
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        });

        // Configurer les colonnes de othertasktable
        titlecolomother.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptioncolomother.setCellValueFactory(new PropertyValueFactory<>("description"));
        datecolomother.setCellValueFactory(new PropertyValueFactory<>("dateTermine"));
        statusCheckCol.setCellValueFactory(cellData -> cellData.getValue().checkBoxProperty());

        // Configuration du CheckBox pour othertasktable
        statusCheckCol.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Tache task = getTableView().getItems().get(getIndex());
                    boolean newStatus = checkBox.isSelected();
                    task.setCheckBox(newStatus);
                    updateTaskStatusInDatabase(task);
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        });

        // Charger les tâches au démarrage
        loadTasks();
    }

    public ObservableList<Tache> getTodayTasks() {
        return todayTasks;
    }


}
