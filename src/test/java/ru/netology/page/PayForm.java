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
    private SelenideElement closeMessage = $(".icon-button .icon");
    private SelenideElement priceString = $("ul :nth-child(4)");
    private SelenideElement goodPopup = $(".notification_status_ok");
    private SelenideElement badPopup = $(".notification_status_error");

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

    public void assertMessageCard(String message) {
        cardMessage.shouldHave(exactText(message));
    }

    public void assertMessageMonth(String message) {
        monthMessage.shouldHave(exactText(message));
    }

    public void assertMessageYear(String message) {
        yearMessage.shouldHave(exactText(message));
    }

    public void assertMessageHolder(String message) {
        holderMessage.shouldHave(exactText(message));
    }

    public void assertMessageCvv(String message) {
        cvvMessage.shouldHave(exactText(message));
    }

    public void assertNoExistHolderMessage() {
        holderMessage.shouldNotBe(exist);
    }

    public void assertGoodMessage() {
        goodPopup.waitUntil(visible, 10000);
        badPopup.shouldNotBe(visible);
    }

    public void assertBadMessage() {
        badPopup.waitUntil(visible, 10000);
        goodPopup.shouldNotBe(visible);
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
