package ru.yandex.praktikum.qascooter.services.courier;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Objects;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_MULTIPLE_CHOICES;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static ru.yandex.praktikum.qascooter.services.courier.CourierCredentials.getCredentialsFrom;
import static ru.yandex.praktikum.qascooter.services.courier.CourierDataGenerator.*;

@RunWith(Parameterized.class)
public class CreateCourierParametersTest {

    private CourierClient courierClient;
    private CourierData courierData;
    private String courierId;

    private final String LOGIN;
    private final String PASSWORD;
    private final String FIRST_NAME;
    private final int EXPECTED_CODE;
    private final String EXPECTED_MESSAGE;

    public CreateCourierParametersTest(String login, String password, String firstName,
                                       int expectedCode, String expectedMessage) {
        this.LOGIN = login;
        this.PASSWORD = password;
        this.FIRST_NAME = firstName;
        this.EXPECTED_CODE = expectedCode;
        this.EXPECTED_MESSAGE = expectedMessage;
    }

    @Parameterized.Parameters(name="create('login': {0}, 'password': {1}, 'firstName': {2}) returns code {3}")
    public static Object[][] getTestData() {
        return new Object[][]{
                { null, null, null, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { null, null, "", SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { null, null, generateFirstName(), SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },

                { null, "", null, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { null, "", "", SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { null, "", generateFirstName(), SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },

                { null, generatePassword(), null, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { null, generatePassword(), "", SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { null, generatePassword(), generateFirstName(), SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },

                { "", null, null, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { "", null, "", SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { "", null, generateFirstName(), SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },

                { "", "", null, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { "", "", "", SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { "", "", generateFirstName(), SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },

                { "", generatePassword(), null, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { "", generatePassword(), "", SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { "", generatePassword(), generateFirstName(), SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },

                { generateLogin(), null, null, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { generateLogin(), null, "",  SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { generateLogin(), null, generateFirstName(), SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },

                { generateLogin(), "", null, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { generateLogin(), "", "", SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },
                { generateLogin(), "", generateFirstName(), SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи" },

                { generateLogin(), generatePassword(), null, SC_CREATED, null },
                { generateLogin(), generatePassword(), "", SC_CREATED, null },
                { generateLogin(), generatePassword(), generateFirstName(), SC_CREATED, null },
        };
    }

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courierData = new CourierData(LOGIN, PASSWORD, FIRST_NAME);
    }

    @After
    public void cleanUp() {
        if (!Objects.equals(courierId,null)) courierClient.deleteCourier(courierId);
    }

    @Epic(value = "Courier Client")
    @Feature(value = "validation")
    @Story(value = "parameters")
    @Test
    @DisplayName("'Create courier' request returns 'status code' depending on parameters")
    @Description("Check that create request returns 'success' for valid required parameters only")
    public void createRequestReturnsStatusCodeDependingOnParameters() {
        Response response = courierClient.createCourier(courierData);
        response.then()
                .assertThat()
                .statusCode(EXPECTED_CODE)
                .and()
                .body("message", equalTo(EXPECTED_MESSAGE));

        int statusCode = response.statusCode();
        if (statusCode >= SC_OK && statusCode < SC_MULTIPLE_CHOICES) {
            courierId = courierClient
                    .loginCourier(getCredentialsFrom(courierData))
                    .path("id")
                    .toString();
        }
    }
}
