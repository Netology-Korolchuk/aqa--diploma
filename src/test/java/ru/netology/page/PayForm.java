package ru.netology.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.Card;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PayForm {
    private SelenideElement buttonPayByCard = $$(".button").find(exactText("Купить"));
    private SelenideElement buttonPayByCredit = $$(".button").find(exactText("Купить в кредит"));
    private SelenideElement buttonContinue = $$(".button").find(exactText("Продолжить"));
    private ElementsCollection formFields = $$(".input");
    private SelenideElement cardInput = formFields.find(text("Номер карты")).$(".input__control");
    private SelenideElement monthInput = formFields.find(text("Месяц")).$(".input__control");
    private SelenideElement yearInput = formFields.find(text("Год")).$(".input__control");
    private SelenideElement holderInput = formFields.find(text("Владелец")).$(".input__control");
    private SelenideElement cvvInput = formFields.find(text("CVC/CVV")).$(".input__control");
    private SelenideElement cardMessage = formFields.find(text("Номер карты")).$(".input__sub");
    private SelenideElement monthMessage = formFields.find(text("Месяц")).$(".input__sub");
    private SelenideElement yearMessage = formFields.find(text("Год")).$(".input__sub");
    private SelenideElement holderMessage = formFields.find(text("Владелец")).$(".input__sub");
    private SelenideElement cvvMessage = formFields.find(text("CVC/CVV")).$(".input__sub");
    private SelenideElement popupWindow = $(".notification");
    private ElementsCollection popupTitle = $$(".notification__title");
    private ElementsCollection popupContent = $$(".notification__content");
    private SelenideElement closeMessage = $(".icon-button .icon");
    private SelenideElement priceString = $("ul :nth-child(4)");
    private String goodTitle = "Успешно";
    private String goodContent = "Операция одобрена Банком.";
    private String badTitle = "Ошибка";
    private String badContent = "Ошибка! Банк отказал в проведении операции.";
    private String emptyMessage = "Поле обязательно для заполнения";
    private String badFormatMessage = "Неверный формат";
    private String messageForExpired = "Истёк срок действия карты";
    private String specialMessageForMonthYear = "Неверно указан срок действия карты";

    public void setPayByCard() {
        buttonPayByCard.click();
    }

    public void setPayByCredit() {
        buttonPayByCredit.click();
    }

    public void setFormFiled(Card card) {
        cardInput.setValue(card.getNumber());
        monthInput.setValue(card.getMonth());
        yearInput.setValue(card.getYear());
        holderInput.setValue(card.getHolder());
        cvvInput.setValue(card.getCvc());
        buttonContinue.click();
    }

    public void setFormFiledAndRefresh(Card card) {
        cardInput.setValue(card.getNumber());
        monthInput.setValue(card.getMonth());
        yearInput.setValue(card.getYear());
        holderInput.setValue(card.getHolder());
        cvvInput.setValue(card.getCvc());
        refresh();
    }

    public void assertFieldsIsEmpty() {
        assertEquals("", cardInput.getValue());
        assertEquals("", monthInput.getValue());
        assertEquals("", yearInput.getValue());
        assertEquals("", holderInput.getValue());
        assertEquals("", cvvInput.getValue());
    }

    public void setFormRepeatedly() {
        closeMessage.click();
        buttonContinue.click();
    }

    public void assertEmptyMessageCard() {
        cardMessage.shouldHave(exactText(emptyMessage));
    }

    public void assertEmptyMessageMonth() {
        monthMessage.shouldHave(exactText(emptyMessage));
    }

    public void assertEmptyMessageYear() {
        yearMessage.shouldHave(exactText(emptyMessage));
    }

    public void assertEmptyMessageHolder() {
        holderMessage.shouldHave(exactText(emptyMessage));
    }

    public void assertEmptyMessageCvv() {
        cvvMessage.shouldHave(exactText(emptyMessage));
    }

    public void assertBadFormatMessageCard() {
        cardMessage.shouldHave(exactText(badFormatMessage));
    }

    public void assertBadFormatMessageMonth() {
        monthMessage.shouldHave(exactText(badFormatMessage));
    }

    public void assertBadFormatMessageYear() {
        yearMessage.shouldHave(exactText(badFormatMessage));
    }

    public void assertBadFormatMessageHolder() {
        holderMessage.shouldHave(exactText(badFormatMessage));
    }

    public void assertBadFormatMessageCvv() {
        cvvMessage.shouldHave(exactText(badFormatMessage));
    }

    public void assertNoExistCardMessage() {
        cardMessage.shouldNotBe(exist);
    }

    public void assertNoExistMonthMessage() {
        monthMessage.shouldNotBe(exist);
    }

    public void assertNoExistYearMessage() {
        yearMessage.shouldNotBe(exist);
    }

    public void assertNoExistHolderMessage() {
        holderMessage.shouldNotBe(exist);
    }

    public void assertNoExistCvvMessage() {
        cvvMessage.shouldNotBe(exist);
    }

    public void assertMessageForExpiredYear() {
        yearMessage.shouldHave(exactText(messageForExpired));
    }

    public void assertMessageForMonth() {
        monthMessage.shouldHave(exactText(specialMessageForMonthYear));
    }

    public void assertMessageForYear() {
        yearMessage.shouldHave(exactText(specialMessageForMonthYear));
    }

    public void assertGoodMessage() {
        popupWindow.waitUntil(visible, 20000);
        popupTitle.find(exactText(goodTitle)).shouldBe(visible);
        popupContent.find(exactText(goodContent)).shouldBe(visible);
    }

    public void assertBadMessage() {
        popupWindow.waitUntil(visible, 20000);
        popupTitle.find(exactText(badTitle)).shouldBe(visible);
        popupContent.find(exactText(badContent)).shouldBe(visible);
    }

    public void assertValueField(Card card) {
        assertEquals(card.getNumber().substring(0, 19), cardInput.getValue());
        assertEquals(card.getMonth().substring(0, 2), monthInput.getValue());
        assertEquals(card.getYear().substring(0, 2), yearInput.getValue());
        assertEquals(card.getHolder(), holderInput.getValue());
        assertEquals(card.getCvc().substring(0, 3), cvvInput.getValue());
    }

    public int getPriceTour() {
        String price = priceString.getText().replaceAll("\\D+", "");
        return Integer.parseInt(price);
    }
}
