package ru.netology.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.DataHelper;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class PayForm {

    private SelenideElement buttonPayByCard = $$(".button").find(exactText("Купить"));
    private SelenideElement buttonPayByCredit = $$(".button").find(exactText("Купить в кредит"));
    private SelenideElement buttonContinue = $$(".button").find(exactText("Продолжить"));
    private ElementsCollection formFields = $$(".input");
    private SelenideElement cardInput = formFields.find(exactText("Номер карты")).$(".input__control");
    private SelenideElement monthInput = formFields.find(exactText("Месяц")).$(".input__control");
    private SelenideElement yearInput = formFields.find(exactText("Год")).$(".input__control");
    private SelenideElement holderInput = formFields.find(exactText("Владелец")).$(".input__control");
    private SelenideElement cvvInput = formFields.find(exactText("CVC/CVV")).$(".input__control");
    private SelenideElement popupWindow = $(".notification");
    private ElementsCollection popupTitle = $$(".notification__title");
    private ElementsCollection popupContent = $$(".notification__content");
    private String goodTitle = "Успешно";
    private String goodContent = "Операция одобрена Банком.";
    private String badTitle = "Ошибка";
    private String badContent = "Ошибка! Банк отказал в проведении операции.";

    public void setPayByCard() {
        buttonPayByCard.click();
    }

    public void setPayByCredit() {
        buttonPayByCredit.click();
    }

    public void setFormFiled(DataHelper.CardForForm card) {
        cardInput.setValue(card.getNumber());
        monthInput.setValue(card.getMonth());
        yearInput.setValue(card.getYear());
        holderInput.setValue(card.getHolder());
        cvvInput.setValue(card.getCvc());
        buttonContinue.click();
    }

    public void assertGoodMessage() {
        popupWindow.waitUntil(visible, 15000);
        popupTitle.find(exactText(goodTitle)).shouldBe(visible);
        popupContent.find(exactText(goodContent)).shouldBe(visible);
    }

    public void assertBadMessage() {
        popupWindow.waitUntil(visible, 15000);
        popupTitle.find(exactText(badTitle)).shouldBe(visible);
        popupContent.find(exactText(badContent)).shouldBe(visible);
    }
}
