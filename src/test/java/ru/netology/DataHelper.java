package ru.netology;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Locale;

public class DataHelper {
    private DataHelper() {}

    @Data
    @AllArgsConstructor
    public static class CardForForm {
        private String number;
        private String month;
        private String year;
        private String holder;
        private String cvc;

        public static CardForForm generatedApprovedCard(String local) {
            Faker faker = new Faker(new Locale(local));
            return new CardForForm("4444 4444 4444 4441",
                    String.valueOf(LocalDate.now().getMonthValue()),
                    String.valueOf(LocalDate.now().plusYears(2).getYear()).substring(2),
                    faker.name().fullName(),
                    faker.business().creditCardExpiry());
        }

        public static CardForForm generatedDeclinedCard(String local) {
            Faker faker = new Faker(new Locale(local));
            return new CardForForm("4444 4444 4444 4442",
                    String.valueOf(LocalDate.now().getMonthValue()),
                    String.valueOf(LocalDate.now().plusYears(3).getYear()).substring(2),
                    faker.name().firstName(),
                    faker.business().creditCardExpiry());
        }

        public static CardForForm generatedNoDbCard(String local) {
            Faker faker = new Faker(new Locale(local));
            return new CardForForm(faker.business().creditCardNumber(),
                    String.valueOf(LocalDate.now().getMonthValue()),
                    String.valueOf(LocalDate.now().plusYears(4).getYear()).substring(2),
                    faker.name().firstName(),
                    faker.business().creditCardExpiry());
        }
    }
}
