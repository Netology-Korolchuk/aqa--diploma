package ru.netology;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.SoftAssertsExtension;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.netology.data.Card;
import ru.netology.interaction.ApiInteraction;
import ru.netology.interaction.DbInteraction;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.data.DataHelper.*;

public class TestApi {
    private static Card approvedCard;
    private static Card declinedCard;
    private static Card noDbCard;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        approvedCard = Card.generatedApprovedCard("en");
        declinedCard = Card.generatedDeclinedCard("ru");
        noDbCard = Card.generatedNoDbCard();
    }

    @AfterEach
    void cleanDb() {
        DbInteraction.clearDB();
    }

    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Отправка валидной карты")
    void shouldPositiveResponsePay(String url) {
        String response = ApiInteraction.sentPayment(approvedCard, url);
        ApiInteraction.assertStatus(PaymentResult.APPROVED.toString(), response);

        selectDbAssertAfterValidate(url, PaymentResult.APPROVED.toString());
    }

    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Отправка declined карты")
    void shouldNegativeResponsePay(String url) {
        String response = ApiInteraction.sentPayment(declinedCard, url);
        ApiInteraction.assertStatus(PaymentResult.DECLINED.toString(), response);

        selectDbAssertAfterValidate(url, PaymentResult.DECLINED.toString());
    }


    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Отправка карты не из БД")
    void shouldValidateCard(String url) {
        String response = ApiInteraction.sentPaymentByNoDbCard(noDbCard, url);
        ApiInteraction.assertBadRequest(response);

        selectDbAssertAfterNoValidate(url);
    }

    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Валидная карта, пустой месяц - оплата по карте")
    void shouldValidateEmptyKeyCard(String url) {
        approvedCard.setMonth("");
        String response = ApiInteraction.sentPaymentByApprovedCardBadField(approvedCard, url);
        ApiInteraction.assertBadRequest(response);

        selectDbAssertAfterNoValidate(url);
    }
    //todo issue ex-"Bad request" fac-approved

    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Валидная карта, невалидные год - оплата по карте")
    void shouldValidateBadFormatKeyCard(String url) {
        approvedCard.setYear("%^");
        String response = ApiInteraction.sentPaymentByApprovedCardBadField(approvedCard, url);
        ApiInteraction.assertBadRequest(response);

        selectDbAssertAfterNoValidate(url);
    }

    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Отправка только номера карты")
    void shouldValidateBody(String url) {
        String response = ApiInteraction.sentPaymentOnlyNumber(url);
        ApiInteraction.assertBadRequest(response);

        selectDbAssertAfterNoValidate(url);
    }
    //todo issue ex-"Bad request" fac-approved

    public void selectDbAssertAfterValidate(String url, String paymentResult) {
        if (url.equals("api/v1/pay")) {
            assertDbAfterPayByCard(paymentResult);
        } else assertDbAfterPayByCredit(paymentResult);
    }

    public void selectDbAssertAfterNoValidate(String url) {
        if (url.equals("api/v1/pay")) {
            assertDbAfterNoValidateCard();
        }
        assertDbAfterNoValidateCredit();
    }

    public void assertDbAfterPayByCard(String paymentResult) {
        val paymentFromDb = DbInteraction.getPaymentByCard();
        assertEquals(paymentResult, DbInteraction.getPaymentByCard().getStatus());
        assertTrue(DbInteraction.isOrderByPaymentExist(paymentFromDb.getTransaction_id()));
    }

    public void assertDbAfterNoValidateCard() {
        assertEquals(0, DbInteraction.getCountRowCard());
        assertEquals(0, DbInteraction.getCountRowOrder());
    }


    public void assertDbAfterPayByCredit(String paymentResult) {
        val paymentFromDb = DbInteraction.getPaymentByCredit();
        assertEquals(paymentResult, DbInteraction.getPaymentByCredit().getStatus());
        assertTrue(DbInteraction.isOrderByCreditExist(paymentFromDb.getBank_id()));
    }

    public void assertDbAfterNoValidateCredit() {
        assertEquals(0, DbInteraction.getCountRowCredit());
        assertEquals(0, DbInteraction.getCountRowOrder());
    }
}

