package apiTests.UserAccountApiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class UserAccountApiTests extends MethodsUserAccountApi {

    @Test
    @Tag("api")
    @DisplayName("Проверка работоспособности сервера API")
    @Order(1)
    @Tag("smoke")
    void checkApiServerHealth() {
        checkApiHealth();
    }


    @Test
    @Tag("api")
    @DisplayName("Логин и сохранение токена")
    @Order(2)
    @Tag("smoke")
    void checkLoginAndSaveToken() {
        loginAndExtractUserData(user);
    }


    @Test
    @Tag("api")
    @DisplayName("Профиль возвращается корректно для авторизованного пользователя")
    @Order(3)
    @Tag("smoke")
    void getProfile_shouldReturnCorrectData() {
        verifyBasicProfile(user);
    }


    @Test
    @Tag("api")
    @DisplayName("Профиль успешно обновляется")
    @Order(4)
    void updateProfile_shouldSucceed() {
        verifyUserProfile(user);
    }

    @Test
    @Tag("api")
    @DisplayName("Сбрасываем пароль")
    @Order(5)
    @Tag("smoke")
    void resetPasswordViaValidEmail() {
        verifyResetPassword(user);
    }


    @Test
    @Tag("api")
    @DisplayName("Сброс пароля работает при фиктивном токене (эмуляция)")
    @Order(6)
    void resetPassword_withMockToken_shouldReturnResponse() {
        verifyResetPassword(user);
    }


    @Test
    @Tag("api")
    @DisplayName("Меняем пароль")
    @Order(7)
    @Tag("smoke")
    void changePassword() {
        changePassword(user);
    }

    @Test
    @Tag("api")
    @DisplayName("Выходим из системы")
    @Order(8)
    @Tag("smoke")
    void logoutSystem() {
        logout(user);
    }

    @Test
    @Tag("api")
    @DisplayName("Удаление аккаунта")
    @Order(9)
    @Tag("smoke")
    void deleteTestAccount() {
        deleteAccount(user);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Регистрация с пустым email")
    void registerWithEmptyEmail() {
        incorrectEmailRegistration();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Регистрация с некорректным email")
    void registerWithInvalidEmail() {
        emptyEmailRegistration();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Регистрация с коротким паролем")
    void registerWithShortPassword() {
        shortPasswordRegistration();

    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Регистрация с уже существующим email")
    void registerWithDuplicateEmail() {
        duplicateEmail();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Регистрация без тела запроса")
    void registerWithEmptyBody() {
        withoutBodyRegistration();
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Логин с неверным паролем")
    void loginWithWrongPassword() {
        loginWithIncorrectPassword(user);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Логин с незарегистрированным email")
    void loginWithUnregisteredEmail() {
        loginWithUnregisteredEmailMethod();

    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Логин без тела запроса")
    void loginWithEmptyBody() {
        loginWithoudBodyMethod();

    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Логин с некорректным Content-Type")
    void loginWithInvalidContentType() {
        loginWithIncorrectContentType();

    }


}
