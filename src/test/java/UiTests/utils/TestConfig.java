package UiTests.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {

    private static final Properties PROPS = loadProps();

    private static Properties loadProps() {
        Properties p = new Properties();
        try (InputStream in = TestConfig.class
                .getClassLoader()
                .getResourceAsStream("application-test.properties")) {
            if (in == null) {
                throw new IllegalStateException("application-test.properties не найден");
            }
            p.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить конфиг", e);
        }
        return p;
    }

    private static String get(String key) {
        return PROPS.getProperty(key, "NOT_SET");
    }

    public static String baseUrl() {
        return get("selenide.base-url");
    }

    public static String browser() {
        return get("selenide.browser");
    }

    public static String browserSize() {
        return get("selenide.browser-size");
    }

    public static int timeout() {
        return Integer.parseInt(get("selenide.timeout"));
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("selenide.headless"));
    }

    public static long pageLoadTimeout() {
        return Long.parseLong(get("selenide.page-load-timeout"));
    }

    public static String defaultPassword() {
        return get("test-data.default-password");
    }

    public static String validUsername() {
        return get("test-data.valid-user.username");
    }

    public static String validPassword() {
        return get("test-data.valid-user.password");
    }
}