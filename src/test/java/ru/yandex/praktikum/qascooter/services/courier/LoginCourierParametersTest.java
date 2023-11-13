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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Objects;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_GATEWAY_TIMEOUT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_MULTIPLE_CHOICES;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

import static org.hamcrest.Matchers.equalTo;
import static ru.yandex.praktikum.qascooter.services.courier.CourierCredentials.getCredentialsFrom;
import static ru.yandex.praktikum.qascooter.services.courier.CourierDataGenerator.*;
import static ru.yandex.praktikum.qascooter.services.courier.CourierDataGenerator.generateFirstName;

@RunWith(Parameterized.class)
public class LoginCourierParametersTest {

    private CourierClient courierClient;
    private CourierData courierData;
    private Integer courierId;

    private final String LOGIN;
    private final String PASSWORD;
    private final int EXPECTED_CODE;
    private final String EXPECTED_MESSAGE;

    public LoginCourierParametersTest(String login, String password,
                                      int expectedCode, String expectedMessage) {
        this.LOGIN = login;
        this.PASSWORD = password;
        this.EXPECTED_CODE = expectedCode;
        this.EXPECTED_MESSAGE = expectedMessage;
    }

    @Parameterized.Parameters(name="login('login': {0}, 'password': {1}) returns code {2}")
    public static Object[][] getTestData() {
        return new Object[][]{
                { null, null, SC_GATEWAY_TIMEOUT, null },
                { null, "", SC_BAD_REQUEST, "Недостаточно данных для входа" },
                { null, generatePassword(), SC_BAD_REQUEST, "Недостаточно данных для входа" },

                { "", null, SC_GATEWAY_TIMEOUT, "Недостаточно данных для входа" },
                { "", "", SC_BAD_REQUEST, "Недостаточно данных для входа" },
                { "", generatePassword(), SC_BAD_REQUEST, "Недостаточно данных для входа" },

                { generateLogin(), null, SC_GATEWAY_TIMEOUT, "Недостаточно данных для входа" },
                { generateLogin(), "", SC_BAD_REQUEST, "Недостаточно данных для входа" },
                { generateLogin(), generatePassword(), SC_NOT_FOUND, "Учетная запись не найдена" }
        };
    }

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courierData = new CourierData(LOGIN, PASSWORD, generateFirstName());
    }

    @After
    public void cleanUp() {
        if (!Objects.equals(courierId,null)) courierClient.deleteCourier(courierId.toString());
    }

    @Epic(value = "Courier Client")
    @Feature(value = "validation")
    @Story(value = "parameters")
    @Test
    @DisplayName("'Login request' returns 'error' depending on parameters")
    @Description("Check that login request returns error correspondent to query parameters")
    public void loginRequestReturnsErrorDependingOnParameters() {
        Response response = courierClient.loginCourier(getCredentialsFrom(courierData));

        int statusCode = response.statusCode();
        if (statusCode >= SC_OK && statusCode < SC_MULTIPLE_CHOICES) {
            courierId = courierClient
                    .loginCourier(getCredentialsFrom(courierData))
                    .path("id");

        response.then()
                .assertThat()
                .statusCode(EXPECTED_CODE)
                .and()
                .body("message", equalTo(EXPECTED_MESSAGE));
        }
    }
}
