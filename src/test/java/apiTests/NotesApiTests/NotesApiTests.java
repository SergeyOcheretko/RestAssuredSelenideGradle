package apiTests.NotesApiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class NotesApiTests extends MethodsNotesApi {

    @Test
    @DisplayName("Check API server health")
    @Order(1)
    @Tag("api")
    @Tag("smoke")
    @Tag("regression")
    void checkApiServerHealth() {
        checkApiHealth();
    }

    @Test
    @DisplayName("Create note")
    @Tag("api")
    @Tag("smoke")
    @Tag("regression")
    void createNotes() {
        createNote(note);
    }

    @Test
    @DisplayName("Retrieve all notes")
    @Tag("api")
    @Tag("regression")
    void retrieveListOfNotes() {
        getAllNotes(note);
    }

    @Test
    @DisplayName("Create and get note by ID")
    @Tag("api")
    @Tag("smoke")
    @Tag("regression")
    void createAndGetNoteById() {
        createNote(note);
        getNoteByIdAndVerify(note);
    }

    @Test
    @DisplayName("Update note by ID")
    @Tag("api")
    @Tag("regression")
    void updateNote() {
        createNote(note);
        updateNoteById(note);
    }

    @Test
    @DisplayName("Update note 'Completed' status")
    @Tag("api")
    @Tag("regression")
    void completedUpdate() {
        createNote(note);
        updateCompletedStatus(note);
    }

    @Test
    @DisplayName("Delete note by ID")
    @Tag("api")
    @Tag("smoke")
    void deleteNote() {
        createNote(note);
        deleteNoteById(note);
    }

    // 🧪 Negative scenarios

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Create note without token")
    void createNoteWithoutToken() {
        createNoteWithoutTokenMethod();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Create note with empty title")
    void createNoteWithEmptyTitle() {
        createNoteWithEmptyTitleMethod();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Create note with title shorter than 4 characters")
    void createNoteWithShortTitle() {
        createNoteWithShortTitleMethod();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Create note with title longer than 100 characters")
    void createNoteWithLongTitle() {
        createNoteWithLongTitleMethod();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Create note with unknown category")
    void createNoteWithInvalidCategory() {
        createNoteWithUnknownCategoryMethod();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Update nonexistent note")
    void updateNonexistentNote() {
        updateUnrealNote();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Delete note owned by another user")
    void deleteNoteOfAnotherUser() {
        deleteInvalidNotes();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Update note with empty request body")
    void updateNoteWithEmptyBody() {
        createNote(note);
        updateNoteWithoutBody(note);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Create note with incorrect Content-Type")
    void createNoteWithInvalidContentType() {
        createNoteWithInvalidContentTypeMethod();
    }
}
