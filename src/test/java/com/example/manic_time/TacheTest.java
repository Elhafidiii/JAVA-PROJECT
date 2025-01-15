package com.example.manic_time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TacheTest {

    private Tache tache;

    @BeforeEach
    void setUp() {
        tache = new Tache(1, "Tâche de test", "Description de test", LocalDate.of(2025, 1, 15), false);
    }

    @Test
    void testInitialValues() {
        assertEquals(1, tache.getId());
        assertEquals("Tâche de test", tache.getTitre());
        assertEquals("Description de test", tache.getDescription());
        assertEquals(LocalDate.of(2025, 1, 15), tache.getDateTermine());
        assertFalse(tache.isEstTermine());
        assertFalse(tache.getCheckBox());
    }

    @Test
    void testSetTitre() {
        tache.setTitre("Nouveau titre");
        assertEquals("Nouveau titre", tache.getTitre());
    }

    @Test
    void testSetDescription() {
        tache.setDescription("Nouvelle description");
        assertEquals("Nouvelle description", tache.getDescription());
    }

    @Test
    void testSetDateTermine() {
        LocalDate newDate = LocalDate.of(2025, 2, 1);
        tache.setDateTermine(newDate);
        assertEquals(newDate, tache.getDateTermine());
    }

    @Test
    void testSetEstTermine() {
        tache.setEstTermine(true);
        assertTrue(tache.isEstTermine());
        assertTrue(tache.getCheckBox());
    }

    @Test
    void testSetCheckBox() {
        tache.setCheckBox(true);
        assertTrue(tache.getCheckBox());
        assertTrue(tache.isEstTermine());

        tache.setCheckBox(false);
        assertFalse(tache.getCheckBox());
        assertFalse(tache.isEstTermine());
    }

    @Test
    void testTitreProperty() {
        tache.titreProperty().set("Titre modifié");
        assertEquals("Titre modifié", tache.getTitre());
    }

    @Test
    void testDescriptionProperty() {
        tache.descriptionProperty().set("Description modifiée");
        assertEquals("Description modifiée", tache.getDescription());
    }

    @Test
    void testDateTermineProperty() {
        LocalDate newDate = LocalDate.of(2025, 3, 1);
        tache.dateTermineProperty().set(newDate);
        assertEquals(newDate, tache.getDateTermine());
    }

    @Test
    void testEstTermineProperty() {
        tache.estTermineProperty().set(true);
        assertTrue(tache.isEstTermine());
    }

    @Test
    void testCheckBoxProperty() {
        tache.checkBoxProperty().set(true);
        assertTrue(tache.getCheckBox());
    }

    @Test
    void testImmutableId() {
        tache.setId(2);
        assertEquals(1, tache.getId()); // L'ID reste inchangé
    }
}
