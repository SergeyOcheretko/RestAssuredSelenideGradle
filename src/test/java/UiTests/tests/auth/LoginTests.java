package UiTests.tests.auth;

import UiTests.base.UiBaseTest;
import UiTests.pages.auth.LoginPage;
import UiTests.pages.auth.RegisterPage;
import UiTests.utils.FlashMessage;
import UiTests.utils.TestConfig;
import com.github.javafaker.Faker;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Authentication")
@Feature("Login")
@Owner("SergeyQA")
@Tag("ui")
public class LoginTests extends UiBaseTest {

    static final LoginPage loginPage = new LoginPage();
    private static final String DUMMY_PASSWORD = TestConfig.validPassword();
    private final RegisterPage registerPage = new RegisterPage();

    @Test
    @Story("Positive login: standard username")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Successful login with standard username")
    void loginWithRegularUsername() {
        String username = new Faker().regexify("[A-Za-z]{5}");
        String password = DUMMY_PASSWORD;

        registerPage.open();
        registerPage.fillRegistrationForm(username, password, password);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_REGISTER.text());

        loginPage.open();
        loginPage.fillLoginPage(username, password);
        loginPage.sendLoginForm();
        assertThat(loginPage.getAlertMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_LOGIN.text());

        loginPage.logOut();
    }

    @Test
    @Story("Positive login: 3 characters")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Successful login with 3-character username")
    void loginWithThreeCharUsername() {
        String username = new Faker().regexify("[A-Za-z]{3}");
        String password = new Faker().regexify("[A-Za-z]{5}");

        registerPage.open();
        registerPage.fillRegistrationForm(username, password, password);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_REGISTER.text());

        loginPage.open();
        loginPage.fillLoginPage(username, password);
        loginPage.sendLoginForm();
        assertThat(loginPage.getAlertMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_LOGIN.text());

        loginPage.logOut();
    }

    @Test
    @Story("Positive login: max length")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Successful login with 39-character username")
    void loginWithMaxLengthUsername() {
        String username = new Faker().regexify("[A-Za-z]{39}");
        String password = DUMMY_PASSWORD;

        registerPage.open();
        registerPage.fillRegistrationForm(username, password, password);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_REGISTER.text());

        loginPage.open();
        loginPage.fillLoginPage(username, password);
        loginPage.sendLoginForm();
        assertThat(loginPage.getAlertMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_LOGIN.text());

        loginPage.logOut();
    }

    @Test
    @Story("Logout")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Successful logout after login")
    void checkLogout() {
        String username = new Faker().regexify("[A-Za-z]{5}");
        String password = DUMMY_PASSWORD;

        registerPage.open();
        registerPage.fillRegistrationForm(username, password, password);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_REGISTER.text());

        loginPage.open();
        loginPage.fillLoginPage(username, password);
        loginPage.sendLoginForm();
        loginPage.logOut();
        assertThat(loginPage.getAlertMessage())
                .isEqualTo(FlashMessage.SUCCESS_LOGOUT.text());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("negativeLoginCasesProvider")
    @Story("Negative login")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Negative login scenarios")
    void negativeLogin(String description, String username, String password, String expectedMessage) {
        loginPage.open();
        loginPage.fillLoginPage(username, password);
        loginPage.sendLoginForm();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(loginPage.getAlertMessage())
                .as("Error message for case: %s", description)
                .containsIgnoringCase(expectedMessage);
        softly.assertAll();
    }

    private static Stream<Arguments> negativeLoginCasesProvider() {
        Faker faker = new Faker();
        final String validUsername = "practice";
        final String validPassword = "SuperSecretPassword!";
        return Stream.of(
                Arguments.of("Empty fields", "", "", "Your username is invalid!"),
                Arguments.of("Empty username", "", validPassword, "Your username is invalid!"),
                Arguments.of("Empty password", validUsername, "", "Your password is invalid!"),
                Arguments.of("Non-existent user", "ghost", "Super!", "Your username is invalid!"),
                Arguments.of("SQL injection in username", "admin'--", validPassword, "Your username is invalid!"),
                Arguments.of("SQL injection in password", validUsername, "' OR 1=1--", "Your password is invalid!"),
                Arguments.of("XSS attempt in username", "<script>alert(1)</script>", validPassword, "Your username is invalid!"),
                Arguments.of("Username with leading space", " practice", validPassword, "Your username is invalid!"),
                Arguments.of("Password with leading space", validUsername, " SuperSecretPassword!", "Your password is invalid!"),
                Arguments.of("Username longer than 39 characters", faker.regexify("[A-Za-z]{40}"), validPassword, "Your username is invalid!")
        );
    }
}
