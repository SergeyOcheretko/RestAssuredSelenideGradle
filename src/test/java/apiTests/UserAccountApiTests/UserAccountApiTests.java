package apiTests.UserAccountApiTests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.*;

@Epic("Authentication")
@Feature("User Account API")
@Owner("SergeyQA")
@Tag("api")
public class UserAccountApiTests extends MethodsUserAccountApi {

    @Test
    @Tag("smoke")
    @DisplayName("Check API server health")
    void checkApiServerHealth() {
        checkApiHealth();
    }

    @Test
    @Tag("smoke")
    @DisplayName("Login and save token")
    void checkLoginAndSaveToken() {
        loginAndExtractUserData(user);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Profile returns correct data for authorized user")
    void getProfile_shouldReturnCorrectData() {
        verifyBasicProfile(user);
    }

    @Test
    @DisplayName("Profile update succeeds")
    void updateProfile_shouldSucceed() {
        verifyUserProfile(user);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Reset password using valid email")
    void resetPasswordViaValidEmail() {
        verifyResetPassword(user);
    }

    @Test
    @DisplayName("Password reset works with mock token (simulation)")
    void resetPassword_withMockToken_shouldReturnResponse() {
        verifyResetPassword(user);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Change password")
    void changePassword() {
        changePassword(user);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Logout from system")
    void logoutSystem() {
        logout(user);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Delete test account")
    void deleteTestAccount() {
        deleteAccount(user);
    }

    @Test
    @Tag("negative")
    @DisplayName("Register with empty email")
    void registerWithEmptyEmail() {
        incorrectEmailRegistration();
    }

    @Test
    @Tag("negative")
    @DisplayName("Register with invalid email")
    void registerWithInvalidEmail() {
        emptyEmailRegistration();
    }

    @Test
    @Tag("negative")
    @DisplayName("Register with short password")
    void registerWithShortPassword() {
        shortPasswordRegistration();
    }

    @Test
    @Tag("negative")
    @DisplayName("Register with duplicate email")
    void registerWithDuplicateEmail() {
        duplicateEmail();
    }

    @Test
    @Tag("negative")
    @DisplayName("Register with empty request body")
    void registerWithEmptyBody() {
        withoutBodyRegistration();
    }

    @Test
    @Tag("negative")
    @DisplayName("Login with incorrect password")
    void loginWithWrongPassword() {
        loginWithIncorrectPassword(user);
    }

    @Test
    @Tag("negative")
    @DisplayName("Login with unregistered email")
    void loginWithUnregisteredEmail() {
        loginWithUnregisteredEmailMethod();
    }

    @Test
    @Tag("negative")
    @DisplayName("Login with empty request body")
    void loginWithEmptyBody() {
        loginWithoudBodyMethod();
    }

    @Test
    @Tag("negative")
    @DisplayName("Login with incorrect Content-Type")
    void loginWithInvalidContentType() {
        loginWithIncorrectContentType();
    }
}
