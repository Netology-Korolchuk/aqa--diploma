package ru.netology.interaction;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import ru.netology.data.Card;
import ru.netology.data.DataHelper;

import static io.restassured.RestAssured.given;

public class ApiInteraction {
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(8080)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static String sentPaymentByCard(Card card) {
        DataHelper.ResponseApi bodyResponse = given()
                .spec(requestSpec)
                .body(card)
                .when()
                .post("api/v1/pay")
                .then()
                .statusCode(200)
                .extract()
                .body().as(DataHelper.ResponseApi.class);
        return bodyResponse.getStatus();
    }

    public static String sentPaymentByCredit(Card card) {
        DataHelper.ResponseApi bodyResponse = given()
                .spec(requestSpec)
                .body(card)
                .when()
                .post("api/v1/credit")
                .then()
                .statusCode(200)
                .extract()
                .body().as(DataHelper.ResponseApi.class);
        return bodyResponse.getStatus();
    }

    public static String sentPaymentByCardOnlyNumber() {
        String bodyResponse = given()
                .spec(requestSpec)
                .body("{\"number\" : \"4444 4444 4444 4441\"}")
                .when()
                .post("api/v1/pay")
                .then()
                .statusCode(400)
                .extract()
                .body().asString();
        return bodyResponse;
    }

    public static String sentRequestPaymentByCardWithoutBody() {
        String bodyResponse = given()
                .spec(requestSpec)
                .when()
                .post("api/v1/pay")
                .then()
                .statusCode(400)
                .extract()
                .body().asString();
        return bodyResponse;
    }

    public static String sentRequestPaymentByCreditWithoutBody() {
        String bodyResponse = given()
                .spec(requestSpec)
                .when()
                .post("api/v1/credit")
                .then()
                .statusCode(400)
                .extract()
                .body().asString();
        return bodyResponse;
    }

    public static String sentPaymentByBadCard(Card card) {
        String bodyResponse = given()
                .spec(requestSpec)
                .body(card)
                .when()
                .post("api/v1/pay")
                .then()
                .statusCode(500)
                .extract()
                .body().asString();
        return bodyResponse;
    }

    public static String sentPaymentByBadCredit(Card card) {
        String bodyResponse = given()
                .spec(requestSpec)
                .body(card)
                .when()
                .post("api/v1/credit")
                .then()
                .statusCode(500)
                .extract()
                .body().asString();
        return bodyResponse;
    }

    public static String sentPaymentByApprovedCardBadField(Card card) {
        String bodyResponse = given()
                .spec(requestSpec)
                .body(card)
                .when()
                .post("api/v1/pay")
                .then()
                .statusCode(400)
                .extract()
                .body().asString();
        return bodyResponse;
    }

    public static String sentPaymentByApprovedCreditBadField(Card card) {
        String bodyResponse = given()
                .spec(requestSpec)
                .body(card)
                .when()
                .post("api/v1/credit")
                .then()
                .statusCode(400)
                .extract()
                .body().asString();
        return bodyResponse;
    }

    public static String sentGetRequestPaymentByCard() {
        String bodyResponse = given()
                .spec(requestSpec)
                .when()
                .get("api/v1/pay")
                .then()
                .statusCode(405)
                .extract()
                .body().asString();
        return bodyResponse;
    }

    public static String sentGetRequestPaymentByCredit() {
        String bodyResponse = given()
                .spec(requestSpec)
                .when()
                .get("api/v1/credit")
                .then()
                .statusCode(405)
                .extract()
                .body().asString();
        return bodyResponse;
    }
}
