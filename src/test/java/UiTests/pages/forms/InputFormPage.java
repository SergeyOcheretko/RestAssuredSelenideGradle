package UiTests.pages.forms;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.*;
import static com.codeborne.selenide.Condition.*;

public class InputFormPage {

    private static final String INPUT_FORM_URL =
            "https://practice.expandtesting.com/inputs";

    private static final SelenideElement displayInputs     = $("#btn-display-inputs");
    private static final SelenideElement clearInputs       = $("#btn-clear-inputs");
    private static final SelenideElement resultDisplayArea = $("#result");

    public void openInputs() {
        Selenide.open(INPUT_FORM_URL);
    }

    public void clickDisplayInputs() {
        displayInputs.click();
    }

    public void checkThatInputIsDisplayed() {
        resultDisplayArea.shouldBe(visible);
    }

    public void clickClearInputs() {
        clearInputs.click();
    }

    public void checkThatInputIsNotDisplaying() {
        resultDisplayArea.shouldHave(exactText(""));
    }
}