package ru.yandex.praktikum.qascooter.services.constants;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;

import static ru.yandex.praktikum.qascooter.services.constants.EndPoints.BASE_URL;

public class RestClient {

    protected io.restassured.specification.RequestSpecification getRequestSpecification() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URL)
                .build();
    }
}
