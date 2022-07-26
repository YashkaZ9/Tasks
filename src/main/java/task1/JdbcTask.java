package task1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcTask {
    private static final String URL = "jdbc:postgresql://localhost:5432/company";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "123";
    private static Connection connection;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Драйвер СУБД PostgreSQL не найден.");
        }
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Не удалось установить соединение с БД.");
        }
    }

    public static void saveEmployees(String inputFileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO employee VALUES (?, ?, ?, ?, ?, ?, ?)");
            long employeesNumber = 1;
            long id;
            BigDecimal salary;
            br.readLine();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            while (br.ready()) {
                String[] employeeData = br.readLine().split(";");
                employeesNumber++;
                if (employeeData.length != Company.EMPLOYEES_FILE_HEADER_LENGTH) {
                    System.out.printf("Работник в строке %d пропущен. Его данные некорректны.\n", employeesNumber);
                    continue;
                }
                try {
                    id = Long.parseLong(employeeData[0]);
                    salary = new BigDecimal(employeeData[5]);
                    if (salary.compareTo(BigDecimal.ZERO) < 0) {
                        System.out.printf("Работник в строке %d пропущен. Данные о его зарплате некорректны.\n", employeesNumber);
                        continue;
                    }
                } catch (NumberFormatException ex) {
                    System.out.printf("Работник в строке %d пропущен. Его данные некорректны.\n", employeesNumber);
                    continue;
                }
                preparedStatement.setLong(1, id);
                preparedStatement.setString(2, employeeData[1]);
                preparedStatement.setString(3, employeeData[2]);
                preparedStatement.setString(4, employeeData[3]);
                preparedStatement.setString(5, employeeData[4]);
                preparedStatement.setBigDecimal(6, salary);
                preparedStatement.setString(7, employeeData[6]);
                preparedStatement.executeUpdate();
            }
            connection.commit();
        } catch (FileNotFoundException ex) {
            System.out.printf("Файл '%s' не найден.\n", inputFileName);
        } catch (SQLException e) {
            System.out.println("При сохранении данных в БД возникла ошибка. Внесенные данные не будут сохранены.");
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("При отмене транзакции возникла ошибка.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения данных.");
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Неверное количество параметров. Должен быть указан файл с данными.");
            return;
        }
        saveEmployees(args[0]);
    }
}
