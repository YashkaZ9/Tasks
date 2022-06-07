package task3;

import java.util.Random;

public class Customer extends Thread {
    private final Warehouse warehouse;
    private final int maxProductsCount;
    private final Random random;
    private int purchasesCount;
    private int productsCount;

    public Customer(Warehouse warehouse, int customersCount) {
        this.warehouse = warehouse;
        this.maxProductsCount = (int)Math.ceil(1.0 * warehouse.getInitialProductsCount() / customersCount);
        this.random = new Random();
        this.purchasesCount = 0;
        this.productsCount = 0;
    }

    @Override
    public void run() {
        synchronized (warehouse) {
            while (warehouse.getProductsCount() > 0) {
                int productsToBuyCount = Math.min(warehouse.getProductsCount(), 1 + random.nextInt(10));
                if (productsCount + productsToBuyCount <= maxProductsCount) {
                    warehouse.buyProducts(productsToBuyCount);
                    purchasesCount++;
                    productsCount += productsToBuyCount;
                }
                try {
                    warehouse.notifyAll();
                    warehouse.wait(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.printf("%s: куплено товаров - %d, совершено покупок - %d\n",
                    getName(), productsCount, purchasesCount);
        }
    }
}
