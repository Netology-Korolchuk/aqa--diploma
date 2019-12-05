package ru.netology.interaction;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import ru.netology.data.DataHelper;

import java.sql.DriverManager;

public class DbInteraction {
    private static String url = "jdbc:mysql://192.168.99.100:3306/app";
    private static String userDB = "app";
    private static String password = "pass";
    private static QueryRunner runner = new QueryRunner();

    @SneakyThrows
    public static void clearDB() {
        try (val conn = DriverManager.getConnection(url, userDB, password)) {
            runner.update(conn, "DELETE FROM credit_request_entity;");
            runner.update(conn, "DELETE FROM payment_entity;");
            runner.update(conn, "DELETE FROM order_entity;");
        }
    }

    @SneakyThrows
    public static Long getCountRowCredit() {
        val countSQL = "SELECT COUNT(*) FROM credit_request_entity;";
        try (val conn = DriverManager.getConnection(url, userDB, password)) {
            Long count = runner.query(conn, countSQL, new ScalarHandler<>());
            return count;
        }
    }

    @SneakyThrows
    public static Long getCountRowCard() {
        val countSQL = "SELECT COUNT(*) FROM payment_entity;";
        try (val conn = DriverManager.getConnection(url, userDB, password)) {
            Long count = runner.query(conn, countSQL, new ScalarHandler<>());
            return count;
        }
    }

    @SneakyThrows
    public static Long getCountRowOrder() {
        val countSQL = "SELECT COUNT(*) FROM order_entity;";
        try (val conn = DriverManager.getConnection(url, userDB, password)) {
            Long count = runner.query(conn, countSQL, new ScalarHandler<>());
            return count;
        }
    }

    @SneakyThrows
    public static DataHelper.PaymentByCardDto getPaymentByCard() {
        val paymentSql = "SELECT * FROM payment_entity ORDER BY created DESC;";
        try (val conn = DriverManager.getConnection(url, userDB, password)) {
            DataHelper.PaymentByCardDto paymentByCardDB = runner.query(conn, paymentSql, new BeanHandler<>(DataHelper.PaymentByCardDto.class));
            return paymentByCardDB;
        }
    }

    @SneakyThrows
    public static DataHelper.PaymentByCreditDto getPaymentByCredit() {
        val paymentSql = "SELECT * FROM credit_request_entity ORDER BY created DESC;";
        try (val conn = DriverManager.getConnection(url, userDB, password)) {
            DataHelper.PaymentByCreditDto paymentByCreditDB = runner.query(conn, paymentSql, new BeanHandler<>(DataHelper.PaymentByCreditDto.class));
            return paymentByCreditDB;
        }
    }

    @SneakyThrows
    public static boolean getOrderById(String id) {
        val orderSQL = "SELECT COUNT(*) FROM order_entity WHERE payment_id = ?;";
        try (val conn = DriverManager.getConnection(url, userDB, password)) {
            Long countOrderById = runner.query(conn, orderSQL, new ScalarHandler<>(), id);
            if (countOrderById > 0) {
                return true;
            } else return false;
        }
    }
}
