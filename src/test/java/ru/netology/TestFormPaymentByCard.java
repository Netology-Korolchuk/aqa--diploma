package ru.netology;

import lombok.val;
import org.junit.jupiter.api.*;
import ru.netology.data.Card;
import ru.netology.interaction.DbInteraction;
import ru.netology.page.PayForm;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.data.DataHelper.*;

public class TestFormPaymentByCard {
    private String serviceUrl = "http://localhost:8080/";
    private Long cardTableRow;
    private Long orderTableRow;
    private int priceTour;

    @AfterAll
    static void cleanDb() {
        DbInteraction.clearDB();
    }

    @BeforeEach
    void getRowInDb() {
        cardTableRow = DbInteraction.getCountRowCard();
        orderTableRow = DbInteraction.getCountRowOrder();
    }

    @Test
    @DisplayName("Купить тур по карте: APPROVED карта, валидные значения для формы")
    void shouldPayByApprovedCard() {
        val card = Card.generatedApprovedCard("ru");
        open(serviceUrl);
        val page = new PayForm();
        priceTour = page.getPriceTour() * 100;
        page.setPayByCard();
        page.setFormFiled(card);
        page.assertGoodMessage();

        assertDbAfterPayByCard(cardTableRow, PaymentResult.APPROVED.toString(), priceTour, orderTableRow);
    }

    @Test
    @DisplayName("Купить тур по карте: APPROVED карта, валидные значения для формы, срок действия - текущий месяц и год")
    void shouldPayByApprovedCardNowExpired() {
        val card = Card.generatedApprovedCardWithNowDate("en");
        open(serviceUrl);
        val page = new PayForm();
        priceTour = page.getPriceTour() * 100;
        page.setPayByCard();
        page.setFormFiled(card);
        page.assertGoodMessage();

        assertDbAfterPayByCard(cardTableRow, PaymentResult.APPROVED.toString(), priceTour, orderTableRow);
    }

    @Test
    @DisplayName("Купить тур по карте: DECLINED карта, валидные значения для формы")
    void shouldNoPayByDeclinedCard() {
        val card = Card.generatedDeclinedCard("en");
        open(serviceUrl);
        val page = new PayForm();
        priceTour = page.getPriceTour() * 100;
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterPayByCard(cardTableRow, PaymentResult.DECLINED.toString(), priceTour, orderTableRow);

        page.assertBadMessage();
    }
    //todo issue ex -"declined" fac -"approved"

    @Test
    @DisplayName("Повторная оплата с той же карты")
    void shouldPayByApprovedCardWithRepeat() {
        val card = Card.generatedApprovedCard("en");
        open(serviceUrl);
        val page = new PayForm();
        priceTour = page.getPriceTour() * 100;
        page.setPayByCard();
        page.setFormFiled(card);
        page.assertGoodMessage();

        Long[] newCounts = assertDbAfterPayByCard(cardTableRow, PaymentResult.APPROVED.toString(), priceTour, orderTableRow);

        page.setFormRepeatedly();
        page.assertGoodMessage();

        assertDbAfterPayByCard(newCounts[0], PaymentResult.APPROVED.toString(), priceTour, newCounts[1]);
    }

    @Test
    @DisplayName("Обновление страницы во время заполнения данных - оплата картой")
    void shouldEmptyCardFormAfterRefresh() {
        val card = Card.generatedApprovedCard("ru");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiledAndRefresh(card);
        page.setPayByCard();
        page.assertFieldsIsEmpty();
    }

    @Test
    @DisplayName("Отправка формы с картой не из базы - оплата по карте")
    void shouldNoPayByNoDbCard() {
        val card = Card.generatedNoDbCard();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);
        page.assertBadMessage();

