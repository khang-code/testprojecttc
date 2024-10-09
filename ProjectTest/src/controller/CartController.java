package controller;
import model.cart.Cart;
import model.cart.CartItem;
import model.product.Laptop;
import model.user.Role;
import model.user.User;
import model.service.CartService;
import model.service.LaptopService;
import model.util.SessionManager;
import model.view.CartView;
import model.view.UserView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CartController {

    private CartService cartService;
    private CartView cartView;
    private UserView userView;
    private LaptopService laptopService;

    public CartController(CartService cartService, CartView cartView, UserView userView, LaptopService laptopService) {
        this.cartService = cartService;
        this.cartView = cartView;
        this.userView = userView;
        this.laptopService = laptopService;
    }

    //===== MENU CHÍNH GIỎ HÀNG =====
    public void cartMenu() {
        while (true) {
            cartView.showCartMenu();
            String choice = userView.getInput("Your choice ");
            switch (choice) {
                case "1":
                    displayCart();
                    break;
                case "2":
                    addItemToCart();
                    break;
                case "3":
                    removeItemFromCart();
                    break;
                case "4":
                    clearCart();
                    break;
                case "5":
                    System.out.println("return.");
                    return;
                default:
                    userView.showMessage("invalid choice.");
            }
        }
    }

    //===== MANAGE CART =====
    public void addItemToCart() {
        String username = SessionManager.getCurrentUser().getUsername();

        laptopService.displayAllProducts();

        Laptop selectedProduct = null;
        int quantity = 0;

        while (true) {
            try {
                String productId = userView.getInput("enter id item: ");
                Optional<Laptop> selectedProductOpt = laptopService.getLaptopById(Integer.parseInt(productId));

                if (selectedProductOpt.isPresent()) {
                    selectedProduct = selectedProductOpt.get();

                    if (selectedProduct.getQuantity() == 0) {
                        System.out.println("item run out of stock.");
                        return;
                    }

                    System.out.println("item information: " + selectedProduct);
                    break;
                } else {
                    System.out.println("Id invalid");
                }
            } catch (NumberFormatException e) {
                System.out.println("invalid id, enter again.");
            }
        }

        while (true) {
            try {
                quantity = Integer.parseInt(userView.getInput("enter number: "));

                if (quantity <= 0) {
                    throw new IllegalArgumentException("number must be greater than 0.");
                }

                if (quantity > selectedProduct.getQuantity()) {
                    System.out.println("number greater than amount please submit lesser number: " + selectedProduct.getQuantity());
                } else {
                    break;
                }

            } catch (NumberFormatException e) {
                System.out.println("invalid number, enter again.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        CartItem item = new CartItem(selectedProduct.getProductId(), selectedProduct.getName(), quantity, selectedProduct.getPrice());
        cartService.updateCart(username, item, true);

        laptopService.decreaseQuantity(selectedProduct.getProductId(), quantity);
    }

    public void removeItemFromCart() {
        String username = SessionManager.getCurrentUser().getUsername();
        Cart cart = cartService.getCart(username);

        if (cart != null && !cart.getItems().isEmpty()) {
            System.out.println("========== all item in your cart ==========");
            List<CartItem> items = cart.getItems();
            for (int i = 0; i < items.size(); i++) {
                CartItem item = items.get(i);
                System.out.println((i + 1) + ". " + "id: " + item.getProductId() +
                        " | name: " + item.getProductName() +
                        " | number: " + item.getQuantity() +
                        " | cost: " + item.getPrice());
            }

            boolean validInput = false;
            while (!validInput) {
                try {
                    String productIndexInput = userView.getInput("id need to delete: ");
                    int productIndex = Integer.parseInt(productIndexInput) - 1;

                    if (productIndex >= 0 && productIndex < items.size()) {
                        CartItem selectedItem = items.get(productIndex);

                        // Xoá sản phẩm khỏi giỏ hàng
                        cartService.updateCart(username, selectedItem, false);

                        // Trả lại số lượng sản phẩm vào kho dựa trên productId
                        Optional<Laptop> laptopOpt = laptopService.getLaptopById(selectedItem.getProductId());
                        if (laptopOpt.isPresent()) {
                            laptopService.increaseQuantity(laptopOpt.get().getProductId(), selectedItem.getQuantity());
                        }

                        System.out.println("delete completed.");
                        validInput = true;
                    } else {
                        System.out.println("id invalid");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("id invalid, enter again.");
                }
            }
        } else {
            System.out.println("your cart is empty");
        }
    }

    private void displayCart() {
        String username = SessionManager.getCurrentUser().getUsername();
        cartService.printCart(username);
    }

    public void clearCart() {
        String username = SessionManager.getCurrentUser().getUsername();
        Cart cart = cartService.getCart(username);

        if (cart != null && !cart.getItems().isEmpty()) {
            boolean validInput = false;
            while (!validInput) {
                String confirmation = userView.getInput("you want to delete? (yes/no): ");

                if (confirmation.equalsIgnoreCase("yes")) {
                    // Trả lại số lượng tất cả các sản phẩm trong giỏ hàng vào kho dựa trên productId
                    for (CartItem item : cart.getItems()) {
                        Optional<Laptop> laptopOpt = laptopService.getLaptopById(item.getProductId());
                        if (laptopOpt.isPresent()) {
                            laptopService.increaseQuantity(laptopOpt.get().getProductId(), item.getQuantity());
                        }
                    }

                    // Xóa toàn bộ giỏ hàng
                    cartService.clearCart(username);
                    System.out.println("all item has been cleared.");
                    validInput = true;
                } else if (confirmation.equalsIgnoreCase("no")) {
                    System.out.println("cancel cleared system");
                    validInput = true;
                } else {
                    System.out.println("enter invalid type please enter (yes/no).");
                }
            }
        } else {
            System.out.println("Your cart empty");
        }
    }

    //===== THANH TOÁN =====
    public void placeOrder() {
        String username = SessionManager.getCurrentUser().getUsername();
        Cart cart = cartService.getCart(username);

        // Nếu giỏ hàng trống, hỏi người dùng có muốn mua hàng không
        if (cart == null || cart.getItems().isEmpty()) {
            System.out.println("your cart is empty");
            String response = userView.getInput("You want to check all item in your cart? (yes/no): ");
            if (response.equalsIgnoreCase("yes")) {
                // Chuyển hướng người dùng đến trang xem tất cả sản phẩm
                laptopService.displayAllProducts();
            } else {
                userView.showMessage("you choose no deleted all item in your cart.");
            }
            return;
        }

        // Nếu giỏ hàng có sản phẩm, hỏi người dùng có muốn thanh toán không
        System.out.println("items you have in your cart are:");
        cartService.printCart(username);

        String confirm = userView.getInput("you want to buy? (yes/no): ");
        if (confirm.equalsIgnoreCase("yes")) {
            userView.showMessage("buy completed. thanks for using my app");

            cartService.clearCart(username);
        } else {
            userView.showMessage("you cancel.");
        }
    }

    // Xem giỏ hàng theo quyền của người dùng
    public void viewCartByRole(String username) {
        // Lấy thông tin người dùng từ SessionManager
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser != null) {
            // Kiểm tra vai trò của người dùng
            if (currentUser.hasRole(Role.ROLE_ADMIN) || currentUser.hasRole(Role.ROLE_SELLER) || currentUser.hasRole(Role.ROLE_MANAGER)) {
                viewAllCarts();
            } else if (currentUser.hasRole(Role.ROLE_BUYER)) {
                cartService.printCart(username);
            } else {
                userView.showMessage("cannot contact");
            }
        } else {
            userView.showMessage("user not found");
        }
    }

    // Admin/Seller xem tất cả giỏ hàng
    public void viewAllCarts() {
        Map<String, Cart> allCarts = cartService.getAllCartsForAdminOrSeller();

        if (allCarts.isEmpty()) {
            userView.showMessage("don't have any cart yet.");
        } else {
            for (Map.Entry<String, Cart> entry : allCarts.entrySet()) {
                String username = entry.getKey();
                Cart cart = entry.getValue();

                userView.showMessage("cart's: " + username + " are");
                cartService.printCart(username);
            }
        }
    }
}
