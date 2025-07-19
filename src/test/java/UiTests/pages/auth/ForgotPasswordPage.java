package UiTests.pages.auth;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.github.javafaker.Faker;
import org.openqa.selenium.interactions.Actions;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ForgotPasswordPage {

    private static final String URL_FORGOT_PASSWORD = "https://practice.expandtesting.com/forgot-password";

    private  final SelenideElement EMAIL_FIELD = $("#email");
 private  final SelenideElement RETRIEVE_PASSWORD = $("button[type='submit']");
    private final SelenideElement flashMessage = $("div[id='confirmation-alert'] p");
    private final SelenideElement invalidEmailMsg= $(".ms-1.invalid-feedback");


   Faker faker = new Faker();
    public void open() {
        Selenide.open(URL_FORGOT_PASSWORD);
    }

    public void sendResetPasswordEmail(String email) {
        EMAIL_FIELD.setValue(email);
      RETRIEVE_PASSWORD.click();
    };
    public String getFlashMessage() {
        return flashMessage.shouldBe(visible).text();
    }
  public String getInvalidMessage(){
        return invalidEmailMsg.shouldBe(visible).text();
  }

    public void sendResetEmailByEnter() {
        String email=faker.internet().emailAddress();

        EMAIL_FIELD.setValue(email).pressEnter();
    }

    public void sendResetEmailByTabSpace() {
        String email=faker.internet().emailAddress();
        EMAIL_FIELD.setValue(email).pressTab();
        Actions actions = new Actions(WebDriverRunner.getWebDriver());
        RETRIEVE_PASSWORD.shouldBe(Condition.focused);
        actions.sendKeys(" ").perform();

    }

}
