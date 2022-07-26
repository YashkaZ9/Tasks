package task1;

import java.io.*;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Company {
    public static final String EMPLOYEES_TRANSFERS_FILE_HEADER = "from_department;from_dep_avg_salary" +
            ";to_department;to_dep_avg_salary;firstName;lastName;salary";
    public static final Integer EMPLOYEES_FILE_HEADER_LENGTH = 7;
    public static final String GROUPS_TRANSFERS_FILE_HEADER = "from_department;from_dep_avg_salary" +
            ";to_department;to_dep_avg_salary;employees;group_avg_salary";

    private String name;
    private Map<String, Department> departments;

    public class Department {
        private String name;
        private String companyName;
        private List<Employee> employees;

        private Department(String name) {
            this.name = name;
            this.companyName = Company.this.name;
            this.employees = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public String getCompanyName() {
            return companyName;
        }

        public BigDecimal getDepartmentAverageSalary() {
            BigDecimal totalSum = BigDecimal.ZERO;
            for (Employee employee : employees) {
                totalSum = totalSum.add(employee.getSalary());
            }
            BigDecimal avgSalary = BigDecimal.ZERO;
            if (employees.size() != 0) {
                avgSalary = totalSum.divide(BigDecimal.valueOf(employees.size()), 2, RoundingMode.HALF_UP);
            }
            return avgSalary;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Department)) return false;

            Department that = (Department) o;

            return getName().equals(that.getName());
        }

        @Override
        public int hashCode() {
            return getName().hashCode();
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public Company(String name) {
        this.name = name;
        departments = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void hireEmployees(String inputFileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            Long id;
            Long employeesNumber = 1L;
            BigDecimal salary;
            br.readLine();
            while (br.ready()) {
                String[] employeeData = br.readLine().split(";");
                employeesNumber++;
                if (employeeData.length != EMPLOYEES_FILE_HEADER_LENGTH) {
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
                String departmentName = employeeData[6];
                departments.putIfAbsent(departmentName, new Department(departmentName));
                Employee employee = new Employee(
                        id,
                        employeeData[1],
                        employeeData[2],
                        employeeData[3],
                        employeeData[4],
                        salary,
                        departments.get(departmentName)
                );
                departments.get(departmentName).employees.add(employee);
            }
        } catch (FileNotFoundException ex) {
            System.out.printf("Файл '%s' не найден.\n", inputFileName);
        } catch (Exception e) {
            System.out.println("Ошибка чтения данных.");
        }
    }

    public HashMap<Department, BigDecimal> getDepartmentsAverageSalaries() {
        HashMap<Department, BigDecimal> departmentsAverageSalaries = new HashMap<>();
        for (Department department : departments.values()) {
            departmentsAverageSalaries.put(department, department.getDepartmentAverageSalary());
        }
        return departmentsAverageSalaries;
    }

    public void findOptimizingEmployeesTransfers(String outputFileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName))) {
            HashMap<Department, BigDecimal> departmentsAverageSalaries = getDepartmentsAverageSalaries();
            bw.write(EMPLOYEES_TRANSFERS_FILE_HEADER);
            for (Department companyDepartment : departments.values()) {
                for (Employee employee : companyDepartment.employees) {
                    for (Department department : departments.values()) {
                        if (!employee.getDepartment().equals(department)
                                && employee.getSalary().compareTo(departmentsAverageSalaries.get(employee.getDepartment())) < 0
                                && employee.getSalary().compareTo(departmentsAverageSalaries.get(department)) > 0) {
                            bw.write(String.format("\n%s;%.2f;%s;%.2f;%s;%s;%s",
                                    employee.getDepartment().getName(),
                                    departmentsAverageSalaries.get(employee.getDepartment()),
                                    department.getName(),
                                    departmentsAverageSalaries.get(department),
                                    employee.getFirstName(),
                                    employee.getLastName(),
                                    employee.getSalary()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка записи.");
        }
    }

    public void findOptimizingGroupEmployeesTransfers(String outputFileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName))) {
            HashMap<Department, BigDecimal> departmentsAverageSalaries = getDepartmentsAverageSalaries();
            bw.write(GROUPS_TRANSFERS_FILE_HEADER);
            for (Department employeeDepartment : departments.values()) {
                for (Department department : departments.values()) {
                    if (department.equals(employeeDepartment)) continue;
                    findOptimizingGroupEmployeesTransfersByDepartment(bw, departmentsAverageSalaries, 0,
                            new ArrayList<>(), BigDecimal.ZERO, employeeDepartment, department);
                }
            }
        } catch (IOException e) {
            System.out.println("Файл для записи не найден или поврежден.");
        }
    }

    private void findOptimizingGroupEmployeesTransfersByDepartment(BufferedWriter bw,
                                                                   HashMap<Department, BigDecimal> departmentsAverageSalaries,
                                                                   int seenEmployeeIdx,
                                                                   List<Employee> curGroup,
                                                                   BigDecimal curGroupSum,
                                                                   Department curDepartment,
                                                                   Department toDepartment) throws IOException {
        BigDecimal curGroupAvg = BigDecimal.ZERO;
        if (curGroup.size() != 0) {
            curGroupAvg = curGroupSum.divide(BigDecimal.valueOf(curGroup.size()), 2, RoundingMode.HALF_UP);
        }
        if (curGroupAvg.compareTo(departmentsAverageSalaries.get(curDepartment)) < 0
                && curGroupAvg.compareTo(departmentsAverageSalaries.get(toDepartment)) > 0) {
            bw.write(String.format("\n%s;%.2f;%s;%.2f;",
                    curDepartment,
                    departmentsAverageSalaries.get(curDepartment),
                    toDepartment.getName(),
                    departmentsAverageSalaries.get(toDepartment)));
            bw.write(curGroup.toString());
            bw.write(String.format(";%.2f", curGroupAvg));
        }
        for (int curEmployeeIdx = seenEmployeeIdx; curEmployeeIdx < curDepartment.employees.size(); ++curEmployeeIdx) {
            Employee employee = curDepartment.employees.get(curEmployeeIdx);
            curGroup.add(employee);
            curGroupSum = curGroupSum.add(employee.getSalary());
            findOptimizingGroupEmployeesTransfersByDepartment(bw, departmentsAverageSalaries,
                    curEmployeeIdx + 1, curGroup, curGroupSum, curDepartment, toDepartment);
            curGroupSum = curGroupSum.subtract(employee.getSalary());
            curGroup.remove(curGroup.size() - 1);
        }
    }

    public void printEmployees() {
        departments.forEach((name, department) -> {
            System.out.printf("%s: %.2f\n", name, department.getDepartmentAverageSalary());
            department.employees.forEach(employee -> System.out.printf("%-15s %-10s %13.2f\n",
                    employee.getLastName(), employee.getFirstName(), employee.getSalary()));
        });
    }

    @Override
    public String toString() {
        return name;
    }
}
