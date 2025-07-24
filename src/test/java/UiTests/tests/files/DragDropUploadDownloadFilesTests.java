package UiTests.tests.files;

import UiTests.base.UiBaseTest;
import UiTests.pages.files.DragDropUploadDownloadFilesPage;
import UiTests.utils.FlashMessage;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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


}
