package ru.netology;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.netology.data.Card;
import ru.netology.interaction.ApiInteraction;
import ru.netology.interaction.DbInteraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.netology.data.DataHelper.*;

@Feature("Тестирование API")
public class TestApi {
    private Card approvedCard;
    private Card declinedCard;
    private Card noDbCard;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Step("Очистка БД после теста")
    @AfterEach
    void cleanDb() {
        DbInteraction.clearDB();
    }


    @Story("Проверка обработки approved card")
    @Severity(SeverityLevel.BLOCKER)
    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Отправка валидной карты")
    void shouldPositiveResponsePay(String url) {
        approvedCard = Card.generatedApprovedCard("en");
        String response = ApiInteraction.sentPayment(approvedCard, url);
        ApiInteraction.assertStatus(PaymentResult.APPROVED.toString(), response);

        selectDbAssertAfterValidate(url, PaymentResult.APPROVED.toString());
    }

    @Story("Проверка обработки declined card")
    @Severity(SeverityLevel.BLOCKER)
    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Отправка declined карты")
    void shouldNegativeResponsePay(String url) {
        declinedCard = Card.generatedDeclinedCard("ru");
        String response = ApiInteraction.sentPayment(declinedCard, url);
        ApiInteraction.assertStatus(PaymentResult.DECLINED.toString(), response);

        selectDbAssertAfterValidate(url, PaymentResult.DECLINED.toString());
    }

    @Story("Проверка обработки NoDb card")
    @Severity(SeverityLevel.CRITICAL)
    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Отправка карты не из БД")
    void shouldValidateCard(String url) {
        noDbCard = Card.generatedNoDbCard();
        String response = ApiInteraction.sentPaymentByNoDbCard(noDbCard, url);
        ApiInteraction.assertBadRequest(response);
    }

    @Story("Проверка обработки невалидного body")
    @Severity(SeverityLevel.NORMAL)
    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Валидная карта, пустой месяц")
    void shouldValidateEmptyKeyCard(String url) {
        approvedCard = Card.generatedApprovedCard("ru");
        approvedCard.setMonth("");
        String response = ApiInteraction.sentPaymentByApprovedCardBadField(approvedCard, url);
        ApiInteraction.assertBadRequest(response);
    }
    //todo issue ex-"Bad request" fac-approved

    @Story("Проверка обработки невалидного body")
    @Severity(SeverityLevel.NORMAL)
    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Валидная карта, невалидные год")
    void shouldValidateBadFormatKeyCard(String url) {
        approvedCard = Card.generatedApprovedCard("ru");
        approvedCard.setYear("%^");
        String response = ApiInteraction.sentPaymentByApprovedCardBadField(approvedCard, url);
        ApiInteraction.assertBadRequest(response);
    }

    @Story("Проверка обработки невалидного body")
    @Severity(SeverityLevel.NORMAL)
    @ParameterizedTest
    @CsvSource({"api/v1/pay", "api/v1/credit"})
    @DisplayName("Отправка только номера карты")
    void shouldValidateBody(String url) {
        String response = ApiInteraction.sentPaymentOnlyNumber(url);
        ApiInteraction.assertBadRequest(response);
    }
    //todo issue ex-"Bad request" fac-approved

    @Step("Выбор проверки БД для approved/declined card")
    public void selectDbAssertAfterValidate(String url, String paymentResult) {
        if (url.equals("api/v1/pay")) {
            assertDbAfterPayByCard(paymentResult);
        } else assertDbAfterPayByCredit(paymentResult);
    }

    @Step("Проверки БД для approved/declined card в таблице Payment")
    public void assertDbAfterPayByCard(String paymentResult) {
        val paymentFromDb = DbInteraction.getPaymentByCard();
        assertNotNull(paymentFromDb, "Транзакция не найдена");
        assertEquals(paymentResult, paymentFromDb.getStatus(), "Статус платежа в БД не соответсвует ожидаемому результату");
    }

    @Step("Проверки БД для approved/declined card в таблице Credit")
    public void assertDbAfterPayByCredit(String paymentResult) {
        String status = DbInteraction.getPaymentByCredit();
        assertNotNull(status, "Транзакция не найдена");
        assertEquals(paymentResult, status, "Статус платежа в БД не соответсвует ожидаемому результату");
    }
}

