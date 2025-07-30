package UiTests.base;

import UiTests.utils.TestConfig;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import static com.codeborne.selenide.Selenide.open;

public abstract class UiBaseTest {

    @BeforeEach
    final void setUp() {
        Configuration.remote      = "http://localhost:4444/wd/hub";
        Configuration.baseUrl     = TestConfig.baseUrl();
        Configuration.browserSize = TestConfig.browserSize();
        Configuration.timeout     = TestConfig.timeout();
        Configuration.headless    = TestConfig.headless();
        Configuration.pageLoadTimeout = TestConfig.pageLoadTimeout();
    }

    protected void openBrowser(String browser) {
        if ("chrome".equals(browser)) {
            ChromeOptions co = new ChromeOptions();
            co.addArguments("--host-resolver-rules=MAP pagead2.googlesyndication.com 127.0.0.1,MAP googleads.g.doubleclick.net 127.0.0.1,MAP tpc.googlesyndication.com 127.0.0.1");
            co.addArguments("--disable-extensions","--disable-background-networking","--no-sandbox","--disable-dev-shm-usage");
            Configuration.browserCapabilities = co;
        } else {
            Configuration.browserCapabilities = new FirefoxOptions();
        }
        open(TestConfig.baseUrl());
    }

    @AfterEach
    final void tearDown() {
        Selenide.closeWebDriver();
    }
}