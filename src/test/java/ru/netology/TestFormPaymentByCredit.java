package ru.netology;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.Card;
import ru.netology.data.DataHelper;
import ru.netology.interaction.DbInteraction;
import ru.netology.page.PayForm;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFormPaymentByCredit {
    private String serviceUrl = "http://localhost:8080/";
    private Long creditTableRow;
    private Long orderTableRow;

    @AfterAll
    static void cleanDb() {
        DbInteraction.clearDB();
    }

    @BeforeEach
    void getRowInDb() {
        creditTableRow = DbInteraction.getCountRowCredit();
        orderTableRow = DbInteraction.getCountRowOrder();
    }

    @Test
    @DisplayName("Купить тур в кредит: APPROVED карта, валидные значения для формы")
    void shouldPayByApprovedCredit() {
        val card = Card.generatedApprovedCard("en");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);
        page.assertGoodMessage();

        assertDbAfterPayByCredit(creditTableRow, DataHelper.PaymentResult.APPROVED.toString(), orderTableRow);
    }

    @Test
    @DisplayName("Купить тур в кредит: APPROVED карта, валидные значения для формы, срок действия - текущий месяц и год")
    void shouldPayByApprovedCreditNowExpired() {
        val card = Card.generatedApprovedCardWithNowDate("ru");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);
        page.assertGoodMessage();

        assertDbAfterPayByCredit(creditTableRow, DataHelper.PaymentResult.APPROVED.toString(), orderTableRow);
    }

    @Test
    @DisplayName("Купить тур в кредит: DECLINED карта, валидные значения для формы")
    void shouldNoPayByDeclinedCredit() {
        val card = Card.generatedDeclinedCard("ru");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);
        page.assertBadMessage();

        assertDbAfterPayByCredit(creditTableRow, DataHelper.PaymentResult.DECLINED.toString(), orderTableRow);
    }
    //todo issue ex -"declined" fac -"approved"

    @Test
    @DisplayName("Повторная оплата с той же карты в кредит")
    void shouldPayByApprovedCreditWithRepeat() {
        val card = Card.generatedApprovedCard("en");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);
        page.assertGoodMessage();

        Long[] newCounts = assertDbAfterPayByCredit(creditTableRow, DataHelper.PaymentResult.APPROVED.toString(), orderTableRow);

        page.setFormRepeatedly();
        page.assertGoodMessage();

        assertDbAfterPayByCredit(newCounts[0], DataHelper.PaymentResult.APPROVED.toString(), newCounts[1]);
    }

    @Test
    @DisplayName("Обновление страницы во время заполнения данных - отплата в кредит")
    void shouldEmptyCreditFormAfterRefresh() {
        val card = Card.generatedDeclinedCard("en");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiledAndRefresh(card);
        page.setPayByCredit();
        page.assertFieldsIsEmpty();
    }

    @Test
    @DisplayName("Отправка формы с картой не из базы - оплата в кредит")
    void shouldNoPayByNoDbCredit() {
        val card = Card.generatedNoDbCard();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);
        page.assertBadMessage();

        assertDbAfterNoValidateCredit();
    }

    @Test
    @DisplayName("Отправка формы с невалидными значениями (min) - оплата в кредит")
    void shouldValidateCreditFormWithMinChar() {
        val card = Card.generatedCardWithMinChar("ru");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(card);

        assertDbAfterNoValidateCredit();

        page.assertBadFormatMessageCard();
        page.assertBadFormatMessageMonth();
        page.assertBadFormatMessageYear();
        page.assertNoExistHolderMessage();
        page.assertBadFormatMessageCvv();
    }

    @Test
    @DisplayName("Отправка формы с невалидными значениями (max) - оплата в кредит")
    void shouldValidateCreditFormWithMaxChar() {
        val card = Card.generatedCardWithMaxChar();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);
        page.assertValueField(card);

        assertDbAfterNoValidateCredit();
    }

    @Test
    @DisplayName("Отправка формы с невалидными значениями (буквы вместо цифр/цифры вместо букв) - оплата в кредит")
    void shouldValidateCreditFromWithNoValidateChar() {
        val card = Card.generatedCardWithLetters("ru");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);

        assertDbAfterNoValidateCredit();

        page.assertBadFormatMessageCard();
        page.assertBadFormatMessageMonth();
        page.assertBadFormatMessageYear();
        page.assertBadFormatMessageHolder();
        page.assertBadFormatMessageCvv();
    }
    //todo issue ex- holder "Неверный формат" fac-"Поле обязательно для заполнения"

    @Test
    @DisplayName("Отправка формы с невалидными значениями (спецсимволы) - оплата в кредит")
    void shouldValidateCreditFormWithSpecialChar() {
        val card = Card.generatedCardWithSpecialChar();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);

        assertDbAfterNoValidateCredit();

        page.assertBadFormatMessageCard();
        page.assertBadFormatMessageMonth();
        page.assertBadFormatMessageYear();
        page.assertBadFormatMessageHolder();
        page.assertBadFormatMessageCvv();
    }
    //todo issue ex- holder "Неверный формат" fac-"Поле обязательно для заполнения"

    @Test
    @DisplayName("Отправка формы: пустая карта, невалидные месяц, владелец, cvv - оплата в кредит")
    void shouldValidateFieldCreditWithEmptyCardNoValidateOtherField() {
        val card = Card.generatedCardWithEmptyCardBadMonthHolderCvv();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);

        assertDbAfterNoValidateCredit();

        page.assertEmptyMessageCard();
        page.assertMessageForMonth();
        page.assertNoExistYearMessage();
        page.assertBadFormatMessageHolder();
        page.assertBadFormatMessageCvv();
    }
    //todo issue ex-card "Поле обязательно для заполнения", holder "Неверный формат" fac - card Неверный формат, holder no message

    @Test
    @DisplayName("Отправка формы: невалидная карта, истекший год, невалидный cvv - оплата в кредит")
    void shouldValidateFieldCreditWithNoValidateCardCvvExpiredYear() {
        val card = Card.generatedCardWithMixedCardCvvExpiredYear("ru");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);

        assertDbAfterNoValidateCredit();

        page.assertBadFormatMessageCard();
        page.assertNoExistMonthMessage();
        page.assertMessageForExpiredYear();
        page.assertNoExistHolderMessage();
        page.assertBadFormatMessageCvv();
    }
    //todo issue ex-year "Истёк срок действия карты" fac-year no message holder "Поле обязательно для заполнения"

    @Test
    @DisplayName("Отправка формы: невалидная карта, истекший месяц, пустой cvv - оплата в кредит")
    void shouldValidateFieldCreditWithNoValidateCardExpiredMonthEmptyCvv() {
        val card = Card.generatedCardWithBadCardExpiredMonthEmptyCvv("en");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);

        assertDbAfterNoValidateCredit();

        page.assertBadFormatMessageCard();
        page.assertMessageForMonth();
        page.assertNoExistYearMessage();
        page.assertNoExistHolderMessage();
        page.assertEmptyMessageCvv();
    }
    //todo issue ex- month "Неверно указан срок действия карты", cvv "Поле обязательно для заполнения" fac-month no message, holder "Поле обязательно для заполнения"

    @Test
    @DisplayName("Отправка формы: год в будущем(+6), пустой владелец - оплата в кредит")
    void shouldValidateFieldCreditWithBigFutureYearEmptyHolder() {
        val card = Card.generatedNoDbCardWithBigFutureYearEmptyHolder();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);

        assertDbAfterNoValidateCredit();

        page.assertNoExistCardMessage();
        page.assertNoExistMonthMessage();
        page.assertMessageForYear();
        page.assertEmptyMessageHolder();
        page.assertNoExistCvvMessage();
    }
    //todo issue ex-year "Неверно указан срок действия карты" fac-no message

    @Test
    @DisplayName("Отправка формы: год в будущем(+6) - оплата в кредит")
    void shouldValidateFieldCreditWithBigFutureYear() {
        val card = Card.generatedApprovedCardWithBigFutureYear("en");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);

        assertDbAfterNoValidateCredit();

        page.assertNoExistCardMessage();
        page.assertNoExistMonthMessage();
        page.assertMessageForYear();
        page.assertNoExistHolderMessage();
        page.assertNoExistCvvMessage();
    }

    @Test
    @DisplayName("Отправка формы: невалидные месяц, год  - оплата в кредит")
    void shouldValidateFieldCreditWithMixedCharMonthYear() {
        val card = Card.generatedApprovedCardWithMixedMonthYear("en");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);

        assertDbAfterNoValidateCredit();

        page.assertNoExistCardMessage();
        page.assertBadFormatMessageMonth();
        page.assertBadFormatMessageYear();
        page.assertNoExistHolderMessage();
        page.assertNoExistCvvMessage();
    }

    @Test
    @DisplayName("Отправка формы с пустым месяцем и годом - оплата в кредит")
    void shouldValidateFieldCreditWithEmptyMonthYear() {
        val card = Card.generatedApprovedCardWithEmptyMonthYear();
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);

        assertDbAfterNoValidateCredit();

        page.assertNoExistCardMessage();
        page.assertEmptyMessageMonth();
        page.assertEmptyMessageYear();
        page.assertNoExistHolderMessage();
        page.assertNoExistCvvMessage();
    }
    //todo issue ex-month, year "Поле обязательно для заполнения" fac-"Неверный формат"

    @Test
    @DisplayName("Отправка формы с невалидным владельцем - оплата в кредит")
    void shouldValidateFieldCreditWithEmptyYear() {
        val card = Card.generatedNoDbCardWithBadHolder("en");
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCredit();
        page.setFormFiled(card);

        assertDbAfterNoValidateCredit();

        page.assertNoExistCardMessage();
        page.assertNoExistMonthMessage();
        page.assertNoExistYearMessage();
        page.assertBadFormatMessageHolder();
        page.assertNoExistCvvMessage();
    }
    // todo issue ex-holder "Неверный формат" fac - no message no validate field

    public Long[] assertDbAfterPayByCredit(Long oldCreditTableRow, String paymentResult, Long oldOrderTableRow) {
        Long newCreditTableRow = DbInteraction.getCountRowCredit();
        assertEquals(oldCreditTableRow + 1, newCreditTableRow);
        val paymentFromDb = DbInteraction.getPaymentByCredit();
        assertEquals(paymentResult, paymentFromDb.getStatus());
        Long newOrderTableRow = DbInteraction.getCountRowOrder();
        assertEquals(oldOrderTableRow + 1, newOrderTableRow);
        assertTrue(DbInteraction.getOrderById(paymentFromDb.getBank_id()));
        return new Long[]{newCreditTableRow, newOrderTableRow};
    }

    public void assertDbAfterNoValidateCredit() {
        Long newCreditTableRow = DbInteraction.getCountRowCredit();
        assertEquals(creditTableRow, newCreditTableRow);
        Long newOrderTableRow = DbInteraction.getCountRowOrder();
        assertEquals(orderTableRow, newOrderTableRow);
    }
}
