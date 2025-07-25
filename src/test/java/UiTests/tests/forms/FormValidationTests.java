package UiTests.tests.forms;

import UiTests.base.UiBaseTest;
import UiTests.pages.forms.FormValidationPage;
import UiTests.utils.FlashMessage;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Validation")
@Feature("Validation form")
@Owner("SergeyQA")
@Tag("ui")
public class FormValidationTests extends UiBaseTest {

    FormValidationPage formValidationPage = new FormValidationPage();


    @Test
    @DisplayName("Happy-path form validation")
    void happyPath() {
        new FormValidationPage();
        formValidationPage.openFormValidationPage();
        formValidationPage.fill("Contact Name", "John Doe");
        formValidationPage.fill("Contact number", "123-4567890");
        formValidationPage.fill("PickUp Date", LocalDate.now().plusDays(1).toString());
        formValidationPage.pick("cash on delivery");
        formValidationPage.submit();
        assertThat(formValidationPage.getFlashMessage()).containsIgnoringCase(FlashMessage.VALIDATION_TICKET_SUCCESS.text());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("negativeFormValidationCaseProvider")
    @Story("Negative Validation Form")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Negative validation form scenarios")
    void negativeValidationForm(String description, String contactName, String contactNumber, String pickUpDate, String paymentMethod, String expectedMessage) {
        formValidationPage.openFormValidationPage();
        formValidationPage.fill("Contact Name", contactName);
        formValidationPage.fill("Contact number", contactNumber);
        formValidationPage.fill("PickUp Date", pickUpDate);
        if (!paymentMethod.isBlank()) {
            formValidationPage.pick(paymentMethod);
        }

        formValidationPage.submit();
        assertThat(formValidationPage.allAlertTexts())
                .as("Error message for case: %s", description)
                .contains(expectedMessage.trim());


    }

    private static Stream<Arguments> negativeFormValidationCaseProvider() {
        return Stream.of(
                // 1-я половина кейсов → "card"
                Arguments.of("Empty Contact Name", "", "123-4567890", LocalDate.now().plusDays(1).toString(), "card", "Please enter your Contact name."),
                Arguments.of("Empty Contact Number", "John Doe", "", LocalDate.now().plusDays(1).toString(), "card", "Please provide your Contact number."),
                Arguments.of("Empty PickUp Date", "John Doe", "123-4567890", "", "card", "Please provide valid Date."),
                Arguments.of("Payment Method Not Selected", "John Doe", "123-4567890", LocalDate.now().plusDays(1).toString(), "", "Please select the Paymeny Method."),
                Arguments.of("Contact Number < 10 digits", "John Doe", "123", LocalDate.now().plusDays(1).toString(), "card", "Please provide your Contact number."),
                Arguments.of("Contact Number letters only", "John Doe", "abcdefghij", LocalDate.now().plusDays(1).toString(), "card", "Please provide your Contact number."),
                Arguments.of("Contact Number special chars", "John Doe", "!@#$%^&*()", LocalDate.now().plusDays(1).toString(), "card", "Please provide your Contact number."),

                // 2-я половина кейсов → "cash on delivery"
                Arguments.of("Phone only spaces", "John Doe", "   ", LocalDate.now().plusDays(1).toString(), "cash on delivery", "Please provide your Contact number."),
                Arguments.of("Phone format (123) 456-7890", "John Doe", "(123) 456-7890", LocalDate.now().plusDays(1).toString(), "cash on delivery", "Please provide your Contact number."),
                Arguments.of("Phone format 123-45-6789", "John Doe", "123-45-6789", LocalDate.now().plusDays(1).toString(), "cash on delivery", "Please provide your Contact number."),
                Arguments.of("Contact Number only spaces", "John Doe", "   ", LocalDate.now().plusDays(1).toString(), "cash on delivery", "Please provide your Contact number.")
        );
    }
}









