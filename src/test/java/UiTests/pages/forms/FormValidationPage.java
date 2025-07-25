package UiTests.pages.forms;

import com.codeborne.selenide.*;
import java.time.LocalDate;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class FormValidationPage {

    private static final String FORM_VALIDATION_URL = "https://practice.expandtesting.com/form-validation";

    private final SelenideElement flashMessage = $("div[role='alert']");


    public void openFormValidationPage(){
Selenide.open(FORM_VALIDATION_URL); }

    /** Text inputs (Name, Number, Date) */
    public FormValidationPage fill(String label, String value) {
        $x("//label[contains(text(),'" + label + "')]//following::input[1]")
                .setValue(value);
        return this;
    }

    /** Drop-down selector */
    public FormValidationPage pick(String optionValue) {
        $("#validationCustom04").selectOption(optionValue);
        return this;
    }

    /** Submit & verify success */
    public FormValidationPage submit() {
        $("button[type='submit']").click();
        return this;
    }
    public String getFlashMessage() {
        return flashMessage.shouldBe(visible).text();
    }
    public List<String> allAlertTexts() {
        return $$(".invalid-feedback")
                .filter(Condition.visible)
                .texts()
                .stream()
                .map(String::trim)
                .toList();
    }

}