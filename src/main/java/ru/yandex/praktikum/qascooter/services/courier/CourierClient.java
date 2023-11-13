package ru.yandex.praktikum.qascooter.services.courier;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.qascooter.services.constants.RestClient;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.qascooter.services.constants.EndPoints.*;

public class CourierClient extends RestClient {

    @Step("POST CourierData {courier}")
    public Response createCourier(CourierData courier) {
        return given()
                .spec(getRequestSpecification())
                .body(courier)
                .when()
                .post(COURIERS_URL);
    }

    @Step("POST CourierData login {courierCredentials}")
    public Response loginCourier(CourierCredentials courierCredentials) {
        return given()
                .spec(getRequestSpecification())
                .body(courierCredentials)
                .when()
                .post(AUTHORIZE_COURIER_URL);
    }

    @Step("DELETE CourierData ( id=\"{id}\" )")
    public Response deleteCourier(String id) {
        return given()
                .spec(getRequestSpecification())
                .when()
                .delete(DELETE_COURIER_URL,  id);
    }
}
