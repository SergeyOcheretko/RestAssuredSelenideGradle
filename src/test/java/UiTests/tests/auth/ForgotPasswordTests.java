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
@Epic("Аутентификация")
@Feature("Восстановление пароля")
@Owner("SergeyQA")
@Tag("ui")
public class ForgotPasswordTests extends UiBaseTest {

    static final ForgotPasswordPage forgotPassword = new ForgotPasswordPage();

    @Test
    @Story("Отправка запроса через кнопку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Отправка смены пароля на валидный email")
    void validSendingResetPassword() {
        String email = new Faker().internet().emailAddress();

        forgotPassword.open();
        forgotPassword.sendResetPasswordEmail(email);

        assertThat(forgotPassword.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SEND_FORGOT_PASSWORD.text());
    }

    @Test
    @Story("Отправка запроса через Enter")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Сброс пароля по Enter")
    void sendingResetPasswordByEnter() {
        forgotPassword.open();
        forgotPassword.sendResetEmailByEnter();

        assertThat(forgotPassword.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SEND_FORGOT_PASSWORD.text());
    }

    @Test
    @Story("Отправка запроса через Tab + Space")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Сброс пароля через Tab → Space")
    void sendingResetPasswordByTabSpace() {
        forgotPassword.open();
        forgotPassword.sendResetEmailByTabSpace();

        assertThat(forgotPassword.getFlashMessage())
                .containsIgnoringCase(FlashMessage.SEND_FORGOT_PASSWORD.text());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("NegativeForgotPasswordCasesProvider")
    @Story("Негативная валидация email")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Негативные кейсы Forgot Password")
    void negativeForgotPassword(String description, String email, String expectedMessage) {
        forgotPassword.open();
        forgotPassword.sendResetPasswordEmail(email);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(forgotPassword.getInvalidMessage())
                .as("Сообщение об ошибке для кейса «%s»", description)
                .containsIgnoringCase(expectedMessage);
        softly.assertAll();
    }

    private static Stream<Arguments> NegativeForgotPasswordCasesProvider() {
        return Stream.of(
                Arguments.of("XSS <script>", "<script>alert(1)</script>@test.com", "Please enter a valid email address."),
                Arguments.of("XSS img", "<img src=x onerror=alert(1)>@test.com", "Please enter a valid email address."),
                Arguments.of("XSS javascript:", "javascript:alert(1)@test.com", "Please enter a valid email address."),
                Arguments.of("SQL OR 1=1", "' OR 1=1--@test.com", "Please enter a valid email address."),
                Arguments.of("SQL UNION", "admin' UNION SELECT * FROM users--@test.com", "Please enter a valid email address."),
                Arguments.of("SQL DROP", "test'; DROP TABLE users;--@test.com", "Please enter a valid email address."),
                Arguments.of("Без @", "invalidemail.com", "Please enter a valid email address."),
                Arguments.of("Двойной @", "user@@example.com", "Please enter a valid email address."),
                Arguments.of("Пробелы", "user name@example.com", "Please enter a valid email address."),
                Arguments.of("Только цифры", "12345", "Please enter a valid email address."),
                Arguments.of("Пустая строка", "", "Please enter a valid email address."),
                Arguments.of("255 символов", "a".repeat(64) + "@" + "b".repeat(190), "Please enter a valid email address."),
                Arguments.of("Очень длинный домен", "a@b" + "c".repeat(300) + ".com", "Please enter a valid email address."),
                Arguments.of("Спецсимволы", "user<>?@example.com", "Please enter a valid email address.")
        );
    }
}
