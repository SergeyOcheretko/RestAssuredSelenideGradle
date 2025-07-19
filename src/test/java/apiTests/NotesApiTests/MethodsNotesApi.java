package apiTests.NotesApiTests;


import apiTests.UserAccountApiTests.MethodsUserAccountApi;
import apiTests.models.TestNote;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MethodsNotesApi extends MethodsUserAccountApi {

    protected static Faker faker = new Faker();

    protected static final String BASE_URL = "https://practice.expandtesting.com/notes";
    protected TestNote note;

    @BeforeEach
    void initApi() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .addHeader("Content-Type", "application/json")
                .log(LogDetail.URI)
                .build();


        // Генерация заметки
        note = new TestNote();
        note.title = faker.lorem().characters(10, 50);
        note.description = faker.lorem().sentence();
        note.category = faker.options().option("Home", "Work", "Personal");
        user.email = faker.internet().emailAddress();
        user.password = faker.internet().password(8, 16);

        registerUser(user);
        loginAndExtractUserData(user);

    }
    @AfterEach
    void cleanupUser() {
        if (user.token != null) {
            given().header("x-auth-token", user.token)
                    .delete("/api/notes/me");
        }
    }
    @Step("Проверка работоспособности АПИ")
    protected static void checkApiHealth() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/health-check")
                .then()
                .log().all()
                .statusCode(200)
                .body("message", equalTo("Notes API is Running"));
    }

    @Step("Создание заметки и сохранение айди")
    protected void createNote(TestNote note) {
        note.id = given()
                .header("X-Auth-Token", user.token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .accept(ContentType.JSON)
                .formParam("title", note.title)
                .formParam("description", note.description)
                .formParam("category", note.category)
                .log().all()
                .when()
                .post("/api/notes")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .path("data.id"); // 👈 извлекаем ID как строку
    }

    @Step("Получение заметки по ID и верификация содержимого")
    protected void getNoteByIdAndVerify(TestNote note) {
        given()
                .header("X-Auth-Token", user.token)
                .accept(ContentType.JSON)
                .pathParam("id", note.id)
                .log().all()
                .when()
                .get("/api/notes/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.id", equalTo(note.id))
                .body("data.title", equalTo(note.title))
                .body("data.description", equalTo(note.description))
                .body("data.category", equalTo(note.category));
    }

    @Step("Получить список заметок для аутентифицированного пользователя")
    protected void getAllNotes(TestNote note) {
        given()
                .header("X-Auth-Token", user.token)
                .when()
                .get("/api/notes")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Step("Обновление заметки по ID")
    protected void updateNoteById(TestNote note) {


        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .pathParam("id", note.id)

                .body(Map.of(
                        "title", note.title,
                        "description", note.description,
                        "completed", note.completed,
                        "category", note.category
                ))
                .log().all()
                .when()
                .put("/api/notes/{id}")
                .then()
                .log().all()
                .body("success", equalTo(true))
                .body("data.id", equalTo(note.id));


    }

    @Step("Обновление статуса сompleted")
    protected void updateCompletedStatus(TestNote note) {
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .pathParam("id", note.id)
                .body(Map.of("completed", note.completed))
                .log().all()
                .when()
                .patch("/api/notes/{id}")
                .then()
                .log().all()
                .body("success", equalTo(true))
                .body("data.id", equalTo(note.id));

    }

    @Step("Удаление заметки по Id")
    protected void deleteNoteById(TestNote note) {
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .pathParam("id", note.id)
                .log().all()
                .when()
                .delete("/api/notes/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true));

    }

    @Step("Создание заметки без токена")
    protected void createNoteWithoutTokenMethod() {

        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .post("/api/notes")
                .then()
                .log().all()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("No authentication token specified in x-auth-token header"));

    }

    @Step("Создание заметки с пустым title")
    protected void createNoteWithEmptyTitleMethod() {
        Map<String, Object> body = Map.of(
                "title", "",
                "description", "Описание для проверки",
                "category", "Home"
        );
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body(body)
                .log().all()
                .when()
                .post("/api/notes")
                .then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false));

    }

    @Step("Создание заметки с title < 4 символов")
    protected void createNoteWithShortTitleMethod() {
        Map<String, Object> body = Map.of(
                "title", "RTY",
                "description", "Описание для проверки",
                "category", "Home"
        );

        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body(body) //
                .log().all()
                .when()
                .post("/api/notes")
                .then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Title must be between 4 and 100 characters"));

    }

    @Step("Создание заметки с title > 100 символов")
    protected void createNoteWithLongTitleMethod() {
        {
            String longTitle = faker.lorem().characters(101, 120);
            Map<String, Object> body = Map.of(
                    "title", longTitle,
                    "description", "Описание для проверки",
                    "category", "Home"
            );

            given()
                    .header("X-Auth-Token", user.token)
                    .contentType(ContentType.JSON)
                    .body(body) //
                    .log().all()
                    .when()
                    .post("/api/notes")
                    .then()
                    .log().all()
                    .statusCode(400)
                    .body("success", equalTo(false))
                    .body("message", equalTo("Title must be between 4 and 100 characters"));


        }
    }

    @Step("Создание заметки с неизвестной категорией")
    protected void createNoteWithUnknownCategoryMethod() {
        Map<String, Object> body = Map.of(
                "title", note.title,
                "description", note.description,
                "category", "QWERTY"
        );
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body(body) //
                .log().all()
                .when()
                .post("/api/notes")
                .then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Category must be one of the categories: Home, Work, Personal"));


    }

    @Step("Обновление несуществующей заметки")
    protected void updateUnrealNote() {
        String invalidId = faker.lorem().characters(10);

        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .pathParam("id", invalidId)
                .body(Map.of(
                        "title", note.title,
                        "description", note.description,
                        "completed", note.completed,
                        "category", note.category
                ))
                .log().all()
                .when()
                .put("/api/notes/{id}")
                .then()
                .log().all()
                .body("success", equalTo(false))
                .body("message", equalTo("Note ID must be a valid ID"));

    }

    @Step("Удаление чужой заметки")

    protected void deleteInvalidNotes() {
        String foreignNoteId = faker.lorem().characters(24);

        given()
                .header("X-Auth-Token", user.token)
                .when()
                .delete("/api/notes/" + foreignNoteId)
                .then()
                .log().all()
                .body("success", equalTo(false))
                .body("message", equalTo("Note ID must be a valid ID"));

    }

    @Step("Обновление заметки без тела запроса")
    protected void updateNoteWithoutBody(TestNote note) {
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body("")
                .log().all()
                .when()
                .put("/api/notes/{id}", note.id)
                .then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false));
    }

    @Step("Создание заметки с некорректным Content-Type")
    protected void createNoteWithInvalidContentTypeMethod() {
        given()
                .header("X-Auth-Token", user.token)
                .contentType("text/plain")
                .formParam("title", note.title)
                .formParam("description", note.description)
                .formParam("category", note.category)
                .formParam("completed", note.completed)
                .log().all()
                .when()
                .put("/api/notes/")
                .then()
                .log().all()
                .statusCode(404);

    }


}
