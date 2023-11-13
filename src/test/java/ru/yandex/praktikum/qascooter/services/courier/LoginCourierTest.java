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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static ru.yandex.praktikum.qascooter.services.courier.CourierDataGenerator.*;

public class LoginCourierTest {

    private CourierClient courierClient;
    private CourierData courier;
    private Integer courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = getRandomCourierData();
        courierClient.createCourier(courier);
    }

    @After
    public void cleanUp() {
        if (!Objects.equals(courierId,null)) courierClient.deleteCourier(courierId.toString());
    }

    @Epic(value = "Courier Client")
    @Feature(value = "operations")
    @Story(value = "loginCourier")
    @Test
    @DisplayName("'Login courier' request returns 'success code' and courier 'id'")
    @Description("Check that if courier can be successfully logged in")
    public void loginRequestReturnsSuccessCodeAndCourierId() {
        Response response = courierClient.loginCourier(CourierCredentials.getCredentialsFrom(courier));
        response.then()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("id", notNullValue());
        courierId = response.then().extract().path("id");
    }

    @Epic(value = "Courier Client")
    @Feature(value = "operations")
    @Story(value = "loginCourier")
    @Test
    @DisplayName("'Login courier' request returns 'error' on wrong 'login'")
    @Description("Check that if 'login' value is wrong error 404 will be returned")
    public void loginRequestReturnsErrorOnWrongLogin() {
        courier.setLogin(generateLogin());
        Response response = courierClient.loginCourier(CourierCredentials.getCredentialsFrom(courier));
        response.then()
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Epic(value = "Courier Client")
    @Feature(value = "operations")
    @Story(value = "loginCourier")
    @Test
    @DisplayName("'Login courier' request returns 'error' on wrong 'password'")
    @Description("Check that if 'password' value is wrong error 404 will be returned")
    public void loginRequestReturnsErrorOnWrongPassword() {
        courier.setPassword(generatePassword());
        Response response = courierClient.loginCourier(CourierCredentials.getCredentialsFrom(courier));
        response.then()
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Epic(value = "Courier Client")
    @Feature(value = "operations")
    @Story(value = "loginCourier")
    @Test
    @DisplayName("'Login courier' request returns 'error' on non-existent 'courier'")
    @Description("Check that if courier is non-existent error 404 will be returned")
    public void loginRequestReturnsErrorOnNonExistentCourier() {
        courierId = courierClient
                .loginCourier(CourierCredentials.getCredentialsFrom(courier))
                .path("id");
        courierClient.deleteCourier(courierId.toString());
        Response response = courierClient
                .loginCourier(CourierCredentials.getCredentialsFrom(courier));
        response.then()
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
