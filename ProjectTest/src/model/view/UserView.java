package model.view;
import model.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserView {
    private final Scanner scanner = new Scanner(System.in);

    public UserView() {
    }

    //===== NHẬN ĐẦU VÀO =====
    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    //===== XÁC NHẬN HÀNH ĐỘNG =====
    public boolean confirmAction(String message) {
        System.out.print(message + " (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("yes");
    }

    //===== HIỂN THỊ MENU =====
    public void showMenu(String title, List<String> options) {
        System.out.println("=================== " + title + " ===================");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
    }

    //============================ MENU USER ============================

    //===== HIỂN THỊ MENU BAN ĐẦU =====
    public void showInitialMenu() {
        List<String> options = new ArrayList<>();
        options.add("Register");
        options.add("Login");
        options.add("Exit");
        showMenu("WELCOME TO MY SHOP", options);
    }

    //===== MENU ADMIN =====
    public void showAdminMenu() {
        List<String> options = new ArrayList<>();
        options.add("Users management");
        options.add("Laptop management");
        options.add("User cart management");
        options.add("Exit");
        showMenu("ADMIN MENU", options);
    }

    //===== MENU QUẢN LÝ ADMIN =====
    public void showManageUsersMenuAdmin() {
        List<String> options = new ArrayList<>();
        options.add("add new user");
        options.add("add new employee");
        options.add("new manager");
        options.add("delete user");
        options.add("Edit user");
        options.add("Show user");
        options.add("Return to main menu");
        showMenu("User management (ADMIN)", options);
    }

    //===== MENU MANAGER =====
    public void showManagerMenu() {
        List<String> options = new ArrayList<>();
        options.add("user management");
        options.add("laptop management");
        options.add("user cart management");
        options.add("password management");
        options.add("Exit");
        showMenu("MENU MANAGER", options);
    }

    //===== MENU QUẢN LÝ MANAGER =====
    public void showManageUsersMenu() {
        List<String> options = new ArrayList<>();
        options.add("add customer");
        options.add("add employee");
        options.add("delete user");
        options.add("edit user");
        options.add("show user");
        options.add("return to main menu");
        showMenu("Manager management (MANAGER)", options);
    }

    //===== MENU SELLER =====
    public void showSellerMenu() {
        List<String> options = new ArrayList<>();
        options.add("Laptop management");
        options.add("User cart management");
        options.add("Edit laptop");
        options.add("change password");
        options.add("Exit");
        showMenu("SELLER MENU", options);
    }

    //===== MENU BUYER =====
    public void showBuyerMenu() {
        List<String> options = new ArrayList<>();
        options.add("Watch item");
        options.add("Buy");
        options.add("Cart");
        options.add("Edit User");
        options.add("Change Password");
        options.add("Delete account");
        options.add("Exit");
        showMenu("BUYER MENU", options);
    }

    //===== THÔNG BÁO =====
    public void showMessage(String message) {
        System.out.println(message);
    }

    //===== HIỆN THỊ THÔNG TIN NGƯỜI DÙNG =====
    public void displayUsers(Map<String, User> users) {
        if (users.isEmpty()) {
            System.out.println("Not found");
            return;
        }
        System.out.println("User List");
        for (User user : users.values()) {
            System.out.println(user);

        }
    }
}
