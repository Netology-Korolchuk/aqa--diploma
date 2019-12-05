package ru.netology.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DataHelper {
    private DataHelper() {
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentByCardDto {
        private int amount;
        private String status;
        private String transaction_id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentByCreditDto {
        private String bank_id;
        private String status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseApi {
        private String status;
    }

    public enum PaymentResult {
        APPROVED, DECLINED
    }
}
