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

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static ru.yandex.praktikum.qascooter.services.order.OrderDataGenerator.getRandomOrderData;

public class TrackOrderTest {

    private OrderClient orderClient;
    private int track;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        OrderData orderData = getRandomOrderData();
        ValidatableResponse createOrderResponse = orderClient.createOrder(orderData);
        track = createOrderResponse.extract().path("track");
    }

    @After
    public void cleanUp() {
        if (track != 0) orderClient.cancelOrder(track);
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "trackOrder")
    @Test
    @DisplayName("'Track order' request returns 'order object' on success")
    @Description("Check that if 'track' is existent request returns 'order object'")
    public void trackRequestReturnsOrderObjectOnSuccess() {
        ValidatableResponse response = orderClient.trackOrder(track);
        response.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("order", notNullValue())
                .and()
                .body("order.id", is(not(0)));
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "trackOrder")
    @Test
    @DisplayName("'Track order' request returns error on non-existent 'track'")
    @Description("Check that if 'track' is non-existent request returns error 404")
    public void trackRequestReturnsErrorOnNonExistentTrack() {
        orderClient.cancelOrder(track);
        ValidatableResponse response = orderClient.trackOrder(track);
        track = 0;
        response.assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Заказ не найден"));
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "trackOrder")
    @Test
    @DisplayName("'Track order' request returns error on missing 'track'")
    @Description("Check that if 'track' is missing request returns error 400")
    public void trackRequestReturnsErrorOnMissingTrack() {
        ValidatableResponse response = orderClient.trackOrder(0);
        track = 0;
        response.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для поиска"));
    }
}
