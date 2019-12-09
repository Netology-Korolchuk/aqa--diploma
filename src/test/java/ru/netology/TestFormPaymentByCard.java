package ru.netology;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.SoftAssertsExtension;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.netology.data.Card;
import ru.netology.interaction.DbInteraction;
import ru.netology.page.PayForm;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.data.DataHelper.*;


public class TestFormPaymentByCard {
    private String serviceUrl = "http://localhost:8080/";
    private int priceTour;
    private static Card approvedCard;
    private static Card declinedCard;
    private static Card noDbCard;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        approvedCard = Card.generatedApprovedCard("ru");
        declinedCard = Card.generatedDeclinedCard("en");
        noDbCard = Card.generatedNoDbCard();
    }

    @AfterEach()
    void cleanDb() {
        DbInteraction.clearDB();
    }



    @Test
    @DisplayName("Купить тур по карте: APPROVED карта, валидные значения для формы")
    void shouldPayByApprovedCard() {
        open(serviceUrl);
        val page = new PayForm();
        priceTour = page.getPriceTour() * 100;
        page.setPayByCard();
        page.setFormFiled(approvedCard);
        page.assertGoodMessage();

        assertDbAfterPayByCard(PaymentResult.APPROVED.toString(), priceTour);
    }

    @Test
    @DisplayName("Купить тур по карте: DECLINED карта, валидные значения для формы")
    void shouldNoPayByDeclinedCard() {
        open(serviceUrl);
        val page = new PayForm();
        priceTour = page.getPriceTour() * 100;
        page.setPayByCard();
        page.setFormFiled(declinedCard);
        page.assertBadMessage();
    }
    //todo issue ex -"declined" fac -"approved"

    @Test
    @DisplayName("Повторная оплата с той же карты")
    void shouldPayByApprovedCardWithRepeat() {
        open(serviceUrl);
        val page = new PayForm();
        priceTour = page.getPriceTour() * 100;
        page.setPayByCard();
        page.setFormFiled(approvedCard);
        page.assertGoodMessage();
        page.setFormRepeatedly();
        page.assertGoodMessage();
    }

    @Test
    @DisplayName("Обновление страницы во время заполнения данных - оплата картой")
    void shouldEmptyCardFormAfterRefresh() {
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiledAndRefresh(declinedCard);
        page.setPayByCard();
        page.assertFieldsIsEmpty();
    }

    @Test
    @DisplayName("Отправка формы с картой не из базы - оплата по карте")
    void shouldNoPayByNoDbCard() {
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(noDbCard);
        page.assertBadMessage();
    }

    @ParameterizedTest
    @DisplayName("Отправка формы с невалидной картой - оплата картой")
    @CsvFileSource(resources = "/dataNumber.csv", numLinesToSkip = 1)
    void shouldValidateCardNumberField(String number, String error) {
        approvedCard.setNumber(number);
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(approvedCard);
        page.assertMessageCard(error);
    }

    @ParameterizedTest
    @DisplayName("Отправка формы с невалидным месяцем - оплата картой")
    @CsvFileSource(resources = "/dataMonth.csv", numLinesToSkip = 1)
    void shouldValidateMonthField(String month, String error) {
        declinedCard.setMonth(month);
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(declinedCard);
        page.assertMessageMonth(error);
    }

    @ParameterizedTest
    @DisplayName("Отправка формы с невалидным годом - оплата картой")
    @CsvFileSource(resources = "/dataYear.csv", numLinesToSkip = 1)
    void shouldValidateYearField(String year, String error) {
        declinedCard.setYear(year);
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(declinedCard);
        page.assertMessageYear(error);
    }

    @ParameterizedTest
    @DisplayName("Отправка формы с невалидным владельцем - оплата картой")
    @CsvFileSource(resources = "/dataHolder.csv", numLinesToSkip = 1)
    void shouldValidateHolderField(String holder, String error) {
        approvedCard.setHolder(holder);
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(approvedCard);
        page.assertMessageHolder(error);
    }

    @ParameterizedTest
    @DisplayName("Отправка формы с невалидным CVC - оплата картой")
    @CsvFileSource(resources = "/dataCvc.csv", numLinesToSkip = 1)
    void shouldValidateCvvField(String cvc, String error) {
        noDbCard.setCvc(cvc);
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(noDbCard);
        page.assertMessageCvv(error);
        page.assertNoExistHolderMessage();
    }

    @Test
    @DisplayName("Отправка формы с невалидными значениями (max) - оплата картой")
    void shouldValidateCardFormWithMaxChar() {
        val card = Card.generatedCardWithMaxChar();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);
        page.assertValueField(card);
    }

    @Test
    @DisplayName("Отправка формы с невалидно датой и пустым владельцем - оплата картой")
    void shouldValidateExpiredField() {
        approvedCard.setYear("18");
        approvedCard.setHolder("");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(approvedCard);
        page.assertMessageCvv("Истёк срок действия карты");
        page.assertNoExistHolderMessage();
    }

    public void assertDbAfterPayByCard(String paymentResult, int price) {
        val paymentFromDb = DbInteraction.getPaymentByCard();
        assertEquals(paymentResult, DbInteraction.getPaymentByCard().getStatus());
        assertEquals(price, paymentFromDb.getAmount());
        assertTrue(DbInteraction.isOrderByPaymentExist(paymentFromDb.getTransaction_id()));
    }
}