        assertDbAfterNoValidateCard();
    }

    @Test
    @DisplayName("Отправка формы с невалидными значениями (min) - оплата картой")
    void shouldValidateCardFormWithMinChar() {
        val card =Card.generatedCardWithMinChar("en");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCard();

        page.assertBadFormatMessageCard();
        page.assertBadFormatMessageMonth();
        page.assertBadFormatMessageYear();
        page.assertNoExistHolderMessage();
        page.assertBadFormatMessageCvv();
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

        assertDbAfterNoValidateCard();
    }

    @Test
    @DisplayName("Отправка формы с невалидными значениями (буквы вместо цифр/цифры вместо букв) - оплата картой")
    void shouldValidateCardFromWithNoValidateChar() {
        val card = Card.generatedCardWithLetters("ru");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCard();

        page.assertBadFormatMessageCard();
        page.assertBadFormatMessageMonth();
        page.assertBadFormatMessageYear();
        page.assertBadFormatMessageHolder();
        page.assertBadFormatMessageCvv();
    }
    //todo issue ex- holder "Неверный формат" fac-"Поле обязательно для заполнения"

    @Test
    @DisplayName("Отправка формы с невалидными значениями (спецсимволы) - оплата картой")
    void shouldValidateCardFormWithSpecialChar() {
        val card = Card.generatedCardWithSpecialChar();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCard();

        page.assertBadFormatMessageCard();
        page.assertBadFormatMessageMonth();
        page.assertBadFormatMessageYear();
        page.assertBadFormatMessageHolder();
        page.assertBadFormatMessageCvv();
    }
    //todo issue ex- holder "Неверный формат" fac-"Поле обязательно для заполнения"

    @Test
    @DisplayName("Отправка формы: пустая карта, невалидные месяц, владелец, cvv - оплата картой")
    void shouldValidateFieldCardWithEmptyCardNoValidateOtherField() {
        val card = Card.generatedCardWithEmptyCardBadMonthHolderCvv();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCard();

        page.assertEmptyMessageCard();
        page.assertMessageForMonth();
        page.assertNoExistYearMessage();
        page.assertBadFormatMessageHolder();
        page.assertBadFormatMessageCvv();
    }
    //todo issue ex-card "Поле обязательно для заполнения", holder "Неверный формат" fac - card Неверный формат, holder no message

    @Test
    @DisplayName("Отправка формы: невалидная карта, истекший год, невалидный cvv - оплата картой")
    void shouldValidateFieldCardWithNoValidateCardCvvExpiredYear() {
        val card = Card.generatedCardWithMixedCardCvvExpiredYear("en");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCard();

        page.assertBadFormatMessageCard();
        page.assertNoExistMonthMessage();
        page.assertMessageForExpiredYear();
        page.assertNoExistHolderMessage();
        page.assertBadFormatMessageCvv();
    }
    //todo issue ex-year "Истёк срок действия карты" fac-year no message holder "Поле обязательно для заполнения"

    @Test
    @DisplayName("Отправка формы: невалидная карта, истекший месяц, пустой cvv - оплата картой")
    void shouldValidateFieldCardWithNoValidateCardExpiredMonthEmptyCvv() {
        val card = Card.generatedCardWithBadCardExpiredMonthEmptyCvv("ru");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCard();

        page.assertBadFormatMessageCard();
        page.assertMessageForMonth();
        page.assertNoExistYearMessage();
        page.assertNoExistHolderMessage();
        page.assertEmptyMessageCvv();
    }
    //todo issue ex- month "Неверно указан срок действия карты", cvv "Поле обязательно для заполнения" fac-month no message, holder "Поле обязательно для заполнения"

    @Test
    @DisplayName("Отправка формы: год в будущем(+6), пустой владелец - оплата картой")
    void shouldValidateFieldCardWithBigFutureYearEmptyHolder() {
        val card = Card.generatedNoDbCardWithBigFutureYearEmptyHolder();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCard();

        page.assertNoExistCardMessage();
        page.assertNoExistMonthMessage();
        page.assertMessageForYear();
        page.assertEmptyMessageHolder();
        page.assertNoExistCvvMessage();
    }
    //todo issue ex-year "Неверно указан срок действия карты" fac-no message

    @Test
    @DisplayName("Отправка формы: год в будущем(+6) - оплата картой")
    void shouldValidateFieldCardWithBigFutureYear() {
        val card = Card.generatedApprovedCardWithBigFutureYear("en");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCard();

        page.assertNoExistCardMessage();
        page.assertNoExistMonthMessage();
        page.assertMessageForYear();
        page.assertNoExistHolderMessage();
        page.assertNoExistCvvMessage();
    }

    @Test
    @DisplayName("Отправка формы: невалидные месяц, год - оплата картой")
    void shouldValidateFieldCardWithMixedCharMonthYear() {
        val card = Card.generatedApprovedCardWithMixedMonthYear("ru");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCard();

        page.assertNoExistCardMessage();
        page.assertBadFormatMessageMonth();
        page.assertBadFormatMessageYear();
        page.assertNoExistHolderMessage();
        page.assertNoExistCvvMessage();
    }

    @Test
    @DisplayName("Отправка формы с пустым месяцем и годом - оплата картой")
    void shouldValidateFieldCardWithEmptyMonthYear() {
        val card = Card.generatedApprovedCardWithEmptyMonthYear();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCard();

        page.assertNoExistCardMessage();
        page.assertEmptyMessageMonth();
        page.assertEmptyMessageYear();
        page.assertNoExistHolderMessage();
        page.assertNoExistCvvMessage();
    }
    //todo issue ex-month, year "Поле обязательно для заполнения" fac-"Неверный формат"

    @Test
    @DisplayName("Отправка формы с невалидным владельцем - оплата картой")
    void shouldValidateFieldCardWithNoValidateHolder() {
        val card = Card.generatedNoDbCardWithBadHolder("ru");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCard();

        page.assertNoExistCardMessage();
        page.assertNoExistMonthMessage();
        page.assertNoExistYearMessage();
        page.assertBadFormatMessageHolder();
        page.assertNoExistCvvMessage();
    }
    // todo issue ex-holder "Неверный формат" fac - no message no validate field

    public Long[] assertDbAfterPayByCard(Long oldCardTableRow, String paymentResult, int price, Long oldOrderTableRow) {
        Long newCardTableRow = DbInteraction.getCountRowCard();
        assertEquals(oldCardTableRow + 1, newCardTableRow);
        val paymentFromDb = DbInteraction.getPaymentByCard();
        assertEquals(paymentResult, paymentFromDb.getStatus());
        assertEquals(price, paymentFromDb.getAmount());
        Long newOrderTableRow = DbInteraction.getCountRowOrder();
        assertEquals(oldOrderTableRow + 1, newOrderTableRow);
        assertTrue(DbInteraction.getOrderById(paymentFromDb.getTransaction_id()));
        return new Long[]{newCardTableRow, newOrderTableRow};
    }

    public void assertDbAfterNoValidateCard() {
        Long newCardTableRow = DbInteraction.getCountRowCard();
        assertEquals(cardTableRow, newCardTableRow);
        Long newOrderTableRow = DbInteraction.getCountRowOrder();
        assertEquals(orderTableRow, newOrderTableRow);
    }
}
