package model.cart;

public class CartItem {
    private int productId;
    private String productName;
    private int quantity;
    private double price;

    // Constructor
    public CartItem(int productId, String productName, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getter cho productId
    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalPrice() {
        return quantity * price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
