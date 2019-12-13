package ru.netology.interaction;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import ru.netology.data.Card;
import ru.netology.data.DataHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiInteraction {
    private static String baseUrl = System.getProperty("app.url");
    private static int port = Integer.parseInt(System.getProperty("app.port"));
    private static String badRequestMessage = "Bad Request";
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .setPort(port)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .addFilter(new AllureRestAssured())
            .build();

    @Step("Отправка POST запроса с approved/declined card")
    public static String sentPayment(Card card, String url) {
        DataHelper.ResponseApi bodyResponse = given()
                .spec(requestSpec)
                .body(card)
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .extract()
                .body().as(DataHelper.ResponseApi.class);
        return bodyResponse.getStatus();
    }

    @Step("Отправка POST запроса с NoDb card")
    public static String sentPaymentByNoDbCard(Card card, String url) {
        return given()
                .spec(requestSpec)
                .body(card)
                .when()
                .post(url)
                .then()
                .statusCode(500)
                .extract()
                .body().asString();
    }

    @Step("Отправка POST запроса только с номером карты в body")
    public static String sentPaymentOnlyNumber(String url) {
        return given()
                .spec(requestSpec)
                .body("{\"number\" : \"4444 4444 4444 4441\"}")
                .when()
                .post(url)
                .then()
                .statusCode(400)
                .extract()
                .body().asString();
    }

    @Step("Отправка POST запроса с невалидными значениями")
    public static String sentPaymentByApprovedCardBadField(Card card, String url) {
        return given()
                .spec(requestSpec)
                .body(card)
                .when()
                .post(url)
                .then()
                .statusCode(400)
                .extract()
                .body().asString();
    }

    @Step("Проверка статуса платежа")
    public static void assertStatus(String expect, String fact) {
        assertEquals(expect, fact, "Статуст платежа не соответствует ожидаемому");
    }

    @Step("Проверка наличия ошибки в ответе на запрос")
    public static void assertBadRequest(String response) {
        assertThat(response, containsString(badRequestMessage));
    }
}
