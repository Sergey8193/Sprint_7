package ru.yandex.praktikum.qascooter.services.courier;

import org.apache.commons.lang3.RandomStringUtils;

public class CourierDataGenerator {

    private static final int LOGIN_COUNT = 5;
    private static final int PASSWORD_COUNT = 8;
    private static final int FIRST_NAME_COUNT = 10;

    public static CourierData getRandomCourierData() {
        String login = generateLogin();
        String password = generatePassword();
        String firstName = generateFirstName();
        return new CourierData(login, password, firstName);
    }

    private static String generateString(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static String generateLogin() {
        return generateString(LOGIN_COUNT);
    }

    public static String generatePassword() {
        return generateString(PASSWORD_COUNT);
    }

    public static String generateFirstName() {
        return generateString(FIRST_NAME_COUNT);
    }
}
