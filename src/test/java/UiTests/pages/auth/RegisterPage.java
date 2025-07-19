package UiTests.pages.auth;


import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.Faker;

import java.util.UUID;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {

    private static final String URL = "https://practice.expandtesting.com/register";

    private final SelenideElement usernameField = $("#username");
    private final SelenideElement passwordField = $("#password");
    private final SelenideElement confirmField = $("#confirmPassword");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement flashMessage = $("#flash b");



    public void open() {
        Selenide.open(URL);
    }

    /**
     * Заполняет форму регистрации
     */
    public void fillRegistrationForm(String username, String password, String confirmPassword ) {
        usernameField.scrollIntoView(true).setValue(username);
        passwordField.setValue(password);
        confirmField.setValue(confirmPassword);    }

    /**
     * Отправляет форму
     */
    public void submit() {
        Selenide.executeJavaScript("arguments[0].click();", submitButton);
    }


    /**
     * Возвращает текст сообщения во flash-блоке
     */
    public String getFlashMessage() {
        return flashMessage.shouldBe(visible).text();
    }

    /**
     * Комплексный метод для быстрой регистрации нового пользователя
     */

    public String createRandomUsername() {
        return "user" + UUID.randomUUID()
                .toString()
                .replaceAll("[^a-z0-9]", "") // убираем дефисы
                .substring(0, 6);            // 6 символов
    }
}

