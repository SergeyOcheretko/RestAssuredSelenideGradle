package UiTests.utils;

import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class GridRunner {
    public static void main(String[] args) throws Exception {
        ChromeOptions options = new ChromeOptions();

        // options.addArguments("--headless"); // если нужен headless
        WebDriver driver = new RemoteWebDriver(
                new URL("http://localhost:4444/wd/hub"),
                options


        );
        driver.get("https://practice.expandtesting.com/");
        System.out.println("Title: " + driver.getTitle());
        driver.quit();
    }
}