package ru.netology.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.netology.data.Card;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

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
    private SelenideElement priceString = $("ul :nth-child(4)");
    private SelenideElement goodPopup = $(".notification_status_ok");
    private SelenideElement badPopup = $(".notification_status_error");

    @Step("Выбор варианта оплаты по карте")
    public void setPayByCard() {
        buttonPayByCard.click();
    }

    @Step("Выбор варианта оплаты в кредит")
    public void setPayByCredit() {
        buttonPayByCredit.click();
    }

    @Step("Установка значений в поля и отправка формы")
    public void setFormFiled(Card card) {
        cardInput.setValue(card.getNumber());
        monthInput.setValue(card.getMonth());
        yearInput.setValue(card.getYear());
        holderInput.setValue(card.getHolder());
        cvvInput.setValue(card.getCvc());
        buttonContinue.click();
    }

    @Step("Проверка сообщения об ошибке поля card")
    public void assertMessageCard(String message) {
        cardMessage.shouldHave(exactText(message));
    }

    @Step("Проверка сообщения об ошибке поля month")
    public void assertMessageMonth(String message) {
        monthMessage.shouldHave(exactText(message));
    }

    @Step("Проверка сообщения об ошибке поля year")
    public void assertMessageYear(String message) {
        yearMessage.shouldHave(exactText(message));
    }

    @Step("Проверка сообщения об ошибке поля holder")
    public void assertMessageHolder(String message) {
        holderMessage.shouldHave(exactText(message));
    }

    @Step("Проверка сообщения об ошибке поля cvv")
    public void assertMessageCvv(String message) {
        cvvMessage.shouldHave(exactText(message));
    }

    @Step("Проверка отсутствия сообщения об ошибке для поля holder")
    public void assertNoExistHolderMessage() {
        holderMessage.shouldNotBe(exist);
    }

    @Step("Проверка сообщения об успешной оплате")
    public void assertGoodMessage() {
        goodPopup.waitUntil(visible, 15000);
        badPopup.shouldNotBe(visible.because("Видно должно быть только одно сообщение"));
    }

    @Step("Проверка сообщения о неуспешной оплате")
    public void assertBadMessage() {
        badPopup.waitUntil(visible, 15000);
        goodPopup.shouldNotBe(visible.because("Видно должно быть только одно сообщение"));
    }

    @Step("Получение цены тура")
    public int getPriceTour() {
        String price = priceString.getText().replaceAll("\\D+", "");
        return Integer.parseInt(price);
    }
}
