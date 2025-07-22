package UiTests.tests.auth;

import UiTests.base.UiBaseTest;
import UiTests.pages.auth.RegisterPage;
import UiTests.utils.FlashMessage;
import UiTests.utils.TestConfig;
import com.github.javafaker.Faker;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

@Epic("Authentication")
@Feature("Registration")
@Owner("SergeyQA")
@Tag("ui")
public class RegisterTests extends UiBaseTest {

    private static final String DUMMY_PASSWORD = TestConfig.validPassword();
    private final RegisterPage registerPage = new RegisterPage();

    private String generateUniqueUsername() {
        return "user" + registerPage.createRandomUsername();
    }

    @Test
    @Story("Regular username")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Register with standard username")
    void registerWithRegularUsername() {
        String username = "user" + new Faker().regexify("[A-Za-z]{5}");
        registerPage.open();
        registerPage.fillRegistrationForm(username, DUMMY_PASSWORD, DUMMY_PASSWORD);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_REGISTER.text());
    }

    @Test
    @Story("Username boundary — min")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Register with 3-character username")
    void registerWithThreeCharUsername() {
        String username = new Faker().regexify("[A-Za-z]{3}");
        registerPage.open();
        registerPage.fillRegistrationForm(username, DUMMY_PASSWORD, DUMMY_PASSWORD);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_REGISTER.text());
    }

    @Test
    @Story("Username boundary — max")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Register with 39-character username")
    void registerWithMaxLengthUsername() {
        String username = new Faker().regexify("[A-Za-z]{39}");
        registerPage.open();
        registerPage.fillRegistrationForm(username, DUMMY_PASSWORD, DUMMY_PASSWORD);
        registerPage.submit();
        assertThat(registerPage.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SUCCESS_REGISTER.text());
    }

    @Test
    @Story("Duplicate username")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Register with already existing username")
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
    @Story("Negative cases")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Validation errors during registration")
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
                .as("Case: %s", description)
                .containsIgnoringCase(expectedMessage);
        softly.assertAll();
    }

    private static Stream<Arguments> negativeCasesProvider() {
        Faker faker = new Faker();
        final String validPass = "Qwerty123!";
        return Stream.of(
                Arguments.of("Empty username", "", validPass, validPass, "All fields are required"),
                Arguments.of("Empty password", "john", "", "", "All fields are required"),
                Arguments.of("Passwords do not match", "john", "fort", "Qwerty123!", "Passwords do not match."),
                Arguments.of("Username < 3 characters", "ab", validPass, validPass, "Username must be at least 3 characters"),
                Arguments.of("Username > 39 characters", faker.regexify("[A-Za-z]{40}"), validPass, validPass, "must be between 3 and 39 characters"),
                Arguments.of("Username with space", "john doe", validPass, validPass, "Invalid username"),
                Arguments.of("Username with special characters", "john@#$", validPass, validPass, "Invalid username"),
                Arguments.of("Password shorter than 4 characters", "john", "123", "123", "Password must be at least 4 characters"),
                Arguments.of("SQL injection attempt in username", "' OR 1=1--", validPass, validPass, "Invalid username"),
                Arguments.of("XSS attempt in username", "<script>alert(1)</script>", validPass, validPass, "Invalid username"),
                Arguments.of("Password length 40 characters", "john", "Aa1!".repeat(40), "Aa1!".repeat(40), "An error occurred during registration. Please try again."),
                Arguments.of("Username starts with space", " jonh", validPass, validPass, "cannot start or end with a hyphen."),
                Arguments.of("Username ends with space", "jonh ", validPass, validPass, "cannot start or end with a hyphen."),
                Arguments.of("Username in uppercase only", "JOHN ", validPass, validPass, "cannot start or end with a hyphen."),
                Arguments.of("Password without digit", "john", "NoDigits!", "NoDigits!", "An error occurred during registration. Please try again."),
                Arguments.of("Username with only digits", "12345", validPass, validPass, "An error occurred during registration. Please try again."),
                Arguments.of("Email instead of username", "WEDIID@gmail.com", validPass, validPass, "Invalid username"),
                Arguments.of("Email instead of username", "WEDIID@gmail.com", validPass, validPass, "cannot start or end with a hyphen.")

        );
    }
}
