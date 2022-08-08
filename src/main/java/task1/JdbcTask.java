package task1;

import java.sql.*;
import java.util.Collection;

public class JdbcTask {
    private static final String URL = "jdbc:postgresql://localhost:5432/company";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "123";

    public static void saveEmployees(Collection<Company.Department> departments) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Драйвер СУБД PostgreSQL не найден.");
        }
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement departmentPreparedStatement = connection.prepareStatement(
                    "INSERT INTO departments(name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            PreparedStatement employeePreparedStatement = connection.prepareStatement(
                    "INSERT INTO employees(first_name, last_name, gender, email, salary, department_id) VALUES (?, ?, ?, ?, ?, ?)"
            );
            for (Company.Department department : departments) {
                departmentPreparedStatement.setString(1, department.getName());
                departmentPreparedStatement.executeUpdate();
                ResultSet resultSet = departmentPreparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    department.setId(resultSet.getInt(1));
                    for (Employee employee : department.getEmployees()) {
                        employeePreparedStatement.setString(1, employee.getFirstName());
                        employeePreparedStatement.setString(2, employee.getLastName());
                        employeePreparedStatement.setString(3, employee.getGender());
                        employeePreparedStatement.setString(4, employee.getEmail());
                        employeePreparedStatement.setBigDecimal(5, employee.getSalary());
                        employeePreparedStatement.setInt(6, department.getId());
                        employeePreparedStatement.executeUpdate();
                    }
//                    Проверка транзакционности
//                    Thread.sleep(5000);
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            System.out.println("При сохранении данных в БД возникла ошибка.");
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Неверное количество параметров. Должен быть указан файл с данными.");
            return;
        }
        Company company = new Company("Т1 Консалтинг");
        company.hireEmployees(args[0]);
        Collection<Company.Department> departments = company.getDepartments().values();
        saveEmployees(departments);
    }
}
