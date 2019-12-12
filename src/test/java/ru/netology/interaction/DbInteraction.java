package ru.netology.interaction;

import io.qameta.allure.Step;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import ru.netology.data.DataHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.util.Properties;

public class DbInteraction {
    private static String url = System.getProperty("db.url");
    private static String userDB;
    private static String password;
    private static QueryRunner runner = new QueryRunner();

    static {
        Properties property = new Properties();
        try (FileInputStream file = new FileInputStream("application.properties")) {
            property.load(file);
            userDB = property.getProperty("spring.datasource.username");
            password = property.getProperty("spring.datasource.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Step("Очистка БД")
    @SneakyThrows
    public static void clearDB() {
        try (val conn = DriverManager.getConnection(url, userDB, password)) {
            runner.update(conn, "DELETE FROM credit_request_entity;");
            runner.update(conn, "DELETE FROM payment_entity;");
            runner.update(conn, "DELETE FROM order_entity;");
        }
    }

    @Step("Получение транзакции из таблицы Card")
    @SneakyThrows
    public static DataHelper.PaymentByCardDto getPaymentByCard() {
        val paymentSql = "SELECT amount, status FROM payment_entity WHERE transaction_id = (SELECT payment_id FROM order_entity ORDER BY created DESC limit 1);";
        try (val conn = DriverManager.getConnection(url, userDB, password)) {
            return runner.query(conn, paymentSql, new BeanHandler<>(DataHelper.PaymentByCardDto.class));
        }
    }

    @Step("Получение статуса транзакции из таблицы Credit")
    @SneakyThrows
    public static String getPaymentByCredit() {
        val paymentSql = "SELECT status FROM credit_request_entity WHERE bank_id = (SELECT credit_id FROM order_entity ORDER BY created DESC limit 1);";
        try (val conn = DriverManager.getConnection(url, userDB, password)) {
            return runner.query(conn, paymentSql, new  ScalarHandler<> ());
        }
    }
}
