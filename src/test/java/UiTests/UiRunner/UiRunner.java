package UiTests.UiRunner;

import UiTests.tests.auth.ForgotPasswordTests;
import UiTests.tests.auth.LoginTests;
import UiTests.tests.auth.RegisterTests;
import UiTests.tests.files.DragDropUploadDownloadFilesTests;
import UiTests.tests.forms.FormValidationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("junit-jupiter")
@DisplayName("Ui Suite")
@SelectClasses({
        RegisterTests.class,
        LoginTests.class,
        ForgotPasswordTests.class,
        DragDropUploadDownloadFilesTests.class,
        FormValidationTests.class

})

public class UiRunner {
}
