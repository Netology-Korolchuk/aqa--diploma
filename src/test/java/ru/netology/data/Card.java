package ru.netology.data;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Data
@AllArgsConstructor
public class Card {
    private String number;
    private String month;
    private String year;
    private String holder;
    private String cvc;
    private static String approvedCard = "4444 4444 4444 4441";
    private static String declinedCard = "4444 4444 4444 4442";

    @Step("Генерация approved card")
    public static Card generatedApprovedCard(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(approvedCard,
                getMonthFromArray(setGoodMonth()),
                String.valueOf(LocalDate.now().plusYears(getRandomInt()).getYear()).substring(2),
                faker.name().fullName(),
                faker.numerify("###"));
    }

    @Step("Генерация declined card")
    public static Card generatedDeclinedCard(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(declinedCard,
                String.valueOf(LocalDate.now().getMonthValue()),
                String.valueOf(LocalDate.now().getYear()).substring(2),
                faker.name().firstName().substring(0, 1).concat(".").concat(faker.name().lastName()),
                faker.numerify("###"));
    }

    @Step("Генерация NoDb card")
    public static Card generatedNoDbCard() {
        Faker faker = new Faker();
        return new Card(faker.business().creditCardNumber().replace("-", " "),
                getMonthFromArray(setGoodMonth()),
                String.valueOf(LocalDate.now().plusYears(getRandomInt()).getYear()).substring(2),
                faker.name().prefix().concat(faker.name().lastName()),
                faker.numerify("###"));
    }

    private static List<String> setGoodMonth() {
        List<String> goodMonth = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
        return goodMonth;
    }

    private static String getMonthFromArray(List<String> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    private static int getRandomInt() {
        Random random = new Random();
        return random.nextInt(5) + 1;
    }
}
