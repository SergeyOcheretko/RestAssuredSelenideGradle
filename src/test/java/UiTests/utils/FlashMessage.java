package UiTests.utils;

public enum FlashMessage {
    SUCCESS_REGISTER("successfully registered"),
    DUPLICATE_USERNAME("an error occurred during registration"),
    SUCCESS_LOGIN("You logged into a secure area!"),
    SUCCESS_LOGOUT("You logged out of the secure area!"),
    SEND_FORGOT_PASSWORD("An e-mail has been sent to you which explains how to reset your password.");


    private final String text;

    FlashMessage(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}