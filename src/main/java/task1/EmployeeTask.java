package task1;

public class EmployeeTask {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Неверное количество параметров. " +
                    "Их должно быть два: [inputFileName] [outputFileName].");
            return;
        }
        Company company = new Company("Т1 Консалтинг");
        company.hireEmployees(args[0]);
//        company.printEmployees();
//        company.findOptimizingEmployeesTransfers(args[1]);
        company.findOptimizingGroupEmployeesTransfers(args[1]);
    }
}