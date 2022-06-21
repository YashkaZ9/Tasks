package task3;

public class Warehouse {
    private final long initialProductsCount = 1000;
    private long productsCount = initialProductsCount;

    public long getInitialProductsCount() {
        return initialProductsCount;
    }

    public long getProductsCount() {
        return productsCount;
    }

    public boolean isOpen() {
        return productsCount > 0;
    }

    public long sellProducts(long desirableProductsCount) {
        long productsToBeSold = Math.min(productsCount, desirableProductsCount);
        this.productsCount -= productsToBeSold;
        return productsToBeSold;
    }
}
