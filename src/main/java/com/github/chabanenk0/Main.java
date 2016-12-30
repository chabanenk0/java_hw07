package com.github.chabanenk0;

import java.sql.*;

/**
 * Created by dmitry on 30.12.16.
 */
public class Main
{
    private static Connection getConnection() throws SQLException {
        String dbUrl;
        dbUrl = "jdbc:mysql://127.0.0.1:3306/javadb?verifyServerCertificate=false&useSSL=true"; // mysql
        //dbUrl = "jdbc:h2:mem:h2java";
        //dbUrl = "jdbc:h2:file:~/h2java";
        Connection connection = DriverManager.getConnection(dbUrl, "root", "111");

        return connection;
    }

    private static void dropSchema(Connection connection)
    {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS employee");
            statement.executeUpdate("DROP TABLE IF EXISTS salary");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createSchema(Connection connection)
    {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE employee (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR (255))");
            statement.executeUpdate("CREATE TABLE salary (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, date DATE, value DECIMAL (10,3), employee_id INT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int insertEmployee(Connection connection, String name)
    {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO employee (name) VALUES(?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            int numberOfChanged = statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            int lastId = -1;
            if (rs.next()){
                lastId = rs.getInt(1);
            } else {
                throw new SQLException("No id of the inserted object");
            }

            return lastId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void printEmployeesTotalSalary(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT e.id, e.name, sum(s.value) as salary from employee e JOIN salary s on (e.id = s.employee_id) GROUP BY employee_id");
            System.out.println("Employees salary:");
            System.out.println("id\tname\tsalary");
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                double salary = resultSet.getDouble(3);
                System.out.println(id + "\t" + name + "\t" + salary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int insertSalary(Connection connection, int employeeId, double salaryValue, Date date)
    {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO salary (employee_id, value, date) VALUES(?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, employeeId);
            statement.setDouble(2, salaryValue);
            statement.setDate(3, date);
            int numberOfChanged = statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            int lastId = -1;
            if (rs.next()) {
                lastId = rs.getInt(1);
            } else {
                throw new SQLException("No id of the inserted object");
            }
            return lastId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        try {
            Connection connection = getConnection();
            dropSchema(connection);
            createSchema(connection);
            int vasyaId = insertEmployee(connection, "Vasya");
            int kolyaId = insertEmployee(connection, "Kolya");
            int petyaId = insertEmployee(connection, "Petya");
            insertSalary(connection, vasyaId, 100, new Date(2016,12, 1));
            insertSalary(connection, vasyaId, 110, new Date(2016,12, 15));
            insertSalary(connection, kolyaId, 210, new Date(2016,12, 1));
            insertSalary(connection, kolyaId, 150, new Date(2016,12, 15));
            insertSalary(connection, petyaId, 156, new Date(2016,12, 1));
            printEmployeesTotalSalary(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
