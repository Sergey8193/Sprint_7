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
import ru.yandex.praktikum.qascooter.services.courier.CourierData;
import ru.yandex.praktikum.qascooter.services.courier.CourierClient;

import java.util.Objects;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;
import static ru.yandex.praktikum.qascooter.services.courier.CourierCredentials.getCredentialsFrom;
import static ru.yandex.praktikum.qascooter.services.courier.CourierDataGenerator.getRandomCourierData;
import static ru.yandex.praktikum.qascooter.services.order.OrderDataGenerator.generateInt;
import static ru.yandex.praktikum.qascooter.services.order.OrderDataGenerator.getRandomOrderData;

public class AcceptOrderTest {

    private final int FROM = 1000000;
    private final int TO = 1200000;

    private OrderClient orderClient;
    private CourierClient courierClient;

    private int track;
    private Integer orderId;
    private Integer courierId;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        OrderData orderData = getRandomOrderData();

        ValidatableResponse createResponse = orderClient.createOrder(orderData);
        track = createResponse.extract().path("track");

        ValidatableResponse trackResponse = orderClient.trackOrder(track);
        orderId = trackResponse.extract().body().path("order.id");

        courierClient = new CourierClient();
        CourierData courierData = getRandomCourierData();
        courierClient.createCourier(courierData);
        courierId = courierClient.loginCourier(getCredentialsFrom(courierData)).path("id");
    }

    @After
    public void cleanUp() {
        if (track != 0) orderClient.cancelOrder(track);
        if (!Objects.equals(courierId, null)) courierClient.deleteCourier(String.valueOf(courierId));
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "acceptOrder")
    @Test
    @DisplayName("'Accept order' request returns 'success' on existent order 'id' and 'courierId'")
    @Description("Check that if order 'id' and 'courierId' are existent request returns success")
    public void acceptRequestReturnsSuccessOnExistentIdAndCourierId() {
        ValidatableResponse response = orderClient.acceptOrder(orderId, courierId);
        response.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("ok", is(true));
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "acceptOrder")
    @Test
    @DisplayName("'Accept order' request returns 'error' on non-existent order 'id'")
    @Description("Check that if order 'id' is non-existent request returns error 404")
    public void acceptRequestReturnsErrorOnNonExistentId() {
        Integer nonExistentOrderId = orderId + generateInt(FROM, TO);
        ValidatableResponse response = orderClient.acceptOrder(nonExistentOrderId, courierId);
        response.assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Заказа с таким id не существует"));
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "acceptOrder")
    @Test
    @DisplayName("'Accept order' request returns 'error' on non-existent 'courierId'")
    @Description("Check that if 'courierId' is non-existent request returns error 404")
    public void acceptRequestReturnsErrorOnNonExistentCourierId() {
        Integer nonExistentCourierId = courierId + generateInt(FROM, TO);
        ValidatableResponse response = orderClient.acceptOrder(orderId, nonExistentCourierId);
        response.assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "acceptOrder")
    @Test
    @DisplayName("'Accept order' request returns 'error' on already accepted order")
    @Description("Check that if order is already accepted request returns error 409")
    public void acceptRequestReturnsErrorOnAlreadyAcceptedOrder() {
        orderClient.acceptOrder(orderId, courierId);
        ValidatableResponse response = orderClient.acceptOrder(orderId, courierId);
        response.assertThat()
                .statusCode(SC_CONFLICT)
                .and()
                .body("message", equalTo("Этот заказ уже в работе"));
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "acceptOrder")
    @Test
    @DisplayName("'Accept order' request returns 'error' on missing order 'id'")
    @Description("Check that if order 'id' is missing request returns error 404")
    public void acceptRequestReturnsErrorOnMissingId() {
        ValidatableResponse response = orderClient.acceptOrder(null, courierId);
        response.assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Not Found."));
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "acceptOrder")
    @Test
    @DisplayName("'Accept order' request returns 'error' on missing 'courierId'")
    @Description("Check that if 'courierId' is missing request returns error 400")
    public void acceptRequestReturnsErrorOnMissingCourierId() {
        ValidatableResponse response = orderClient.acceptOrder(orderId, null);
        response.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для поиска"));
    }
}
