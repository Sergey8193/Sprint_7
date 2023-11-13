package ru.yandex.praktikum.qascooter.services.courier;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static ru.yandex.praktikum.qascooter.services.courier.CourierDataGenerator.getRandomCourierData;

public class DeleteCourierTest {

    private CourierClient courierClient;
    private String courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        CourierData courierInfo = getRandomCourierData();
        courierClient.createCourier(courierInfo).path("id");
        courierId = courierClient
                .loginCourier(CourierCredentials.getCredentialsFrom(courierInfo)).path("id")
                .toString();
    }

    @After
    public void cleanUp() {
        if (!Objects.equals(courierId, null)) courierClient.deleteCourier(courierId);
    }

    @Epic(value = "Courier Client")
    @Feature(value = "operations")
    @Story(value = "deleteCourier")
    @Test
    @DisplayName("'Delete courier' request returns 'success' on existent 'id'")
    @Description("Check that if courier can be deleted")
    public void deleteRequestReturnsSuccessOnExistentId() {
        Response response = courierClient.deleteCourier(courierId);
        courierId = null;
        response.then()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat().body("ok", is(true));
    }

    @Epic(value = "Courier Client")
    @Feature(value = "operations")
    @Story(value = "deleteCourier")
    @Test
    @DisplayName("'Delete courier' request returns 'error' on missing 'id'")
    @Description("Check that if courierId is missing request returns error 400")
    public void  deleteRequestReturnsErrorOnMissingCourierId() {
        Response response = courierClient.deleteCourier("");
        response.then()
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .assertThat().body("message", equalTo("Not Found."));
    }

    @Epic(value = "Courier Client")
    @Feature(value = "operations")
    @Story(value = "deleteCourier")
    @Test
    @DisplayName("'Delete courier' request returns 'error' on non-existent 'id'")
    @Description("Check that if courierId is non-existent request returns error 404")
    public void deleteRequestReturnsErrorOnOnNonExistentCourierId() {
        courierClient.deleteCourier(courierId);
        Response response = courierClient.deleteCourier(courierId);
        response.then()
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .assertThat().body("message", equalTo("Курьера с таким id нет."));
    }
}
