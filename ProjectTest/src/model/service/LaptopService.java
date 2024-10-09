package model.service;
import model.cart.CartItem;
import model.cart.Cart;
import model.product.Category;
import model.product.Product;
import model.product.Laptop;
import model.util.SessionManager;
import model.view.UserView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LaptopService {
    private static final String PRODUCT_FILE_PATH = "C:\\Users\\DELL\\IdeaProjects\\ProjectTest\\src\\model\\stored\\products.csv";

    //===== ĐỊNH NGHĨA THUỘC TÍNH =====
    private UserView userView;
    private Map<String, Laptop> laptops = new HashMap<>();
    private Category windowsCategory;
    private Category appleCategory;
    private CartService cartService;

    //===== CONSTRUCTOR =====
    public LaptopService(UserView userView, CartService cartService) {
        this.userView = userView;
        this.cartService = cartService;
        this.windowsCategory = new Category(Category.WINDOWS_CATEGORY);
        this.appleCategory = new Category(Category.APPLE_CATEGORY);

        Map<String, Laptop> loadedLaptops = readProductsFromFile();
        if (!loadedLaptops.isEmpty()) {
            this.laptops = loadedLaptops;
            categorizeLaptops();
        }

        // Tìm productId lớn nhất từ các sản phẩm hiện có và thiết lập lại idCounter
        int maxProductId = laptops.values().stream()
                .mapToInt(Laptop::getProductId)
                .max()
                .orElse(0);

        // Thiết lập lại giá trị cho idCounter
        Laptop.setIdCounter(maxProductId);
    }

    //===== PHÂN LOẠI LAPTOP =====
    private void categorizeLaptops() {
        for (Laptop laptop : laptops.values()) {
            if (Category.isWindowsBrand(laptop.getBrand())) {
                windowsCategory.addLaptopToCategory(laptop);
            } else if (Category.isAppleBrand(laptop.getBrand())) {
                appleCategory.addLaptopToCategory(laptop);
            }
        }
    }

    //===== LẤY LAPTOP THEO ID =====
    public Optional<Laptop> getLaptopById(int productId) {
        Laptop laptop = laptops.get(String.valueOf(productId));
        return Optional.ofNullable(laptop);
    }

    //===== LẤY LAPTOP THEO DANH MỤC (Category) =====
    public void displayLaptopsByCategory(String category) {
        if (category.equalsIgnoreCase(Category.WINDOWS_CATEGORY)) {
            windowsCategory.displayLaptopsInCategory();
            handlePurchase(category);
        } else if (category.equalsIgnoreCase(Category.APPLE_CATEGORY)) {
            appleCategory.displayLaptopsInCategory();
            handlePurchase(category);
        } else {
            userView.showMessage("List not exist.");
            return;
        }
    }

    // Lựa chọn sản phẩm theo danh mục
    private void handlePurchase(String category) {
        try {
            String response = userView.getInput("Do you want to buy any item in this (yes/no): ");

            if (response.equalsIgnoreCase("yes")) {

                String productId = userView.getInput("Enter id item you want to buy ");
                Optional<Laptop> selectedProductOpt = getLaptopById(Integer.parseInt(productId));

                if (selectedProductOpt.isPresent()) {
                    Laptop laptop = selectedProductOpt.get();

                    // Kiểm tra số lượng hàng tồn kho
                    if (laptop.getQuantity() <= 0) {
                        System.out.println("This Item is run out of stock.");
                        return;
                    }

                    // Nhập số lượng sản phẩm
                    int quantity;
                    while (true) {
                        try {
                            quantity = Integer.parseInt(userView.getInput("Enter number you want to buy "));
                            if (quantity > 0 && quantity <= laptop.getQuantity()) {
                                break;
                            } else {
                                System.out.println("Invalid quantity. The maximum quantity that can be purchased is:" + laptop.getQuantity());
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter valid number");
                        }
                    }

                    // Thêm sản phẩm vào giỏ hàng
                    CartItem item = new CartItem(laptop.getProductId(), laptop.getName(), quantity, laptop.getPrice());
                    cartService.updateCart(SessionManager.getCurrentUser().getUsername(), item, true);

                    // Giảm số lượng sản phẩm trong kho
                    decreaseQuantity(laptop.getProductId(), quantity);

                } else {
                    System.out.println("Id item not found.");
                }
            } else {
                System.out.println("You have chosen not to purchase the product.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid product code or quantity format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Got error " + e.getMessage());
        }
    }

    //===== HIỂN THỊ TẤT CẢ LAPTOP =====
    public void displayAllProducts() {
        if (laptops.isEmpty()) {
            System.out.println("empty list.");
        } else {
            List<Laptop> sortedLaptops = laptops.values().stream()
                    .sorted(Comparator.comparingInt(Laptop::getProductId))
                    .toList();

            System.out.println("====== All product ======");
            for (Laptop laptop : sortedLaptops) {
                System.out.println(laptop);
            }
        }
    }

    //================= QUẢN LÝ SẢN PHẨM =================
    public void addLaptop(Laptop laptop, String categoryName) {
        if (laptops.containsKey(String.valueOf(laptop.getProductId()))) {
            System.out.println("Item with ID " + laptop.getProductId() + " already exists. cannot add more");
        } else {
            if (categoryName.equals(Category.APPLE_CATEGORY) && Category.isAppleBrand(laptop.getBrand())) {
                appleCategory.addLaptopToCategory(laptop);
                System.out.println("Have add item into Apple category.");
            } else {
                windowsCategory.addLaptopToCategory(laptop);
                System.out.println("have add item into Windows category.");
            }

            // Thêm vào danh sách sản phẩm
            laptops.put(String.valueOf(laptop.getProductId()), laptop);
            writeProductToFile(laptops);
            System.out.println("has add item with ID " + laptop.getProductId() + " into list and save into file.");
        }
    }

    public void removeLaptop(Laptop laptop) {
        if (laptop != null) {
            if (Category.isWindowsBrand(laptop.getBrand())) {
                windowsCategory.getLaptops().remove(laptop);
            } else if (Category.isAppleBrand(laptop.getBrand())) {
                appleCategory.getLaptops().remove(laptop);
            }

            CartItem cartItem = new CartItem(laptop.getProductId(), laptop.getName(), laptop.getQuantity(), laptop.getPrice());
            laptops.remove(String.valueOf(laptop.getProductId()));

            Map<String, Cart> carts = cartService.getAllCartsForAdminOrSeller();
            for (String username : carts.keySet()) {
                Cart cart = carts.get(username);
                if (cart != null && cart.containsProduct(laptop.getProductId())) {
                    cartService.updateCart(username, cartItem, false);
                }
            }
            writeProductToFile(laptops);
            System.out.println("item with ID " + laptop.getProductId() + " has delete.");
        } else {
            System.out.println("this laptop not exist.");
        }
    }

    public void updateLaptop(int productId, String name, String brand, double price, int quantity, String description) {
        Laptop laptop = laptops.get(String.valueOf(productId));

        if (laptop != null) {
            if (name != null && !name.isEmpty()) {
                laptop.setName(name);
            }
            if (brand != null && !brand.isEmpty()) {
                laptop.setBrand(brand);
            }
            if (price > 0) {
                laptop.setPrice(price);
            }
            if (quantity >= 0) {
                laptop.setQuantity(quantity);
            }
            if (description != null && !description.isEmpty()) {
                laptop.setSpecifications(description);
            }

            if (!laptop.getBrand().equalsIgnoreCase(brand)) {
                if (Category.isWindowsBrand(laptop.getBrand())) {
                    windowsCategory.getLaptops().remove(laptop);
                } else if (Category.isAppleBrand(laptop.getBrand())) {
                    appleCategory.getLaptops().remove(laptop);
                }

                if (Category.isWindowsBrand(brand)) {
                    System.out.println("Add List Window complete");
                    windowsCategory.addLaptopToCategory(laptop);
                } else if (Category.isAppleBrand(brand)) {
                    System.out.println("Add List Apple complete");
                    appleCategory.addLaptopToCategory(laptop);
                }
            }

            laptops.put(String.valueOf(productId), laptop);
            writeProductToFile(laptops);
            System.out.println("Updated product information with ID " + productId + " completed");
        } else {
            System.out.println("item with ID " + productId + " not exist.");
        }
    }

    // Tăng số lượng sản phẩm
    public void increaseQuantity(int productId, int amount) {
        Laptop laptop = laptops.get(String.valueOf(productId));
        if (laptop != null) {
            laptop.setQuantity(laptop.getQuantity() + amount);
            writeProductToFile(laptops);
        } else {
            System.out.println("item not exist.");
        }
    }

    // Giảm số lượng sản phẩm
    public void decreaseQuantity(int productId, int amount) {
        Laptop laptop = laptops.get(String.valueOf(productId));
        if (laptop != null) {
            int newQuantity = laptop.getQuantity() - amount;
            if (newQuantity >= 0) {
                laptop.setQuantity(newQuantity);
                writeProductToFile(laptops);
            } else {
                System.out.println("Cannot reduce quantity more");
            }
        } else {
            System.out.println("This item not exist.");
        }
    }

    /**********   Handle File   ***********/
    // Ghi dữ liệu sản phẩm vào file CSV
    public void writeProductToFile(Map<String, Laptop> laptops) {
        try (FileWriter writer = new FileWriter(PRODUCT_FILE_PATH)) {
            writer.write("ProductId|Name|Brand|Price|Quantity|Description\n");

            List<Laptop> sortedLaptops = laptops.values().stream()
                    .sorted(Comparator.comparingInt(Laptop::getProductId))
                    .toList();

            for (Laptop laptop : sortedLaptops) {
                writer.write(String.format("%d|%s|%s|%.2f|%d|%s%n",
                        laptop.getProductId(),
                        laptop.getName(),
                        laptop.getBrand(),
                        laptop.getPrice(),
                        laptop.getQuantity(),
                        laptop.getSpecifications()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Đọc dữ liệu sản phẩm từ file CSV
    public Map<String, Laptop> readProductsFromFile() {
        Map<String, Laptop> laptops = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCT_FILE_PATH))) {
            String line;
            reader.readLine(); // Bỏ qua tiêu đề

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] split = line.split("\\|");
                if (split.length != 6) {
                    continue;
                }

                try {
                    int productId = Integer.parseInt(split[0].trim());
                    String name = split[1].trim();
                    String brand = split[2].trim();
                    double price = Double.parseDouble(split[3].trim());
                    int quantity = Integer.parseInt(split[4].trim());
                    String description = split[5].trim();

                    Laptop laptop = new Laptop(productId, name, brand, price, quantity, description);
                    laptops.put(String.valueOf(productId), laptop);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return laptops;
    }
}