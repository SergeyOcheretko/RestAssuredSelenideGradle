package apiTests.NotesApiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class NotesApiTests extends MethodsNotesApi {

    @Test
    @DisplayName("Проверка работоспособности сервера API")
    @Order(1)
    @Tag("api")
    @Tag("smoke")
    @Tag("regression")

    void checkApiServerHealth() {
        checkApiHealth();
    }

    @Test
    @DisplayName("Создание заметки")
    @Tag("api")
    @Tag("smoke")
    @Tag("regression")

    void createNotes() {
        createNote(note);
    }

    @Test
    @DisplayName("Взять все заметки")
    @Tag("api")
    @Tag("regression")
    void retrieveListOfNotes() {
        getAllNotes(note);
    }

    @Test
    @DisplayName("Создание и получение заметки по ID")
    @Tag("api")
    @Tag("smoke")
    @Tag("regression")
    void createAndGetNoteById() {


        createNote(note);
        getNoteByIdAndVerify(note);
    }

    @Test
    @DisplayName("Обновление заметки по ID")
    @Tag("api")
    @Tag("regression")
    void updateNote() {


        createNote(note);
        updateNoteById(note);
    }

    @Test
    @DisplayName("Обновление статуса Completed")
    @Tag("api")
    @Tag("regression")
    void completedUpdate() {
        createNote(note);

        updateCompletedStatus(note);
    }


    @Test
    @DisplayName("Удаление заметки по Id")

    @Tag("api")
    @Tag("smoke")
    void deleteNote() {

        createNote(note);
        deleteNoteById(note);
    }


    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Создание заметки без токена")
    void createNoteWithoutToken() {
        createNoteWithoutTokenMethod();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Создание заметки с пустым title")
    void createNoteWithEmptyTitle() {
        createNoteWithEmptyTitleMethod();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Создание заметки с title < 4 символов")
    void createNoteWithShortTitle() {
        createNoteWithShortTitleMethod();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Создание заметки с title > 100 символов")
    void createNoteWithLongTitle() {
        createNoteWithLongTitleMethod();
    }


    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Создание заметки с неизвестной категорией")
    void createNoteWithInvalidCategory() {
        createNoteWithUnknownCategoryMethod();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Обновление несуществующей заметки")
    void updateNonexistentNote() {
        updateUnrealNote();

    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Удаление чужой заметки")
    void deleteNoteOfAnotherUser() {
        deleteInvalidNotes();
    }


    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Обновление заметки без тела запроса")
    void updateNoteWithEmptyBody() {
        createNote(note);
        updateNoteWithoutBody(note);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Создание заметки с некорректным Content-Type")
    void createNoteWithInvalidContentType() {
        createNoteWithInvalidContentTypeMethod();
    }





    }

