package UiTests.base;
import UiTests.utils.TestConfig;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import static com.codeborne.selenide.Selenide.open;

public class UiBaseTest {


    @BeforeEach
    void setUp() {



        ChromeOptions options = new ChromeOptions();

        options.addArguments("--host-resolver-rules=" +
                "MAP pagead2.googlesyndication.com 127.0.0.1," +
                "MAP googleads.g.doubleclick.net 127.0.0.1," +
                "MAP tpc.googlesyndication.com 127.0.0.1");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

            Configuration.browserCapabilities = options;
            Configuration.baseUrl     = TestConfig.baseUrl();
            Configuration.browserSize   = TestConfig.browserSize();
            Configuration.timeout       = TestConfig.timeout();
            Configuration.headless      = TestConfig.headless();
            Configuration.pageLoadTimeout = TestConfig.pageLoadTimeout();

            open(TestConfig.baseUrl());
            WebDriverRunner.getWebDriver().manage().window().maximize();
    }
    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }
}






