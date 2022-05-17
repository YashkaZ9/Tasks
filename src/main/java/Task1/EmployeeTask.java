package Task1;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeTask {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Неправильное количество параметров. Их должно быть два.");
            return;
        }
        HashMap<String, List<Employee>> departmentEmployees = getDepartmentEmployees(args[0]);
        HashMap<String, Double> departmentAverageSalary = getDepartmentAverageSalary(departmentEmployees);
        printEmployees(departmentEmployees, departmentAverageSalary);
        getOptimizingEmployeeTransfers(args[1], departmentEmployees, departmentAverageSalary);
    }

    public static HashMap<String, List<Employee>> getDepartmentEmployees(String inputFileName) {
        HashMap<String, List<Employee>> departmentEmployees = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            br.readLine();
            while (br.ready()) {
                String[] employeeData = br.readLine().split("\t");
                String department = employeeData[6];
                Employee employee = new Employee(
                        Long.parseLong(employeeData[0]),
                        employeeData[1],
                        employeeData[2],
                        employeeData[3],
                        employeeData[4],
                        Double.parseDouble(employeeData[5].substring(1)),
                        employeeData[6]
                );
                if (!departmentEmployees.containsKey(department)) {
                    departmentEmployees.put(department, new ArrayList<>());
                }
                departmentEmployees.get(department).add(employee);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Файл имеет неправильный формат.");
        }
        return departmentEmployees;
    }

    public static HashMap<String, Double> getDepartmentAverageSalary(HashMap<String, List<Employee>> departmentEmployees) {
        HashMap<String, Double> departmentAverageSalary = new HashMap<>();
        departmentEmployees.forEach((department, employees) ->
                departmentAverageSalary.put(department, employees.stream()
                        .mapToDouble(Employee::getSalary)
                        .average().orElse(0)));
        return departmentAverageSalary;
    }

    public static void printEmployees(HashMap<String, List<Employee>> departmentEmployees,
                                      HashMap<String, Double> departmentAverageSalary) {
        departmentEmployees.forEach((department, employees) -> {
            System.out.printf("%s: %.3f $\n", department, departmentAverageSalary.get(department));
            employees.forEach(System.out::println);
        });
    }

    public static void getOptimizingEmployeeTransfers(String outputFileName,
                                                   HashMap<String, List<Employee>> departmentEmployees,
                                                   HashMap<String, Double> departmentAverageSalary) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName))) {
            bw.write("id\tfirstName\tlastName\tsalary\tfrom_department\tto_department\n");
            for (Employee employee : departmentEmployees.values().stream()
                    .flatMap(Collection::stream).collect(Collectors.toList())) {
                for (String department : departmentEmployees.keySet()) {
                    if (employee.getSalary() < departmentAverageSalary.get(employee.getDepartment())
                        && employee.getSalary() > departmentAverageSalary.get(department)) {
                        bw.write(String.format("%d\t%s\t%s\t$%s\t%s\t%s\n",
                                employee.getId(),
                                employee.getFirstName(),
                                employee.getLastName(),
                                employee.getSalary(),
                                employee.getDepartment(),
                                department));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Файл для записи поврежден.");
        }
    }
}
