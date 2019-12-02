package ru.netology;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.DriverManager;

public class DbInteraction {
    private static String url = "jdbc:mysql://192.168.99.100:3306/app";
    private static String userDB = "app";
    private static String password = "pass";
    private static QueryRunner runner = new QueryRunner();

    @SneakyThrows
    static void clearDB() {
        try (val conn = DriverManager.getConnection(url, userDB, password)) {
            runner.update(conn, "DELETE FROM credit_request_entity");
            runner.update(conn, "DELETE FROM payment_entity");
            runner.update(conn, "DELETE FROM order_entity");
        }
    }
}
