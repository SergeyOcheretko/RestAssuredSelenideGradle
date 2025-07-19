package apiTests.UserAccountApiTests;

import apiTests.models.TestUser;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;


import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MethodsUserAccountApi {


    protected static Faker faker = new Faker();

    protected static final String BASE_URL = "https://practice.expandtesting.com/notes";
    protected static TestUser user = new TestUser();


    @BeforeEach
    void initApi() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        RestAssured.requestSpecification = new RequestSpecBuilder()

                .setBaseUri(BASE_URL)
                .addHeader("Content-Type", "application/json")
                .log(LogDetail.URI)
                .build();
        //  Генерация пользователя
        user.name = faker.name().username();
        user.email = faker.internet().emailAddress();
        user.password = faker.internet().password(8, 16);
        user.phone = faker.phoneNumber().cellPhone();
        user.company = faker.lorem().characters(5, 15);


        registerUser(user);
        loginAndExtractUserData(user);


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


    @Step("Регистрируем пользователя: {user.email}")
    protected static void registerUser(TestUser user) {
        String body = String.format("""
                {
                  "name": "%s",
                  "email": "%s",
                  "password": "%s"
                }
                """, user.name, user.email, user.password);

        given()
                .contentType(ContentType.JSON)
                .accept("application/json")
                .body(body)
                .when()
                .post("/api/users/register")
                .then()
                .log().all()
                .statusCode(anyOf(is(200), is(201)));
    }

    @Step("Логинимся и сохраняем токен")
    protected static void loginAndExtractUserData(TestUser user) {
        String body = String.format("""
                {
                  "email": "%s",
                  "password": "%s"
                }
                """, user.email, user.password);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept("application/json")
                .body(body)
                .when()
                .post("/api/users/login")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        user.token = response.jsonPath().getString("data.token");
        user.name = response.jsonPath().getString("data.name");
        user.email = response.jsonPath().getString("data.email");

        assertNotNull(user.token, "❌ Токен не получен");
    }


    protected static void updateUser(TestUser user, String name, String phone, String company) {
        given()
                .header("X-Auth-Token", user.token)
                .accept(ContentType.JSON)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("name", name)
                .formParam("phone", phone)
                .formParam("company", company)
                .log().all()
                .when()
                .patch("/api/users/profile")
                .then()
                .log().all()
                .statusCode(200); // должен сработать!

        user.name = name;
        user.phone = phone;
        user.company = company;

    }

    @Step("Проверяем, что профиль соответствует локальному объекту")
    protected static void verifyUserProfile(TestUser user) {
        String newName = faker.name().firstName() + " " + faker.name().lastName();
        String phone = faker.number().digits(10);
        String company = faker.company().name();
        updateUser(user, newName, phone, company);
        given()
                .header("X-Auth-Token", user.token)
                .accept(ContentType.JSON)
                .when()
                .get("/api/users/profile")
                .then()
                .statusCode(200)
                .body("data.name", equalTo(user.name))
                .body("data.phone", equalTo(user.phone))
                .body("data.company", equalTo(user.company));
    }

    @Step("Подтверждаем юзер профайл")
    protected static void verifyBasicProfile(TestUser user) {
        given()
                .header("X-Auth-Token", user.token)
                .accept(ContentType.JSON)
                .when()
                .get("/api/users/profile")
                .then()
                .statusCode(200)
                .body("data.name", equalTo(user.name))
                .body("data.email", equalTo(user.email));
    }

    @Step("Отправляем Reset Password на валидный емейл")
    protected static void verifyResetPassword(TestUser user) {
        String email = "sergej.ocheretko1@gmail.com";

        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .accept(ContentType.JSON)
                .formParam("email", email)
                .log().all()
                .when()
                .post("/api/users/forgot-password")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", containsString(email));
    }

    @Step("Сбрасываем пароль через API с токеном")
    protected static void resetPassword(String newPassword, String resetToken) {
        String body = String.format("""
                {
                  "password": "%s",
                  "token": "%s"
                }
                """, newPassword, resetToken);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .log().all()
                .when()
                .post("/api/users/reset-password")
                .then()
                .log().all()
                .statusCode(anyOf(is(200), is(401), is(400)));// добавляем 400
    }

    @Step("Обновляем пароль пользователя через API")
    protected static void changePassword(TestUser user) {
        String currentPassword = user.password;
        String newPassword = faker.internet().password(8, 16, true, true, true);

        String body = String.format("""
                {
                  "currentPassword": "%s",
                  "newPassword": "%s"
                }
                """, currentPassword, newPassword);

        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .log().all()
                .when()
                .post("/api/users/change-password")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", containsString("successfully"));

        user.password = newPassword; // Обновляем локально
        System.out.printf("🔐 Пароль успешно обновлён: %s%n", newPassword);
    }

    @Step("Выходим из системы (logout)")
    protected static void logout(TestUser user) {
        given()
                .header("X-Auth-Token", user.token)
                .accept(ContentType.JSON)
                .log().all()
                .when()
                .delete("/api/users/logout")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("User has been successfully logged out"));
    }

    @Step("Удаляем аккаунт")
    protected static void deleteAccount(TestUser user) {
        given()
                .header("X-Auth-Token", user.token)
                .accept(ContentType.JSON)
                .log().all()
                .when()
                .delete("/api/users/delete-account")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Account successfully deleted"));

    }


    @Step("Регистрация с пустым эмейлом")
    protected static void incorrectEmailRegistration() {
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", user.name,
                        "email", "",
                        "password", user.password
                ))
                .log().all()
                .when()
                .post("/api/users/register")
                .then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", containsString("valid email"));
    }


    @Step("Регистрация с некорректным email")
    protected static void emptyEmailRegistration() {
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", user.name,
                        "email", "rjgmrtgm@irkmre.com,",
                        "password", user.password))
                .log().all()
                .when()
                .post("/api/users/register")
                .then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", containsString("email"));

    }

    @Step("Регистрация с коротким паролем")
    protected static void shortPasswordRegistration() {
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", user.name,
                        "email", user.email,
                        "password", "fre"))
                .log().all()
                .when()
                .post("/api/users/register")
                .then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", containsString("Password"));

    }

    @Step("Регистрация с уже существующим email")
    protected static void duplicateEmail() {
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", user.name,
                        "email", "sergej.ocheretko1@gmail.com",
                        "password", user.password))
                .log().all()
                .when()
                .post("/api/users/register")
                .then()
                .log().all()
                .statusCode(anyOf(is(409), (is(400))))
                .body("success", equalTo(false))
                .body("message", containsString("same email"));

    }

    @Step("Регистрация без тела запроса")
    protected static void withoutBodyRegistration() {
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", "",
                        "email", "",
                        "password", ""))
                .log().all()
                .when()
                .post("/api/users/register")
                .then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false));
    }

    @Step("Логин с неверным паролем")
    protected static void loginWithIncorrectPassword(TestUser user) {
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", user.name,
                        "email", user.email,
                        "password", "1"))
                .log().all()
                .when()
                .post("/api/users/login")
                .then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false));
    }

    @Step("Логин с незарегистрированным email")
    protected static void loginWithUnregisteredEmailMethod() {
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", user.name,
                        "email", "s.ocheretko@gmail.com",
                        "password", user.password))
                .log().all()
                .when()
                .post("/api/users/login")
                .then()
                .log().all()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", containsString("Incorrect email"));
    }

    @Step("Логин без тела запроса")
    protected static void loginWithoudBodyMethod() {
        given()
                .header("X-Auth-Token", user.token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", "",
                        "email", "",
                        "password", ""))
                .log().all()
                .when()
                .post("/api/users/login")
                .then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false));

    }

    @Step("Логин с некорректным Content-Type")
    protected static void loginWithIncorrectContentType() {
        String rawBody = """
                {
                    "name": "",
                    "email": "",
                    "password": ""
                }
                """;
        given()
                .header("X-Auth-Token", user.token)
                .contentType("text/plain")
                .log().all()
                .when()
                .post("/api/users/login")
                .then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false));

    }


}










