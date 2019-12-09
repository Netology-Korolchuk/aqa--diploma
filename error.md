`    @SneakyThrows
     public static void clearDB() {
         try (val conn = DriverManager.getConnection(url, userDB, password)) {
             runner.update(conn, "DELETE FROM credit_request_entity; DELETE FROM payment_entity; DELETE FROM order_entity;");
         }
     }`
     
     `You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'DELETE FROM payment_entity; DELETE FROM order_entity' at line 1 Query: DELETE FROM credit_request_entity; DELETE FROM payment_entity; DELETE FROM order_entity; Parameters: []
     java.sql.SQLException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'DELETE FROM payment_entity; DELETE FROM order_entity' at line 1 Query: DELETE FROM credit_request_entity; DELETE FROM payment_entity; DELETE FROM order_entity; Parameters: []`


`System.getProperty("spring.datasource.url");` null
