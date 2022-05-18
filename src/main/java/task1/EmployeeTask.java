package task1;

public class EmployeeTask {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Неверное количество параметров. " +
                    "Их должно быть два: [inputFileName] [outputFileName].");
            return;
        }
        Company company = new Company("Т1 Консалтинг", args[0]);
        company.printEmployees();
        company.printOptimizingEmployeesTransfers(args[1]);
    }
}
