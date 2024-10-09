package model.cart;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Cart {
    private String username;
    private List<CartItem> items;

    public Cart(String username) {
        this.username = username;
        this.items = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<CartItem> getItems() {
        return items;
    }

    // Thêm sản phẩm vào giỏ hàng
    public void addItem(CartItem item) {
        Optional<CartItem> existingItem = getItemByProductId(item.getProductId());
        if (existingItem.isPresent()) {
            // Nếu sản phẩm đã có trong giỏ, tăng số lượng
            existingItem.get().setQuantity(existingItem.get().getQuantity() + item.getQuantity());
        } else {
            // Nếu sản phẩm chưa có, thêm vào giỏ
            items.add(item);
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng theo productId
    public void removeItemByProductId(int productId) {
        items.removeIf(item -> item.getProductId() == productId);
    }

    // Tính tổng giá trị của giỏ hàng
    public double getTotalCartValue() {
        return items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    // Tìm sản phẩm trong giỏ theo productId
    public Optional<CartItem> getItemByProductId(int productId) {
        return items.stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst();
    }

    public void clearCart() {
        items.clear();
        System.out.println("Tất cả sản phẩm đã được xóa khỏi giỏ hàng.");
    }

    public boolean containsProduct(int productId) {
        for (CartItem item : items) {
            if (item.getProductId() == productId) {
                return true;
            }
        }
        return false;
    }
}
