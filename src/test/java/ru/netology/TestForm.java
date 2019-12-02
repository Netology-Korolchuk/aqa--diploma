package ru.netology;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.page.PayForm;

import static com.codeborne.selenide.Selenide.*;

public class TestForm {
    private String serviceUrl = "http://localhost:8080/";
    private static DataHelper.CardForForm approvedCard;
    private static DataHelper.CardForForm declinedCard;
    private static DataHelper.CardForForm noDbCard;

    @BeforeAll
    static void setData() {
        approvedCard = DataHelper.CardForForm.generatedApprovedCard("ru");
        declinedCard = DataHelper.CardForForm.generatedDeclinedCard("en");
        noDbCard = DataHelper.CardForForm.generatedNoDbCard("ru");
    }

    @AfterAll
    static void cleanDb() {
        DbInteraction.clearDB();
    }

    @Test
    void shouldPayByApprovedCard() {
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(approvedCard);
        page.assertGoodMessage();
    }

    @Test
    void shouldNoPayByDeclinedCard() {
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(declinedCard);
        page.assertBadMessage();
    }

    @Test
    void shouldPayByCardBad1() {
        open(serviceUrl);
        val page = new PayForm();
        page.setPayByCard();
        page.setFormFiled(noDbCard);
        page.assertBadMessage();
    }
}
