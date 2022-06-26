package task3;

import java.util.Random;
import java.util.concurrent.Phaser;

public class Customer extends Thread {
    private final Warehouse warehouse;
    private final Phaser purchasesLimiter;
    private final Random choice;
    private long purchasesCount;
    private long productsCount;

    public Customer(Warehouse warehouse, Phaser purchasesLimiter) {
        this.warehouse = warehouse;
        this.purchasesLimiter = purchasesLimiter;
        this.purchasesLimiter.register();
        this.choice = new Random();
        this.purchasesCount = 0;
        this.productsCount = 0;
    }

    public void buyProducts() {
        long desirableProductsCount = 1 + choice.nextInt(10);
        long buyingProductsCount = warehouse.sellProducts(desirableProductsCount);
        productsCount += buyingProductsCount;
        purchasesCount += buyingProductsCount > 0 ? 1 : 0;
    }

    @Override
    public void run() {
        while (warehouse.isOpen()) {
            buyProducts();
            purchasesLimiter.arriveAndAwaitAdvance();
        }
        System.out.printf("%s: совершено покупок - %d, куплено товаров - %d\n",
                getName(), purchasesCount, productsCount);
        purchasesLimiter.arriveAndDeregister();
    }
}
