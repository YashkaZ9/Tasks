package task1;

import java.io.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Company {
    private String name;

    public class Department {
        private String name;
        private String companyName;
        private Set<Employee> employees;

        public String getName() {
            return name;
        }

        public String getCompanyName() {return companyName;}

        private Department(String name) {
            this.name = name;
            this.companyName = Company.this.name;
            this.employees = new HashSet<>();
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
            return "Department{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    private Map<String, Department> departments;

    public Company(String name, String inputFileName) {
        this.name = name;
        departments = new HashMap<>();
        hireEmployees(inputFileName);
    }

    public String getName() {
        return name;
    }

    private void hireEmployees(String inputFileName) {
        final Integer INPUT_FILE_HEADER_LENGTH = 7;
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            Long id;
            BigDecimal salary;
            br.readLine();
            while (br.ready()) {
                String[] employeeData = br.readLine().split(";");
                if (employeeData.length != INPUT_FILE_HEADER_LENGTH) {
                    continue;
                }
                try {
                    id = Long.parseLong(employeeData[0]);
                    salary = new BigDecimal(employeeData[5]);
                    if (salary.compareTo(BigDecimal.ZERO) < 0
                            || salary.compareTo(BigDecimal.valueOf(100_000_000)) > 0) {
                        continue;
                    }
                } catch (NumberFormatException ex) {
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
            BigDecimal totalSum = BigDecimal.ZERO;
            for (Employee employee : department.employees) {
                totalSum = totalSum.add(employee.getSalary());
            }
            departmentsAverageSalaries.put(department,
                    totalSum.divide(BigDecimal.valueOf(department.employees.size()), 2, RoundingMode.HALF_UP));
        }
        return departmentsAverageSalaries;
    }

    public void printOptimizingEmployeesTransfers(String outputFileName) {
        final String FILE_HEADER = "from_department;from_dep_avg_salary" +
                ";to_department;to_dep_avg_salary;firstName;lastName;salary\n";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName))) {
            HashMap<Department, BigDecimal> departmentsAverageSalaries = getDepartmentsAverageSalaries();
            bw.write(FILE_HEADER);
            for (Department companyDepartment : departments.values()) {
                for (Employee employee : companyDepartment.employees) {
                    for (Department department : departments.values()) {
                        if (!employee.getDepartment().equals(department)
                            && employee.getSalary().compareTo(departmentsAverageSalaries.get(employee.getDepartment())) < 0
                                && employee.getSalary().compareTo(departmentsAverageSalaries.get(department)) > 0) {
                            bw.write(String.format("%s;%.2f;%s;%.2f;%s;%s;%s\n",
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

    public void printEmployees() {
        HashMap<Department, BigDecimal> departmentAverageSalary = getDepartmentsAverageSalaries();
        departments.forEach((name, department) -> {
            System.out.printf("%s: %.2f\n", name, departmentAverageSalary.get(department));
            department.employees.forEach(employee ->  System.out.printf("%-15s %-10s %13.2f\n",
                    employee.getLastName(), employee.getFirstName(), employee.getSalary()));
        });
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                '}';
    }
}
