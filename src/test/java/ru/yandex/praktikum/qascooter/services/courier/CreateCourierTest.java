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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.yandex.praktikum.qascooter.services.courier.CourierCredentials.getCredentialsFrom;
import static ru.yandex.praktikum.qascooter.services.courier.CourierDataGenerator.*;

public class CreateCourierTest {

    private CourierData courierData;
    private Integer courierId;
    private CourierClient courierClient;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courierData = getRandomCourierData();
    }

    @After
    public void cleanUp() {
        if (!Objects.equals(courierId, null)) courierClient.deleteCourier(courierId.toString());
    }

    @Epic(value = "Courier Client")
    @Feature(value = "operations")
    @Story(value = "createCourier")
    @Test
    @DisplayName("'Create courier' request returns 'success'")
    @Description("Check that if 'courier' can be created")
    public void createCourierRequestReturnsSuccessThenCourierSuccessfullyLogsIn() {
        CourierData courierData = getRandomCourierData();
        Response response = courierClient.createCourier(courierData);
        int createStatusCode = response.then().extract().statusCode();
        boolean isCourierCreated = response.then().extract().path("ok");

        assertEquals(String.format("Wrong status code. Expected %d", SC_CREATED), SC_CREATED, createStatusCode);
        assertTrue("Wrong success createCourier response message. Expected 'ok: true'", isCourierCreated);

        courierId = courierClient
                .loginCourier(getCredentialsFrom(courierData))
                .path("id");

        boolean isCourierIdCreated = !Objects.equals(courierId, null) && courierId > 0;
        assertTrue("Courier login is failed. Expected not null courierId.", isCourierIdCreated);
    }

    @Epic(value = "Courier Client")
    @Feature(value = "operations")
    @Story(value = "createCourier")
    @Test
    @DisplayName("'Create courier' request returns 'success' on missing 'firstName'")
    @Description("Check that if 'firstName' field is not required")
    public void createRequestReturnsSuccessOnMissingFirstName() {
        courierData.setFirstName(null);
        Response response = courierClient.createCourier(courierData);
        response.then()
                .assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body("ok", is(true));

        courierId = courierClient
                .loginCourier(getCredentialsFrom(courierData))
                .path("id");
    }

    @Epic(value = "Courier Client")
    @Feature(value = "operations")
    @Story(value = "createCourier")
    @Test
    @DisplayName("'Create courier' request returns 'error' on use already existent 'login'")
    @Description("Check if creating of two couriers with identical login is impossible with error 409")
    public void createRequestReturnsErrorOnUseAlreadyExistentLogin() {
        courierClient.createCourier(courierData);
        courierId = courierClient
                .loginCourier(getCredentialsFrom(courierData))
                .path("id");

        CourierData anotherCourierData = new CourierData(courierData.getLogin(), generatePassword(), generateFirstName());
        Response response = courierClient.createCourier(anotherCourierData);
        response.then()
                .assertThat()
                .statusCode(SC_CONFLICT)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }
}
