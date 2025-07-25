package UiTests.tests.auth;

import UiTests.base.UiBaseTest;
import UiTests.pages.auth.ForgotPasswordPage;
import com.github.javafaker.Faker;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import UiTests.utils.FlashMessage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Authentication")
@Feature("Password recovery")
@Owner("SergeyQA")
@Tag("ui")
public class ForgotPasswordTests extends UiBaseTest {

    static final ForgotPasswordPage forgotPassword = new ForgotPasswordPage();

    @Test
    @Story("Trigger request via button")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Send password reset request to valid email")
    void validSendingResetPassword() {
        String email = new Faker().internet().emailAddress();

        forgotPassword.open();
        forgotPassword.sendResetPasswordEmail(email);

        assertThat(forgotPassword.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SEND_FORGOT_PASSWORD.text());
    }

    @Test
    @Story("Trigger request via Enter")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Send password reset request using Enter")
    void sendingResetPasswordByEnter() {
        forgotPassword.open();
        forgotPassword.sendResetEmailByEnter();

        assertThat(forgotPassword.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SEND_FORGOT_PASSWORD.text());
    }

    @Test
    @Story("Trigger request via Tab + Space")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Send password reset request via Tab → Space")
    void sendingResetPasswordByTabSpace() {
        forgotPassword.open();
        forgotPassword.sendResetEmailByTabSpace();

        assertThat(forgotPassword.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SEND_FORGOT_PASSWORD.text());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("negativeForgotPasswordCasesProvider")
    @Story("Negative email validation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Negative Forgot Password test cases")
    void negativeForgotPassword(String description, String email, String expectedMessage) {
        forgotPassword.open();
        forgotPassword.sendResetPasswordEmail(email);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(forgotPassword.getInvalidMessage())
                .as("Error message for case: %s", description)
                .containsIgnoringCase(expectedMessage);
        softly.assertAll();
    }

    private static Stream<Arguments> negativeForgotPasswordCasesProvider() {
        return Stream.of(
                Arguments.of("XSS <script>", "<script>alert(1)</script>@test.com", "Please enter a valid email address."),
                Arguments.of("XSS img tag", "<img src=x onerror=alert(1)>@test.com", "Please enter a valid email address."),
                Arguments.of("XSS javascript:", "javascript:alert(1)@test.com", "Please enter a valid email address."),
                Arguments.of("SQL OR 1=1", "' OR 1=1--@test.com", "Please enter a valid email address."),
                Arguments.of("SQL UNION", "admin' UNION SELECT * FROM users--@test.com", "Please enter a valid email address."),
                Arguments.of("SQL DROP", "test'; DROP TABLE users;--@test.com", "Please enter a valid email address."),
                Arguments.of("Missing @ symbol", "invalidemail.com", "Please enter a valid email address."),
                Arguments.of("Double @ symbol", "user@@example.com", "Please enter a valid email address."),
                Arguments.of("Email with spaces", "user name@example.com", "Please enter a valid email address."),
                Arguments.of("Numeric-only input", "12345", "Please enter a valid email address."),
                Arguments.of("Empty input", "", "Please enter a valid email address."),
                Arguments.of("255 characters", "a".repeat(64) + "@" + "b".repeat(190), "Please enter a valid email address."),
                Arguments.of("Very long domain", "a@b" + "c".repeat(300) + ".com", "Please enter a valid email address."),
                Arguments.of("Special characters", "user<>?@example.com", "Please enter a valid email address.")
        );
    }
}
