package task3;

import java.util.concurrent.Exchanger;

public class PurchaseTask {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Должно быть указано количество покупателей.");
            return;
        }
        int customersCount = 0;
        try {
            customersCount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Передан параметр неверного типа.");
        }
        Warehouse warehouse = new Warehouse();
        for (int i = 0; i < customersCount; ++i) {
            new Customer(warehouse, customersCount).start();
        }
    }
}
