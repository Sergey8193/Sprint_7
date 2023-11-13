package ru.yandex.praktikum.qascooter.services.order;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class CancelOrderTest {

    private OrderClient orderClient;
    private int track;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        OrderData orderData = new OrderData(
                "Иван", "Петров", "г.Москва, Абрикосовский переулок, д.1",
                "5","+79136051251", 2, "2023-11-18",
                "Предварительно позвонить", List.of("BLACK")
        );
        ValidatableResponse createOrderResponse = orderClient.createOrder(orderData);
        track = createOrderResponse.extract().path("track");
    }

    @After
    public void cleanUp() {
        if (track != 0) orderClient.cancelOrder(track);
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "cancelOrder")
    @Test
    @DisplayName("'Cancel request' returns 'Ok' on success")
    @Description("Check that if 'track' is valid request returns Ok")
    public void cancelRequestReturnsOkOnSuccess() {
        ValidatableResponse response = orderClient.cancelOrder(track);
        response.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("ok", is(true));
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "cancelOrder")
    @Test
    @DisplayName("'Cancel request' returns 'error' on non-existent 'track'")
    @Description("Check if 'track' is non-existent request returns error 404")
    public void cancelRequestReturnsErrorOnNonExistentTrack() {
        orderClient.cancelOrder(track);
        ValidatableResponse response = orderClient.cancelOrder(track);
        track = 0;
        response.assertThat()
                .statusCode(SC_CONFLICT)
                .and()
                .body("message", equalTo("Заказ нельзя завершить"));
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "cancelOrder")
    @Test
    @DisplayName("'Cancel request' returns 'error' on missing 'track'")
    @Description("Check if 'track' is missing request returns error 400")
    public void cancelRequestReturnsErrorOnMissingTrack() {
        ValidatableResponse response = orderClient.cancelOrder(0);
        response.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для поиска"));
    }
}
