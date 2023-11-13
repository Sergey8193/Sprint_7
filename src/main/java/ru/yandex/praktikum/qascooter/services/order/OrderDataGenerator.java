package ru.yandex.praktikum.qascooter.services.order;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.List;

public class OrderDataGenerator {

    private static final int MIN_NAME_COUNT = 2;
    private static final int MAX_NAME_COUNT = 4;
    private static final int MIN_LAST_NAME_COUNT = 4;
    private static final int MAX_LAST_NAME_COUNT = 6;
    private static final int MIN_CITY_COUNT = 4;
    private static final int MAX_CITY_COUNT = 7;
    private static final int MIN_STREET_COUNT = 5;
    private static final int MAX_STREET_COUNT = 8;
    private static final int MIN_APT_NUMBER = 1;
    private static final int MAX_APT_NUMBER = 40;
    private static final int MIN_METRO_STATION_NUMBER = 1;
    private static final int MAX_METRO_STATION_NUMBER = 100;
    private static final int MIN_PHONE_COUNT = 10;
    private static final int MAX_PHONE_COUNT = 10;
    private static final int MIN_RENT_TIME = 1;
    private static final int MAX_RENT_TIME = 7;
    private static final int MIN_WORD_COUNT = 3;
    private static final int MAX_WORD_COUNT_1 = 6;
    private static final int MAX_WORD_COUNT_2 = 7;
    private static final int MAX_WORD_COUNT_3 = 8;
    private static final int MIN_COMMENT_LENGTH = 3;
    private static final int MAX_COMMENT_LENGTH = 5;

    public static OrderData getRandomOrderData() {
        String firstName = generateFirstName();
        String lastName = generateLastName();
        String address = generateAddress();
        String metroStation = generateMetroStation();
        String phone = generatePhone();
        int rentTime = generateRentTime();
        String deliveryDate = generateDeliveryDate();
        String comment = generateComment();
        List <String> color = generateColor();

        return new OrderData(firstName, lastName,
                address, metroStation, phone,
                rentTime, deliveryDate, comment, color);
    }

    public static String generateFirstName() {
        return generateString(generateInt(MIN_NAME_COUNT, MAX_NAME_COUNT), "L", "d");
    }
    public static String generateLastName() {
        return generateString(generateInt(MIN_LAST_NAME_COUNT, MAX_LAST_NAME_COUNT), "D", "off");
    }

    public static String generateAddress() {
        return generateFromToString(MIN_CITY_COUNT, MAX_CITY_COUNT, "", " city, ") +
                generateFromToString(MIN_STREET_COUNT, MAX_STREET_COUNT, "", " street, ") +
                generateFromToNumberString(MIN_APT_NUMBER, MAX_APT_NUMBER, "apt. ", "");
    }

    public static String generateMetroStation() {
        return generateInt(MIN_METRO_STATION_NUMBER, MAX_METRO_STATION_NUMBER).toString();
    }

    public static String generatePhone() {
        return generateFromToStringOfNumbers(MIN_PHONE_COUNT, MAX_PHONE_COUNT,"+7", "");
    }

    public static int generateRentTime() {
        return generateInt(MIN_RENT_TIME, MAX_RENT_TIME);
    }

    public static String generateDeliveryDate() {
        return LocalDate.now().plusDays(generateInt(1, 31)).toString();
    }

    public static String generateComment() {
        StringBuilder randomComment =
                new StringBuilder(generateFromToString(MIN_WORD_COUNT, MAX_WORD_COUNT_2, "", " "));
        for (int i = 0; i < generateInt(MIN_COMMENT_LENGTH - 2, MAX_COMMENT_LENGTH - 2); i++) {
            randomComment.append(generateFromToString(MIN_WORD_COUNT, MAX_WORD_COUNT_3, "", " "));
        }
        randomComment.append(generateFromToString(MIN_WORD_COUNT, MAX_WORD_COUNT_1, "", ""));
        return randomComment.toString();
    }

    public static List <String> generateColor() {
        List <String> color = null;
        switch (generateInt(1, 4)) {
            case 2: color = List.of("BLACK"); break;
            case 3: color = List.of("GRAY"); break;
            case 4: color = List.of("BLACK", "GRAY"); break;
        }
        return color;
    }

    public static Integer generateInt(int from, int to ) {
        return from + (int) ( Math.random() * (to - from + 1));
    }

    private static String generateString(int count, String prefix, String postfix) {
        return prefix + RandomStringUtils.randomAlphabetic(count) + postfix;
    }

    private static String generateFromToString(int from, int to, String prefix, String postfix) {
        return prefix + RandomStringUtils.randomAlphabetic(generateInt(from, to)) + postfix;
    }

    private static String generateFromToNumberString(int from, int to, String prefix, String postfix) {
        return prefix + generateInt(from, to) + postfix;
    }

    private static String generateFromToStringOfNumbers(int from, int to, String prefix, String postfix) {
        StringBuilder randomNumber = new StringBuilder(RandomStringUtils.random(generateInt(from, to), "0123456789"));
        if (randomNumber.charAt(0) == '0') {
            randomNumber.replace(0,1, RandomStringUtils.random(1, "123456789"));
        }
        return prefix + randomNumber + postfix;
    }
}
