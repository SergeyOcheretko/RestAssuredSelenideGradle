package UiTests.tests.forms;

import UiTests.base.UiBaseTest;
import UiTests.pages.forms.InputFormPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InputFormTests extends UiBaseTest {


InputFormPage inputFormPage = new InputFormPage();


@Test
    @DisplayName("Check Display Inputs")
void checkDisplayInputsButton(){
    inputFormPage.openInputs();
    inputFormPage.clickDisplayInputs();
    inputFormPage.checkThatInputIsDisplayed();
}

    @Test
    @DisplayName("Check Clear Inputs")
    void checkClearInputsButton(){
        inputFormPage.openInputs();
        inputFormPage.clickDisplayInputs();
        inputFormPage.clickClearInputs();
        inputFormPage.checkThatInputIsNotDisplaying();
    }




}
