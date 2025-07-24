package UiTests.pages.files;

import UiTests.utils.TestFileUtil;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DragDropUploadDownloadFilesPage {

    private static final String DRAG_AND_DROP_URL = "https://practice.expandtesting.com/drag-and-drop";
    private static final String UPLOAD_FILE = "https://practice.expandtesting.com/upload";


    private final SelenideElement elementA = $("#column-a");
    private final SelenideElement elementB = $("#column-b");
    private final SelenideElement chooseFileButton = $("#fileInput");
    private final SelenideElement uploadFileButton = $("#fileSubmit");
    private final SelenideElement fileUploadedMessage = $("div[class='container'] h1");
    private final SelenideElement flashMessage = $("div[id='flash'] b");
    private final SelenideElement please_Banner = $(".flash");


    public void open() {
        Selenide.open(DRAG_AND_DROP_URL);
    }

    public void openUploadLink() {
        Selenide.open(UPLOAD_FILE);
    }

    public void dragAndDropMethod() {
        elementA.shouldHave(text("A"));
        elementB.shouldHave(text("B"));

        elementA.dragAndDropTo(elementB);

    }

    public void checkDragAndDropResult() {
        elementA.shouldHave(text("B"));
        elementB.shouldHave(text("A"));
    }

    public void setTinyUploadFile() {
        File tiny = TestFileUtil.writeTempFile("hello.txt", "Hello World");
        $(chooseFileButton).uploadFile(tiny);

    }

    public void checkMsgAfterUploadFile() {
        $(fileUploadedMessage).shouldBe(visible)
                .shouldHave(text("File Uploaded!"));
    }

    public void setLargeUploadFile() throws IOException {
        File large = TestFileUtil.writeLargeFile("bye.txt", 750_000);
        $(chooseFileButton).uploadFile(large);
    }

    public String getFlashMessage() {
        return flashMessage.shouldBe(visible).text();
    }

    public void uploadClickButton() {
        $(uploadFileButton).click();
    }

    public void checkAlertMessage() {
        String validationText = $("input[type='file']").should(exist).getAttribute("validationMessage");
        assertThat(validationText).containsIgnoringCase("Please select a file");

    }

    public void cancelUploadingByEsc() {


        actions().sendKeys(Keys.ESCAPE).perform();

    }

    public void clickOnChooseFileButton() {
        executeJavaScript("document.querySelector('input[name=file]').click();");
    }


}




