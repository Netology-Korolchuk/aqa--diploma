package ru.netology;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.Card;
import ru.netology.interaction.ApiInteraction;
import ru.netology.interaction.DbInteraction;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;

public class TestApi {

    @AfterAll
    static void cleanDb() {
        DbInteraction.clearDB();
    }

    @Test
    @DisplayName("Отправка валидной карты - оплата по карте")
    void shouldPositiveResponsePay() {
        val card = Card.generatedApprovedCard("ru");
        String response = ApiInteraction.sentPaymentByCard(card);
        assertEquals(PaymentResult.APPROVED.toString(), response);
    }

    @Test
    @DisplayName("Отправка валидной карты - оплата в кредит")
    void shouldPositiveResponseCredit() {
        val card = Card.generatedApprovedCard("en");
        String response = ApiInteraction.sentPaymentByCredit(card);
        assertEquals(PaymentResult.APPROVED.toString(), response);
    }

    @Test
    @DisplayName("Отправка declined карты - оплата по карте")
    void shouldNegativeResponsePay() {
        val card = Card.generatedDeclinedCard("en");
        String response = ApiInteraction.sentPaymentByCard(card);
        assertEquals(PaymentResult.DECLINED.toString(), response);
    }

    @Test
    @DisplayName("Отправка declined карты - оплата в кредит")
    void shouldNegativeResponseCredit() {
        val card = Card.generatedDeclinedCard("ru");
        String response = ApiInteraction.sentPaymentByCredit(card);
        assertEquals(PaymentResult.DECLINED.toString(), response);
    }

    @Test
    @DisplayName("Отправка карты не из БД - оплата по карте")
    void shouldValidateCard() {
        val card = Card.generatedNoDbCard();
        String response = ApiInteraction.sentPaymentByBadCard(card);
        assertThat(response, containsString("400 Bad Request"));
    }

    @Test
    @DisplayName("Отправка карты не из БД - оплата в кредит")
    void shouldValidateCredit() {
        val card = Card.generatedNoDbCard();
        String response = ApiInteraction.sentPaymentByBadCredit(card);
        assertThat(response, containsString("400 Bad Request"));
    }

    @Test
    @DisplayName("Get запрос - отплата по карте")
    void shouldValidateGetRequestCard() {
        String response = ApiInteraction.sentGetRequestPaymentByCard();
        assertThat(response, containsString("Method Not Allowed"));
    }

    @Test
    @DisplayName("Get запрос - отплата в кредит")
    void shouldValidateGetRequestCredit() {
        String response = ApiInteraction.sentGetRequestPaymentByCredit();
        assertThat(response, containsString("Method Not Allowed"));
    }

    @Test
    @DisplayName("Отправка запроса с пустым body - оплата по карте")
    void shouldValidateEmptyBodyCard() {
        String response = ApiInteraction.sentRequestPaymentByCardWithoutBody();
        assertThat(response, containsString("Bad Request"));
    }

    @Test
    @DisplayName("Отправка запроса с пустым body - оплата в кредит")
    void shouldValidateEmptyBodyCredit() {
        String response = ApiInteraction.sentRequestPaymentByCreditWithoutBody();
        assertThat(response, containsString("Bad Request"));
    }

    @Test
    @DisplayName("Валидная карта, пустые дата и год - оплата по карте")
    void shouldValidateEmptyKeyCard() {
        val card = Card.generatedApprovedCardWithEmptyMonthYear();
        String response = ApiInteraction.sentPaymentByApprovedCreditBadField(card);
        assertThat(response, containsString("Bad Request"));
    }
    //todo issue ex-"Bad request" fac-approved

    @Test
    @DisplayName("Валидная карта, пустые дата и год - оплата в кредит")
    void shouldValidateEmptyKeyCredit() {
        val card = Card.generatedApprovedCardWithEmptyMonthYear();
        String response = ApiInteraction.sentPaymentByApprovedCreditBadField(card);
        assertThat(response, containsString("Bad Request"));
    }
    //todo issue ex-"Bad request" fac-approved

    @Test
    @DisplayName("Валидная карта, невалидные дата и месяц - оплата по карте")
    void shouldValidateBadFormatKeyCard() {
        val card = Card.generatedApprovedCardWithMixedMonthYear("en");
        String response = ApiInteraction.sentPaymentByApprovedCardBadField(card);
        assertThat(response, containsString("Bad Request"));
    }

    @Test
    @DisplayName("Валидная карта, невалидные дата и месяц - оплата в кредит")
    void shouldValidateBadFormatKeyCredit() {
        val card = Card.generatedApprovedCardWithMixedMonthYear("ru");
        String response = ApiInteraction.sentPaymentByApprovedCreditBadField(card);
        assertThat(response, containsString("Bad Request"));
    }

    @Test
    @DisplayName("Отправка только номера карты - отплата по карте")
    void shouldValidateBody() {
        String response = ApiInteraction.sentPaymentByCardOnlyNumber();
        assertThat(response, containsString("Bad Request"));
    }
    //todo issue ex-"Bad request" fac-approved
}

