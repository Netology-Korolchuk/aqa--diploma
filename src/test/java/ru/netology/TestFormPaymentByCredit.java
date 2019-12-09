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
import ru.netology.data.DataHelper;
import ru.netology.interaction.DbInteraction;
import ru.netology.page.PayForm;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestFormPaymentByCredit {
    private String serviceUrl = "http://localhost:8080/";
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

    @AfterEach()
    void cleanDb() {
        DbInteraction.clearDB();
    }


    @Test
    @DisplayName("Купить тур в кредит: APPROVED карта, валидные значения для формы")
    void shouldPayByApprovedCredit() {
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(approvedCard);
        page.assertGoodMessage();

        assertDbAfterPayByCredit(DataHelper.PaymentResult.APPROVED.toString());
    }

    @Test
    @DisplayName("Купить тур в кредит: DECLINED карта, валидные значения для формы")
    void shouldNoPayByDeclinedCredit() {
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(declinedCard);
        page.assertBadMessage();
    }
    //todo issue ex -"declined" fac -"approved"

    @Test
    @DisplayName("Повторная оплата с той же карты")
    void shouldPayByApprovedCardWithRepeat() {
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(approvedCard);
        page.assertGoodMessage();
        page.setFormRepeatedly();
        page.assertGoodMessage();
    }

    @Test
    @DisplayName("Обновление страницы во время заполнения данных - оплата в кредит")
    void shouldEmptyCardFormAfterRefresh() {
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiledAndRefresh(declinedCard);
        page.setPayByCard();
        page.assertFieldsIsEmpty();
    }

    @Test
    @DisplayName("Отправка формы с картой не из базы - оплата в кредит")
    void shouldNoPayByNoDbCredit() {
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(noDbCard);
        page.assertBadMessage();
    }

    @ParameterizedTest
    @DisplayName("Отправка формы с невалидной картой - оплата в кредит")
    @CsvFileSource(resources = "/dataNumber.csv", numLinesToSkip = 1)
    void shouldValidateCardNumberField(String number, String error) {
        approvedCard.setNumber(number);
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
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
        page.setPayByCredit();
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
        page.setPayByCredit();
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
        page.setPayByCredit();
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
        page.setPayByCredit();
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
        page.setPayByCredit();
        page.setFormFiled(card);
        page.assertValueField(card);
    }

    @Test
    @DisplayName("Отправка формы с невалидно датой и пустым владельцем - оплата картой")
    void shouldValidateExpiredField() {
        approvedCard.setMonth("13");
        approvedCard.setHolder("");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(approvedCard);
        page.assertMessageCvv("Неверно указан срок действия карты");
        page.assertNoExistHolderMessage();
    }

    public void assertDbAfterPayByCredit(String paymentResult) {
        val paymentFromDb = DbInteraction.getPaymentByCredit();
        assertEquals(paymentResult, DbInteraction.getPaymentByCredit().getStatus());
        assertTrue(DbInteraction.isOrderByCreditExist(paymentFromDb.getBank_id()));
    }
}
