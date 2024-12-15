package com.example.manic_time;

import javafx.beans.property.*;
import kotlin.OverloadResolutionByLambdaReturnType;

import java.time.LocalDate;

public class Tache {

    private final StringProperty titre;
    private final StringProperty description;
    private final ObjectProperty<LocalDate> dateTermine;
    private final BooleanProperty estTermine;
    private final BooleanProperty checkBox;
    private final int id;

    // Constructeur
    public Tache(int id, String titre, String description, LocalDate dateTermine, boolean estTermine) {
        this.id = id;
        this.titre = new SimpleStringProperty(titre);
        this.description = new SimpleStringProperty(description);
        this.dateTermine = new SimpleObjectProperty<>(dateTermine);
        this.estTermine = new SimpleBooleanProperty(estTermine);
        this.checkBox = new SimpleBooleanProperty(estTermine); // Le checkbox est initialisé selon estTermine
    }

    // Getters et Setters
    public String getTitre() {
        return titre.get();
    }

    public StringProperty titreProperty() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre.set(titre);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public LocalDate getDateTermine() {
        return dateTermine.get();
    }

    public ObjectProperty<LocalDate> dateTermineProperty() {
        return dateTermine;
    }

    public void setDateTermine(LocalDate dateTermine) {
        this.dateTermine.set(dateTermine);
    }

    public boolean isEstTermine() {
        return estTermine.get();
    }

    public BooleanProperty estTermineProperty() {
        return estTermine;
    }

    public boolean isCompleted() {
        return estTermine.get();
    }

    public void setEstTermine(boolean estTermine) {
        this.estTermine.set(estTermine);
    }

    public boolean getCheckBox() {
        return checkBox.get();
    }

    public BooleanProperty checkBoxProperty() {
        return checkBox;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox.set(checkBox);
        setEstTermine(checkBox);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        // ID est géré par la base de données, donc il ne faut pas le changer
    }
}
