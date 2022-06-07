package task3;

public class Warehouse {
    private final int initialProductsCount = 1000;
    private int productsCount = initialProductsCount;

    public int getProductsCount() {
        return productsCount;
    }

    public int getInitialProductsCount() {
        return initialProductsCount;
    }

    public void buyProducts(int productsCount) {
        this.productsCount -= productsCount;
    }
}
