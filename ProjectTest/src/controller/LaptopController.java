package controller;
import model.product.Laptop;
import model.service.LaptopService;
import model.view.*;

public class LaptopController {

    //===== DEFINE ATTRIBUTES =====
    private LaptopView laptopView;
    private LaptopService laptopService;
    private UserView userView;

    //===== CONSTRUCTOR =====
    public LaptopController(LaptopView laptopView, LaptopService laptopService, UserView userView) {
        this.laptopView = laptopView;
        this.laptopService = laptopService;
        this.userView = userView;
    }

    //===== PRODUCT MANAGEMENT MENU =====
    public void showProductManagementMenu() {
        while (true) {
            laptopView.manageProduct();
            String choice = userView.getInput("Your choice: ");

            switch (choice) {
                case "1":
                    addNewLaptop();
                    break;
                case "2":
                    removeLaptop();
                    break;
                case "3":
                    updateLaptop();
                    break;
                case "4":
                    laptopService.displayAllProducts();
                    break;
                case "5":
                    return;
                default:
                    userView.showMessage("Invalid choice. Please try again.");
            }
        }
    }

    //===== MANAGE LAPTOP =====
    private void addNewLaptop() {
        // Get product information from the user
        String name = laptopView.inputLaptopName();
        String brand = laptopView.inputLaptopBrand();
        double price = laptopView.inputLaptopPrice();
        int quantity = laptopView.inputLaptopQuantity();
        String description = laptopView.inputLaptopDescription();

        // Create a new Laptop object
        Laptop newLaptop = new Laptop(name, brand, price, quantity, description);

        String category = brand.equalsIgnoreCase("Apple") ? "Apple" : "Windows";

        laptopService.addLaptop(newLaptop, category);
    }

    private void removeLaptop() {
        int productId = Integer.parseInt(userView.getInput("Enter product ID: "));
        laptopService.getLaptopById(productId).ifPresentOrElse(
                laptop -> {
                    laptopView.displayLaptopDetail(productId);
                    if (userView.confirmAction("Are you sure you want to delete this product?")) {
                        laptopService.removeLaptop(laptop);
                    }
                },
                () -> userView.showMessage("Product does not exist.")
        );
    }

    private void updateLaptop() {
        int productId = Integer.parseInt(userView.getInput("Enter product ID: "));

        laptopService.getLaptopById(productId).ifPresentOrElse(
                laptop -> {
                    String name = laptopView.inputLaptopName();
                    String brand = laptopView.inputLaptopBrand();
                    double price = laptopView.inputLaptopPrice();
                    int quantity = laptopView.inputLaptopQuantity();
                    String description = laptopView.inputLaptopDescription();

                    laptopService.updateLaptop(productId, name, brand, price, quantity, description);
                    userView.showMessage("Product has been successfully updated.");
                },
                () -> userView.showMessage("Product does not exist.")
        );
    }

    //===== DISPLAY PRODUCTS BY CATEGORY =====
    public void showCategoryLaptops() {
        while (true) {
            laptopView.showCategory();
            String choice = userView.getInput("Your choice: ");

            switch (choice) {
                case "1":
                    laptopService.displayLaptopsByCategory("Windows");
                    break;
                case "2":
                    laptopService.displayLaptopsByCategory("Apple");
                    break;
                case "3":
                    laptopService.displayAllProducts();
                    break;
                case "4":
                    return;
                default:
                    userView.showMessage("Invalid choice. Please try again.");
            }
        }
    }
}
