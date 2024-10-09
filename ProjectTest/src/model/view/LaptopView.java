package model.view;
import model.product.Category;
import model.product.Laptop;
import model.service.LaptopService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LaptopView {
    private UserView userView;
    private Scanner scanner = new Scanner(System.in);
    private LaptopService laptopService;

    //===== CONSTRUCTOR =====
    public LaptopView(UserView userView, LaptopService laptopService) {
        this.userView = userView;
        this.laptopService = laptopService;
    }

    //===== PHÂN LOẠI LAPTOP =====
    public void showCategory() {
        List<String> options = new ArrayList<>();
        options.add(Category.WINDOWS_CATEGORY);
        options.add(Category.APPLE_CATEGORY);
        options.add("All Laptops");
        options.add("Return");
        userView.showMenu("Laptop Menu", options);
    }

    //===== MANAGER LAPTOP =====
    public void manageProduct() {
        List<String> options = new ArrayList<>();
        options.add("add laptop");
        options.add("delete laptop");
        options.add("edit laptop");
        options.add("all laptop had add");
        options.add("return");
        userView.showMenu("Product Menu", options);
    }

    //===== NHẬP THÔNG TIN SẢN PHẨM =====
    public String inputLaptopName() {
        String name;
        do {
            System.out.print("Enter Laptop Name: ");
            name = scanner.nextLine();
            if (name.trim().isEmpty()) {
                System.out.println("Laptop name cannot be empty");
            }
        } while (name.trim().isEmpty());
        return name;
    }

    public String inputLaptopBrand() {
        String brand;
        do {
            System.out.print("Please enter Laptop Brand: ");
            brand = scanner.nextLine();
            if (brand.trim().isEmpty()) {
                System.out.println("Laptop brand cannot be empty.");
            }
        } while (brand.trim().isEmpty());
        return brand;
    }

    public double inputLaptopPrice() {
        double price = 0;
        boolean valid = false;
        while (!valid) {
            try {
                System.out.print("Enter Laptop Price: ");
                price = Double.parseDouble(scanner.nextLine());
                if (price <= 0) {
                    throw new IllegalArgumentException("Laptop price cannot be empty");
                }
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Number invalid");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        return price;
    }

    public int inputLaptopQuantity() {
        int quantity = 0;
        boolean valid = false;
        while (!valid) {
            try {
                System.out.print("Please enter Laptop Quantity: ");
                quantity = Integer.parseInt(scanner.nextLine());
                if (quantity < 0) {
                    throw new IllegalArgumentException("Cannot have negative quantity");
                }
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Number must be integer");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        return quantity;
    }

    public String inputLaptopDescription() {
        System.out.print("Please enter Laptop Description: ");
        return scanner.nextLine();
    }

    //===== HIỂN THỊ CHI TIẾT SẢN PHẨM =====
    public void displayLaptopDetail(int productId) {
        laptopService.getLaptopById(productId).ifPresentOrElse(
                laptop -> System.out.println(laptop),
                () -> System.out.println("Laptop not found")
        );
    }
}
