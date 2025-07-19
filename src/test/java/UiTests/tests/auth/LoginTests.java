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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

@Epic("Аутентификация")
@Feature("Логин")
@Owner("SergeyQA")
public class LoginTests extends UiBaseTest {

    static final LoginPage loginPage = new LoginPage();
    private static final String DUMMY_PASSWORD = TestConfig.validPassword();
    private final RegisterPage registerPage = new RegisterPage();

    @Test
    @Story("Позитивный логин: обычный username")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Успешный логин с обычным username")
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
    @Story("Позитивный логин: 3 символа")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Успешный логин с username из 3 символов")
    void loginWithThreeCharUsername() {
        String username = new Faker().regexify("[A-Za-z]{3}");
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
    @Story("Позитивный логин: максимальная длина")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Успешный логин с username из 39 символов")
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
    @Story("Выход из системы")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Успешный логаут после входа")
    void checkLogout() {
        String username = new Faker().regexify("[A-Za-z]{3}");
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
    @MethodSource("NegativeLoginCasesProvider")
    @Story("Негативный логин")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Негативные кейсы логина")
    void negativeLogin(String description, String username, String password, String expectedMessage) {
        loginPage.open();
        loginPage.fillLoginPage(username, password);
        loginPage.sendLoginForm();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(loginPage.getAlertMessage())
                .as("Сообщение об ошибке для кейса «%s»", description)
                .containsIgnoringCase(expectedMessage);
        softly.assertAll();
    }

    private static Stream<Arguments> NegativeLoginCasesProvider() {
        Faker faker = new Faker();
        final String validLogUsername = "practice";
        final String validLogPass = "SuperSecretPassword!";
        return Stream.of(
                Arguments.of("Пустые поля", "", "", "Your username is invalid!"),
                Arguments.of("Пустой Username", "", validLogPass, "Your username is invalid!"),
                Arguments.of("Пустой Password", validLogUsername, "", "Your password is invalid!"),
                Arguments.of("Несуществующий юзер", "ghost", "Super!", "Your username is invalid!"),
                Arguments.of("SQL-инъекция в username", "admin'--", validLogPass, "Your username is invalid!"),
                Arguments.of("SQL-инъекция в пароль", validLogUsername, "' OR 1=1--", "Your password is invalid!"),
                Arguments.of("XSS в username", "<script>alert(1)</script>", validLogPass, "Your username is invalid!"),
                Arguments.of("Username с пробелом", " practice", validLogPass, "Your username is invalid!"),
                Arguments.of("Пароль с пробелом", validLogUsername, " SuperSecretPassword!", "Your password is invalid!"),
                Arguments.of("Username > 39 символов", faker.regexify("[A-Za-z]{40}"), validLogPass, "Your username is invalid!")
        );
    }
}
