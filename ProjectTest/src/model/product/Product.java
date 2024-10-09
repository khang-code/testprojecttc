package model.product;

public interface Product {
    String getName();
    void setName(String name);

    String getBrand();
    void setBrand(String brand);

    double getPrice();
    void setPrice(double price);

    int getProductId();

    int getQuantity();
    void setQuantity(int quantity);

    String toString();
}