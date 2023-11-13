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

import java.util.List;
import java.util.Objects;

import static org.apache.http.HttpStatus.SC_OK;
import org.assertj.core.api.SoftAssertions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.yandex.praktikum.qascooter.services.courier.CourierCredentials.getCredentialsFrom;
import static ru.yandex.praktikum.qascooter.services.courier.CourierDataGenerator.getRandomCourierData;
import static ru.yandex.praktikum.qascooter.services.order.OrderDataGenerator.getRandomOrderData;

public class GetOrdersTest {

    private SoftAssertions softAssertions;
    private CourierClient courierClient;
    private OrderClient orderClient;
    private Integer courierId;
    private int track;
    private List <Order> orders;

    @Before
    public void setUp() {
        softAssertions = new SoftAssertions();
        courierClient = new CourierClient();
        orderClient = new OrderClient();
    }

    @After
    public void cleanUp() {
        if (!Objects.equals(courierId, null)) courierClient.deleteCourier(courierId.toString());
        if (track != 0) orderClient.cancelOrder(track);
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "getOrders")
    @Test
    @DisplayName("'Get orders' request returns 'order list' on success")
    @Description("Check that if response body contains order list")
    public void getOrdersRequestReturnsOrderListOnSuccess() {
        final int ORDER_LIST_SIZE = 20;
        ValidatableResponse response = orderClient.getOrders(courierId, null, ORDER_LIST_SIZE, 0);
        orders = response
                .extract()
                .body()
                .jsonPath()
                .getList("orders", Order.class);

        int statusCode = response.extract().statusCode();
        assertEquals(String.format("Status code is wrong. Expected %d", SC_OK), SC_OK, statusCode);

        boolean ordersListIsNotEmpty = !Objects.equals(orders, null)  && !orders.isEmpty();
        assertTrue("Orders list is empty", ordersListIsNotEmpty);

        boolean ordersSizeIsValid = orders.size() == ORDER_LIST_SIZE;
        assertTrue(String.format("Wrong orders list size. Expected %d", ORDER_LIST_SIZE), ordersSizeIsValid);
    }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "getOrders")
    @Test
    @DisplayName("'Get orders' request returns just created order accepted by just created courier")
    @Description("Check that if new order accepted by new courier request returns order list with only this new order")
    public void getOrdersRequestReturnsJustCreatedOrderAcceptedByJustCreatedCourier() {
        CourierData courierData = getRandomCourierData();
        courierClient.createCourier(courierData);
        courierId = courierClient.loginCourier(getCredentialsFrom(courierData)).path("id");

        OrderClient orderClient = new OrderClient();
        OrderData orderData = getRandomOrderData();
        ValidatableResponse createResponse = orderClient.createOrder(orderData);

        track = createResponse.extract().path("track");

        ValidatableResponse trackResponse = orderClient.trackOrder(track);
        Order order = trackResponse.extract().body().jsonPath().getObject("order", Order.class);
        Integer orderId = order.getId();

        ValidatableResponse acceptOrderResponse = orderClient.acceptOrder(orderId, courierId);
        int acceptStatusCode = acceptOrderResponse.extract().statusCode();
        boolean isOkTrue = acceptOrderResponse.extract().body().path("ok");

        softAssertions.assertThat(acceptStatusCode).isEqualTo(SC_OK);
        softAssertions.assertThat(isOkTrue).isEqualTo(true);

        ValidatableResponse getOrdersResponse = orderClient.getOrders(courierId, null, null, null);
        int getOrdersStatusCode = getOrdersResponse.extract().statusCode();
        orders = getOrdersResponse.extract().body().jsonPath().getList("orders", Order.class);
        boolean isOrdersNotEmpty = !orders.isEmpty();

        softAssertions.assertThat(getOrdersStatusCode).isEqualTo(SC_OK);
        softAssertions.assertThat(isOrdersNotEmpty).isEqualTo(true);
        softAssertions.assertThat(orders.get(0).getTrack()).isEqualTo(track);
        softAssertions.assertThat(orders.get(0).getCourierId()).isEqualTo(courierId);
        softAssertions.assertThat(orders.get(0).getId()).isEqualTo(orderId);
        softAssertions.assertAll();
    }
}
