package ru.yandex.praktikum.qascooter.services.constants;

public final class EndPoints {
    public static final String BASE_URL = "http://qa-scooter.praktikum-services.ru/api/v1/";

    public static final String COURIERS_URL = BASE_URL + "courier/";
    public static final String AUTHORIZE_COURIER_URL = COURIERS_URL + "login/";
    public static final String DELETE_COURIER_URL = COURIERS_URL + "{id}";

    public static final String ORDERS_URL = BASE_URL + "orders/";
    public static final String TRACK_ORDER_URL = ORDERS_URL + "track";
    public static final String CANCEL_ORDER_URL = ORDERS_URL + "cancel";
    public static final String ACCEPT_ORDER_URL = ORDERS_URL + "accept/{id}?courierId={courierId}";
}
