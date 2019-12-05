package ru.netology.data;

import com.github.javafaker.Faker;
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

    public static Card generatedApprovedCard(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(approvedCard,
                getMonthFromArray(setGoodMonth()),
                String.valueOf(LocalDate.now().plusYears(getRandomInt()).getYear()).substring(2),
                faker.name().fullName(),
                faker.numerify("###"));
    }

    public static Card generatedApprovedCardWithNowDate(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(approvedCard,
                String.valueOf(LocalDate.now().getMonthValue()),
                String.valueOf(LocalDate.now().getYear()).substring(2),
                faker.name().firstName().concat(".").concat(faker.name().lastName()).concat("-").concat(faker.name().lastName()),
                faker.numerify("###"));
    }

    public static Card generatedDeclinedCard(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(declinedCard,
                getMonthFromArray(setGoodMonth()),
                String.valueOf(LocalDate.now().plusYears(getRandomInt()).getYear()).substring(2),
                faker.name().username(),
                faker.numerify("###"));
    }

    public static Card generatedNoDbCard() {
        Faker faker = new Faker();
        return new Card(faker.business().creditCardNumber().replace("-", " "),
                getMonthFromArray(setGoodMonth()),
                String.valueOf(LocalDate.now().plusYears(getRandomInt()).getYear()).substring(2),
                faker.name().prefix().concat(faker.name().lastName()),
                faker.numerify("###"));
    }

    public static Card generatedCardWithMinChar(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(faker.business().creditCardNumber().replace("-", " ").substring(1),
                getMonthFromArray(setBadMonth()),
                String.valueOf(LocalDate.now().plusYears(getRandomInt()).getYear()).substring(3),
                faker.name().firstName().substring(0, 1),
                faker.numerify("##"));
    }

    public static Card generatedCardWithMaxChar() {
        Faker faker = new Faker();
        return new Card(faker.business().creditCardNumber().replace("-", " ").concat("1"),
                getMonthFromArray(setBadMonth()).concat(getMonthFromArray(setGoodMonth())),
                String.valueOf(LocalDate.now().plusYears(getRandomInt()).getYear()).substring(1),
                "adadaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaagfggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggfgggggggggggggdgfretererertytytytytyaaaaaaaaaaaaaaaaaaaaaadddddddddd",
                faker.numerify("####"));
    }

    public static Card generatedCardWithLetters(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(faker.letterify("???? ???? ???? ????"),
                faker.letterify("??"),
                faker.letterify("??"),
                faker.numerify("#. #####"),
                faker.letterify("???"));
    }

    public static Card generatedCardWithSpecialChar() {
        return new Card("?#@! ()/* $%^& _=-+",
                "@<",
                ">:",
                ";. {}?`~",
                "№[]");
    }

    public static Card generatedCardWithEmptyCardBadMonthHolderCvv() {
        Faker faker = new Faker();
        return new Card("",
                "13",
                String.valueOf(LocalDate.now().plusYears(getRandomInt()).getYear()).substring(2),
                faker.name().firstName().concat(" Иванов"),
                faker.numerify("#"));
    }

    public static Card generatedCardWithMixedCardCvvExpiredYear(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(faker.bothify("?#&? ???> ???$ @???"),
                getMonthFromArray(setGoodMonth()),
                String.valueOf(LocalDate.now().minusYears(getRandomInt()).getYear()).substring(2),
                faker.name().firstName().substring(0, 1).concat(".").concat(faker.name().lastName()),
                faker.bothify("??%"));
    }

    public static Card generatedCardWithBadCardExpiredMonthEmptyCvv(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(String.valueOf(getRandomInt()),
                String.valueOf(LocalDate.now().minusMonths(1).getMonthValue()),
                String.valueOf(LocalDate.now().getYear()).substring(2),
                faker.name().firstName().substring(0, 1).concat("/").concat(faker.name().lastName()),
                "");
    }

    public static Card generatedNoDbCardWithBigFutureYearEmptyHolder() {
        Faker faker = new Faker();
        return new Card(faker.business().creditCardNumber().replace("-", " "),
                getMonthFromArray(setGoodMonth()),
                String.valueOf(LocalDate.now().plusYears(7).getYear()).substring(2),
                "",
                faker.numerify("###"));
    }

    public static Card generatedApprovedCardWithBigFutureYear(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(approvedCard,
                getMonthFromArray(setGoodMonth()),
                String.valueOf(LocalDate.now().plusYears(7).getYear()).substring(2),
                faker.name().firstName().concat(".").concat(faker.name().lastName()).concat("/").concat(faker.name().lastName()),
                faker.numerify("###"));
    }

    public static Card generatedApprovedCardWithMixedMonthYear(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(approvedCard,
                faker.bothify("(?"),
                faker.bothify("?,"),
                "xxx",
                faker.numerify("###"));
    }

    public static Card generatedApprovedCardWithEmptyMonthYear() {
        Faker faker = new Faker();
        return new Card(approvedCard,
                "",
                "",
                "cardholder",
                faker.numerify("###"));
    }

    public static Card generatedNoDbCardWithBadHolder(String local) {
        Faker faker = new Faker(new Locale(local));
        return new Card(faker.business().creditCardNumber().replace("-", ""),
                getMonthFromArray(setGoodMonth()),
                String.valueOf(LocalDate.now().plusYears(getRandomInt()).getYear()).substring(2),
                faker.bothify("?. ???#@??"),
                faker.numerify("###"));
    }

    private static List<String> setGoodMonth() {
        List<String> goodMonth = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
        return goodMonth;
    }

    private static List<String> setBadMonth() {
        List<String> badMonth = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9");
        return badMonth;
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
