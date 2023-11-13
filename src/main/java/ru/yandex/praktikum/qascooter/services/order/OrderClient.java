package ru.yandex.praktikum.qascooter.services.order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import ru.yandex.praktikum.qascooter.services.constants.RestClient;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.qascooter.services.constants.EndPoints.*;

public class OrderClient extends RestClient {

    @Step("GET Orders ( courierId=\"{courierId}\", nearestStation=\"{nearestStation}\", limit=\"{limit}\", page=\"{page}\" )")
    public ValidatableResponse getOrders(Integer courierId, String nearestStation, Integer limit, Integer page) {
        RequestSpecification spec = given().spec(getRequestSpecification());
        spec = Objects.equals(courierId, null ) ? spec : spec.queryParam("courierId", courierId);
        spec = Objects.equals(nearestStation, null ) ? spec : spec.queryParam("nearestStation", nearestStation);
        spec = Objects.equals(limit, null ) ? spec : spec.queryParam("limit", limit);
        spec = Objects.equals(page, null ) ? spec : spec.queryParam("page", page);

        return spec
                .when()
                .get(ORDERS_URL)
                .then();
    }

    @Step("POST Orders create {orderData}")
    public ValidatableResponse createOrder(OrderData orderData) {
        return given()
                .spec(getRequestSpecification())
                .body(orderData)
                .when()
                .post(ORDERS_URL)
                .then();
    }

    @Step("GET Orders track ( t=\"{track}\" )")
    public ValidatableResponse trackOrder(int track) {
        return given()
                .spec(getRequestSpecification())
                .queryParam("t", track == 0 ? "" : track)
                .when()
                .get(TRACK_ORDER_URL)
                .then();
    }

    @Step("PUT Orders cancel ( track=\"{track}\" )")
    public ValidatableResponse cancelOrder(int track) {
        return given()
                .spec(getRequestSpecification())
                .queryParam("track", track == 0 ? "" : track)
                .when()
                .put(CANCEL_ORDER_URL)
                .then();
    }

    @Step("PUT Orders accept ( id=\"{orderId}\", {courierId=\"{courierId}\" )")
    public ValidatableResponse acceptOrder(Integer orderId, Integer courierId) {
        return given()
                .spec(getRequestSpecification())
                .when()
                .put(ACCEPT_ORDER_URL,
                        Objects.equals(orderId, null) ? "" : orderId,
                        Objects.equals(courierId, null) ? "" : courierId)
                .then();
    }
}
