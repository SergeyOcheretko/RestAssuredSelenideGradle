package UiTests.tests.auth;

import UiTests.base.UiBaseTest;
import UiTests.pages.auth.RegisterPage;
import UiTests.utils.FlashMessage;
import UiTests.utils.TestConfig;
import com.github.javafaker.Faker;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

@Epic("Аутентификация")
@Feature("Регистрация")
@Owner("SergeyQA")
@Tag("ui")
public class RegisterTests extends UiBaseTest {

    private static final String DUMMY_PASSWORD = TestConfig.validPassword();
    private final RegisterPage registerPage = new RegisterPage();

    private String generateUniqueUsername() {
        return "user" + registerPage.createRandomUsername();
    }


    @Test
    @Story("Обычный username")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Регистрация с обычным username")
    void registerWithRegularUsername() {
        String username = "user" + new Faker().regexify("[A-Za-z]{5}");
        registerPage.open();
        registerPage.fillRegistrationForm(username, DUMMY_PASSWORD, DUMMY_PASSWORD);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_REGISTER.text());
    }

    @Test
    @Story("Граничный username — минимум")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Регистрация с username из 3 символов")
    void registerWithThreeCharUsername() {
        Faker faker = new Faker();
     String  username = faker.regexify("[A-Za-z]{3}");
        registerPage.open();
        registerPage.fillRegistrationForm(username, DUMMY_PASSWORD, DUMMY_PASSWORD);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_REGISTER.text());
    }

    @Test
    @Story("Граничный username — максимум")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Регистрация с username из 39 символов")
    void registerWithMaxLengthUsername() {
        String username = new Faker().regexify("[A-Za-z]{39}");
        registerPage.open();
        registerPage.fillRegistrationForm(username, DUMMY_PASSWORD, DUMMY_PASSWORD);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_REGISTER.text());
    }

    @Test
    @Story("Регистрация с повторным username")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Регистрация с уже существующим username")
    void shouldRejectDuplicateUsername() {
        String uniqueUser = generateUniqueUsername();

        registerPage.open();
        registerPage.fillRegistrationForm(uniqueUser, DUMMY_PASSWORD, DUMMY_PASSWORD);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_REGISTER.text());

        registerPage.open();
        registerPage.fillRegistrationForm(uniqueUser, DUMMY_PASSWORD, DUMMY_PASSWORD);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.DUPLICATE_USERNAME.text());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("negativeCasesProvider")
    @Story("Негативные кейсы")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Проверка ошибок при регистрации")
    void negativeRegistration(String description,
                              String username,
                              String password,
                              String confirmPassword,
                              String expectedMessage) {

        registerPage.open();
        registerPage.fillRegistrationForm(username, password, confirmPassword);
        registerPage.submit();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(registerPage.getFlashMessage())
                .as("Кейс: %s", description)
                .containsIgnoringCase(expectedMessage);
        softly.assertAll();
    }

    private static Stream<Arguments> negativeCasesProvider() {
        Faker faker = new Faker();
        final String validPass = "Qwerty123!";
        return Stream.of(
                Arguments.of("Пустой username", "", validPass, validPass, "All fields are required"),
                Arguments.of("Пустой пароль", "john", "", "", "All fields are required"),
                Arguments.of("Пароли не совпадают", "john", "fort", "Qwerty123!", "Passwords do not match."),
                Arguments.of("Username < 3 символов", "ab", validPass, validPass, "Username must be at least 3 characters"),
                Arguments.of("Username > 39 символов", faker.regexify("[A-Za-z]{40}"), validPass, validPass, "must be between 3 and 39 characters"),
                Arguments.of("Username с пробелом", "john doe", validPass, validPass, "Invalid username"),
                Arguments.of("Username со спецсимволами", "john@#$", validPass, validPass, "Invalid username"),
                Arguments.of("Пароль короче 4 символов", "john", "123", "123", "Password must be at least 4 characters"),
                Arguments.of("SQL-инъекция в username", "' OR 1=1--", validPass, validPass, "Invalid username"),
                Arguments.of("XSS в username", "<script>alert(1)</script>", validPass, validPass, "Invalid username"),
                Arguments.of("Пароль 40 символов", "john", "Aa1!".repeat(40), "Aa1!".repeat(40), "An error occurred during registration. Please try again."),
                Arguments.of("Username начинается с пробела", " jonh", validPass, validPass, "cannot start or end with a hyphen."),
                Arguments.of("Username заканчивается с пробелом", "jonh ", validPass, validPass, "cannot start or end with a hyphen."),
                Arguments.of("Username только заглавными буквами", "JOHN ", validPass, validPass, "cannot start or end with a hyphen."),
                Arguments.of("Пароль без цифры", "john", "NoDigits!", "NoDigits!", "An error occurred during registration. Please try again."),
                Arguments.of("Username только цифры", "12345", validPass, validPass, "An error occurred during registration. Please try again."),
                Arguments.of("Email вместо Username", "WEDIID@gmail.com", validPass, validPass, "Invalid username")
        );
    }

}
