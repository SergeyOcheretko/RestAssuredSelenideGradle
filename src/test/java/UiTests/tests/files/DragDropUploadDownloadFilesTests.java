package UiTests.tests.files;

import UiTests.base.UiBaseTest;
import UiTests.pages.files.DragDropUploadDownloadFilesPage;
import UiTests.utils.FlashMessage;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.json.Json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Epic("Files")
@Feature("Work with files")
@Owner("SergeyQA")
@Tag("ui")
public class DragDropUploadDownloadFilesTests extends UiBaseTest {


    DragDropUploadDownloadFilesPage dragDropUploadDownloadFilesPage = new DragDropUploadDownloadFilesPage();


    @Test
    @DisplayName("Drag and Drop file")
    void dragAndDropTest() {
        dragDropUploadDownloadFilesPage.open();
        dragDropUploadDownloadFilesPage.dragAndDropMethod();
        dragDropUploadDownloadFilesPage.checkDragAndDropResult();
    }

    @Test
    @DisplayName("Upload file <500 KB")
    void uploadFile() {
        dragDropUploadDownloadFilesPage.openUploadLink();
        dragDropUploadDownloadFilesPage.setTinyUploadFile();
        dragDropUploadDownloadFilesPage.uploadClickButton();
        dragDropUploadDownloadFilesPage.checkMsgAfterUploadFile();
    }

    @Test
    @DisplayName("Check upload file >500 KB")
    void uploadLargeFile() throws IOException {
        dragDropUploadDownloadFilesPage.openUploadLink();
        dragDropUploadDownloadFilesPage.setLargeUploadFile();
        dragDropUploadDownloadFilesPage.uploadClickButton();

        assertThat(dragDropUploadDownloadFilesPage.getFlashMessage()).isEqualToIgnoringCase(FlashMessage.FILE_TOO_LARGE.text());


    }

    @Test
    @DisplayName("Check Upload button without uploading file")
    void checkUploadWithoutUploading() {
        dragDropUploadDownloadFilesPage.openUploadLink();
        dragDropUploadDownloadFilesPage.uploadClickButton();

        dragDropUploadDownloadFilesPage.checkAlertMessage();
        assertThat(WebDriverRunner.getWebDriver().getCurrentUrl())
                .endsWith("/upload");
    }

    @Test
    @DisplayName("Cancel Uploading via Escape")
    void cancelFileUploading() {
        dragDropUploadDownloadFilesPage.openUploadLink();
        dragDropUploadDownloadFilesPage.clickOnChooseFileButton();
        dragDropUploadDownloadFilesPage.cancelUploadingByEsc();
    }

@Test
    @DisplayName("Download JPG file")
    void checkDownloadJpgFile() throws FileNotFoundException {
        dragDropUploadDownloadFilesPage.openDownloadPageLink();

    File jpg = dragDropUploadDownloadFilesPage.downloadJPGFile();
    assertThat(jpg).exists();
    assertThat(jpg.getName()).isEqualTo("cdct.jpg");

    }
    @Test
    @DisplayName("Download Json file")
    void checkDownloadJsonFile() throws FileNotFoundException {
        dragDropUploadDownloadFilesPage.openDownloadPageLink();

        File Json = dragDropUploadDownloadFilesPage.downloadJSONFile();
        assertThat(Json).exists();
        assertThat(Json.getName()).isEqualTo("some-file.json");

    }

    @Test
    @DisplayName("Download TXT file")
    void checkDownloadTXTFile() throws FileNotFoundException {
        dragDropUploadDownloadFilesPage.openDownloadPageLink();

        File TXT = dragDropUploadDownloadFilesPage.downloadTXTFile();
        assertThat(TXT).exists();
        assertThat(TXT.getName()).isEqualTo("some-file.txt");

    }
    @Test
    @DisplayName("Download PNG file")
    void checkDownloadPNGFile() throws FileNotFoundException {
        dragDropUploadDownloadFilesPage.openDownloadPageLink();

        File PNG = dragDropUploadDownloadFilesPage.downloadPNGFile();
        assertThat(PNG).exists();
        assertThat(PNG.getName()).isEqualTo("wdio.png");

    }

}
