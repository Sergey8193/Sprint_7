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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.*;
import static ru.yandex.praktikum.qascooter.services.order.OrderDataGenerator.getRandomOrderData;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final List <String> COLOR;

    private OrderClient orderClient;
    private OrderData orderData;
    private int track;

    public CreateOrderTest(List <String> color) {
        this.COLOR = color;
    }

    @Parameterized.Parameters(name = "'Create order' request returns 'track' on success ( COLOR: {0} )")
    public static Object[][] getTestData() {
        return new Object[][] {
                { List.of("BLACK") },
                { List.of("GREY") },
                { List.of("BLACK", "GREY") },
                { null },
        };
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        orderData = getRandomOrderData();
        orderData.setColor(COLOR);
    }

    @After
    public void cleanUp() { if (track != 0) orderClient.cancelOrder(track); }

    @Epic(value = "Order client")
    @Feature(value = "operations")
    @Story(value = "createOrder")
    @Test
    @DisplayName("'Create order' request returns 'track' on success")
    @Description("Check that request returns success on any of possible 'color' value")
    public void createRequestReturnsTrackOnSuccess() {
        ValidatableResponse response = orderClient.createOrder(orderData);
        track = response.extract().path("track");
        response.assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body("track", greaterThan(0));
    }
}
