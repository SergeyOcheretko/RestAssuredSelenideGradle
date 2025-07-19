package UiTests.pages.auth;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.Faker;
import java.util.UUID;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class LoginPage {

    private static final String URL_LOGIN = "https://practice.expandtesting.com/login";
    private static final String URL_REGISTRATION = "https://practice.expandtesting.com/register";

    private final SelenideElement usernameField = $("#username");
    private final SelenideElement passwordField = $("#password");
    private final SelenideElement loginButton = $("button[type='submit']");
    private final SelenideElement alertMessage = $("#flash");
    private final SelenideElement logOutButton = $(".icon-2x.icon-signout");
    private final Faker faker = new Faker();

    public void open() {
        Selenide.open(URL_LOGIN);
    }

    public void openRegistration() {
        Selenide.open(URL_REGISTRATION);
    }

    /**
     * Генерирует читаемое имя без точек и спецсимволов
     */
    public String randomUsername() {
        return faker.name().firstName().toLowerCase() + faker.number().digits(3);
    }

    /**
     * Генерирует случайный пароль 8–12 символов
     */
    public String randomPassword() {
        return faker.internet().password(8, 12);
    }

    /**
     * Заполняет форму логина
     */
    public void fillLoginPage(String username, String password) {
        usernameField.scrollTo().setValue(username);
        passwordField.setValue(password);
    }


    /**
     * Отправляет форму логина
     */

    public void sendLoginForm() {
        executeJavaScript("arguments[0].click();", loginButton);
    }


    /**
     * Возвращает текст сообщения во flash-блоке
     */
    public String getAlertMessage() {
        return alertMessage.shouldBe(visible).text();
    }

    /**
     * Создание юзера с 39 символами и последующим логином
     */
    public String createUsername39() {
        // 34 случайных символа + 5-буквенный префикс = 39
        return "usr" + UUID.randomUUID()
                .toString()
                .replaceAll("[^a-z0-9]", "")
                .substring(0, 36);
    }

    /**
     * Логаут
     */
    public void logOut() {

        $(logOutButton)
                .scrollIntoView("{block: \"center\", inline: \"center\"}")
                .shouldBe(visible)
                .click();
    }

}



