package model.product;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class Laptop implements Product {
    //===== ĐỊNH NGHĨA THUỘC TÍNH =====
private static AtomicInteger idCounter = new AtomicInteger();
private int productId;
private String name;
private String brand;
private double price;
private String specifications;
private int quantity;

//===== CONSTRUCTOR thêm sản phẩm mới =====
public Laptop(String name, String brand, double price, int quantity, String specifications) {
    this.productId = idCounter.incrementAndGet();
    setName(name);
    setBrand(brand);
    setPrice(price);
    setQuantity(quantity);
    this.specifications = specifications;
}

//===== CONSTRUCTOR đọc từ file =====
public Laptop(int productId, String name, String brand, double price, int quantity, String specifications) {
    this.productId = productId;
    setName(name);
    setBrand(brand);
    setPrice(price);
    this.specifications = specifications;
    setQuantity(quantity);

    if (productId > idCounter.get()) {
        idCounter.set(productId);
    }
}

// Trong lớp Laptop
public static void setIdCounter(int maxProductId) {
    idCounter.set(maxProductId);
}

@Override
public int getProductId() {
    return productId;
}

@Override
public String getName() {
    return name;
}

@Override
public void setName(String name) {
    if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException("Tên sản phẩm không thể để trống.");
    }
    this.name = name;
}

@Override
public String getBrand() {
    return brand;
}

@Override
public void setBrand(String brand) {
    if (brand == null || brand.trim().isEmpty()) {
        throw new IllegalArgumentException("Thương hiệu không thể để trống.");
    }
    this.brand = brand;
}

@Override
public double getPrice() {
    return price;
}

@Override
public void setPrice(double price) {
    if (price < 0) {
        throw new IllegalArgumentException("Giá không thể nhỏ hơn 0.");
    }
    this.price = price;
}

@Override
public int getQuantity() {
    return quantity;
}

@Override
public void setQuantity(int quantity) {
    if (quantity < 0) {
        throw new IllegalArgumentException("Số lượng không thể nhỏ hơn 0.");
    }
    this.quantity = quantity;
}

public String getSpecifications() {
    return specifications;
}

public void setSpecifications(String specifications) {
    this.specifications = specifications;
}



@Override
public String toString() {
    // Định dạng giá tiền theo chuẩn địa phương (viết theo dạng tiền tệ của Việt Nam)
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    String formattedPrice = currencyFormat.format(price);

    return String.format("Laptop{Mã số: %d |" +
                    " Product name: '%s' |" +
                    " Branch: '%s' |" +
                    " Cost: %s |" +  // Thay vì %.2f, sử dụng định dạng tiền tệ
                    " Number: %d |" +
                    " Description: '%s'}",
            productId, name, brand, formattedPrice, quantity, specifications);
}
}
