package ru.netology;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.netology.data.Card;
import ru.netology.interaction.DbInteraction;
import ru.netology.page.PayForm;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.data.DataHelper.*;

@Feature("Тестирование оплаты тура по карте")
public class TestFormPaymentByCard {
    private String serviceUrl = "http://localhost:8080/";
    private int priceTour;
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
    @AfterEach()
    void cleanDb() {
        DbInteraction.clearDB();
    }

    @Story("Проверка обработки approved card")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    @DisplayName("Купить тур по карте: APPROVED карта, валидные значения для формы")
    void shouldPayByApprovedCard() {
        approvedCard = Card.generatedApprovedCard("ru");
        open(serviceUrl);
        val page = new PayForm();
        priceTour = page.getPriceTour() * 100;
        page.setPayByCard();
        page.setFormFiled(approvedCard);
        page.assertGoodMessage();

        assertDbAfterPayByCard(PaymentResult.APPROVED.toString(), priceTour);
    }

    @Story("Проверка обработки declined card")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    @DisplayName("Купить тур по карте: DECLINED карта, валидные значения для формы")
    void shouldNoPayByDeclinedCard() {
        declinedCard = Card.generatedDeclinedCard("en");
        open(serviceUrl);
        val page = new PayForm();
        priceTour = page.getPriceTour() * 100;
        page.setPayByCard();
        page.setFormFiled(declinedCard);
        page.assertBadMessage();
    }
    //todo issue ex -"declined" fac -"approved"

    @Story("Проверка обработки NoDb card")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    @DisplayName("Отправка формы с картой не из базы - оплата по карте")
    void shouldNoPayByNoDbCard() {
        noDbCard = Card.generatedNoDbCard();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(noDbCard);
        page.assertBadMessage();
    }

    @Story("Проверка валидации поля - номер карты")
    @Severity(SeverityLevel.NORMAL)
    @ParameterizedTest
    @DisplayName("Отправка формы с невалидной картой - оплата картой")
    @CsvFileSource(resources = "/dataNumber.csv", numLinesToSkip = 1)
    void shouldValidateCardNumberField(String number, String error) {
        approvedCard = Card.generatedApprovedCard("en");
        approvedCard.setNumber(number);
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(approvedCard);
        page.assertMessageCard(error);
    }

    @Story("Проверка валидации поля - месяц")
    @Severity(SeverityLevel.NORMAL)
    @ParameterizedTest
    @DisplayName("Отправка формы с невалидным месяцем - оплата картой")
    @CsvFileSource(resources = "/dataMonth.csv", numLinesToSkip = 1)
    void shouldValidateMonthField(String month, String error) {
        declinedCard = Card.generatedDeclinedCard("ru");
        declinedCard.setMonth(month);
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(declinedCard);
        page.assertMessageMonth(error);
    }

    @Story("Проверка валидации поля - год")
    @Severity(SeverityLevel.NORMAL)
    @ParameterizedTest
    @DisplayName("Отправка формы с невалидным годом - оплата картой")
    @CsvFileSource(resources = "/dataYear.csv", numLinesToSkip = 1)
    void shouldValidateYearField(String year, String error) {
        declinedCard = Card.generatedDeclinedCard("ru");
        declinedCard.setYear(year);
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(declinedCard);
        page.assertMessageYear(error);
    }

    @Story("Проверка валидации поля - владелец")
    @Severity(SeverityLevel.NORMAL)
    @ParameterizedTest
    @DisplayName("Отправка формы с невалидным владельцем - оплата картой")
    @CsvFileSource(resources = "/dataHolder.csv", numLinesToSkip = 1)
    void shouldValidateHolderField(String holder, String error) {
        approvedCard = Card.generatedApprovedCard("en");
        approvedCard.setHolder(holder);
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(approvedCard);
        page.assertMessageHolder(error);
    }

    @Story("Проверка валидации поля - cvv")
    @Severity(SeverityLevel.NORMAL)
    @ParameterizedTest
    @DisplayName("Отправка формы с невалидным CVC - оплата картой")
    @CsvFileSource(resources = "/dataCvc.csv", numLinesToSkip = 1)
    void shouldValidateCvvField(String cvc, String error) {
        noDbCard = Card.generatedNoDbCard();
        noDbCard.setCvc(cvc);
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(noDbCard);
        page.assertMessageCvv(error);
        page.assertNoExistHolderMessage();
    }

    @Step("Проверка транзакции из БД")
    public void assertDbAfterPayByCard(String paymentResult, int price) {
        val paymentFromDb = DbInteraction.getPaymentByCard();
        assertNotNull(paymentFromDb, "Транзакция не найдена");
        assertEquals(paymentResult, paymentFromDb.getStatus(), "Статус платежа в БД не соответсвует ожидаемому результату");
        assertEquals(price, paymentFromDb.getAmount(), "Цена в БД не соответствует цене тура");
    }
}
