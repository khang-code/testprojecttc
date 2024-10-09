package model.service;
import model.cart.Cart;
import model.cart.CartItem;
import model.user.User;
import java.io.*;
import java.util.*;

public class CartService {
    private Map<String, Cart> carts;

    private static final String FILE_NAME = "C:\\Users\\DELL\\IdeaProjects\\ProjectTest\\src\\model\\stored\\carts.csv";

    public CartService() {
        carts = new HashMap<>();
        carts = readCartsFromFile();
    }

    public Cart getOrCreateCart(User buyer) {
        String username = buyer.getUsername();
        if (!carts.containsKey(username)) {
            carts.put(username, new Cart(username));
        }
        return carts.get(username);
    }

    public Cart getCart(String username) {
        return carts.get(username);
    }

    public void updateCart(String username, CartItem item, boolean isAdding) {
        Cart cart = carts.get(username);

        if (cart != null) {
            if (isAdding) {
                cart.addItem(item);
                System.out.println("Product has been added to the cart.");
            } else {
                cart.removeItemByProductId(item.getProductId());
                System.out.println("Product has been removed from the cart.");
            }

            writeCartsToFile(new ArrayList<>(carts.values()));
        } else {
            System.out.println("Cart not found.");
        }
    }

    public void printCart(String username) {
        Cart cart = carts.get(username);
        if (cart != null && !cart.getItems().isEmpty()) {
            System.out.println("====== CART OF USER: ' " + username + " ' ======");
            for (CartItem item : cart.getItems()) {
                System.out.println("Product ID: " + item.getProductId() + " - Product Name: " + item.getProductName() +
                        " - Quantity: " + item.getQuantity() + " - Price: " + item.getPrice());
            }
            System.out.println("Total Cart Value: " + cart.getTotalCartValue());
        } else {
            System.out.println("Your cart is empty.");
        }
    }

    public void clearCart(String username) {
        Cart cart = carts.get(username);

        if (cart != null && !cart.getItems().isEmpty()) {
            cart.clearCart();
            System.out.println("All products have been removed from your cart...");
        } else {
            System.out.println("The cart is empty or cart not found.");
        }
        writeCartsToFile(new ArrayList<>(carts.values()));
    }

    public Map<String, Cart> getAllCartsForAdminOrSeller() {
        return new HashMap<>(carts);
    }

    public void writeCartsToFile(List<Cart> carts) {
        carts.sort(Comparator.comparing(Cart::getUsername));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(String.format("%-15s | %-10s | %-20s | %-10s | %-10s%n",
                    "Username", "ProductId", "ProductName", "Quantity", "Price"));

            for (Cart cart : carts) {
                for (CartItem item : cart.getItems()) {
                    writer.write(String.format("%-15s | %-10d | %-20s | %-10d | %-10.2f%n",
                            cart.getUsername(),
                            item.getProductId(),
                            item.getProductName(),
                            item.getQuantity(),
                            item.getPrice()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing data to file: " + FILE_NAME);
            e.printStackTrace();
        }
    }

    // Read cart data from the CSV file
    public Map<String, Cart> readCartsFromFile() {
        Map<String, Cart> carts = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = reader.readLine(); // Skip the header

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");

                if (data.length != 5) {
                    System.err.println("Invalid data: " + line);
                    continue;
                }

                String username = data[0].trim();
                int productId = Integer.parseInt(data[1].trim());
                String productName = data[2].trim();
                int quantity = Integer.parseInt(data[3].trim());
                double price = Double.parseDouble(data[4].trim());

                CartItem item = new CartItem(productId, productName, quantity, price);
                carts.computeIfAbsent(username, k -> new Cart(username)).addItem(item);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Error reading data from file: " + FILE_NAME);
            e.printStackTrace();
        }

        return carts;
    }
}
