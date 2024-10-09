package controller;

import model.service.CartService;
import model.service.UserService;
import model.user.Role;
import model.user.User;
import model.util.SessionManager;
import model.view.UserView;

import java.util.HashMap;
import java.util.Map;

public class UserController {

    //===== ĐỊNH NGHĨA THUỘC TÍNH =====
    private Map<String, User> users = new HashMap<>();
    private UserView userView;
    private UserService userService;
    private LaptopController laptopController;
    private CartService cartService;
    private CartController cartController;
    private User loggedInUser;

    //===== CONSTRUCTOR =====
    public UserController(UserView userView,
                          UserService userService,
                          LaptopController laptopController,
                          CartService cartService,
                          CartController cartController) {
        this.userView = userView;
        this.userService = userService;
        this.laptopController = laptopController;
        this.cartService = cartService;
        this.cartController = cartController;
    }

    //********************* BẮT ĐẦU CHƯƠNG TRÌNH *********************

    public void start() {
        while (true) {
            if (loggedInUser == null) {
                userView.showInitialMenu();
                String choice = userView.getInput("Choose your choice: ");
                switch (choice) {
                    case "1" -> registerBuyer();
                    case "2" -> login();
                    case "3" -> {
                        userView.showMessage("thanks for using app");
                        return;
                    }
                    default -> userView.showMessage("Choose invalid");
                }
            } else {
                handleRoleBasedMenu();
            }
        }
    }

    private void logout() {
        loggedInUser = null;
        userView.showMessage("Exist completed");
    }

    private void registerBuyer() {
        String username = "";
        String password = "";
        String name = "";
        String email = "";
        String phoneNumber = "";

        while (true) {
            username = userView.getInput("enter username: ");
            try {
                userService.validateUsername(username);
                break;
            } catch (IllegalArgumentException e) {
                userView.showMessage(e.getMessage());
            }
        }
        while (true) {
            password = userView.getInput("enter password: ");
            try {
                userService.validatePassword(password);
                break;
            } catch (IllegalArgumentException e) {
                userView.showMessage(e.getMessage());
            }
        }
        while (true) {
            name = userView.getInput("enter fullname: ");
            try {
                userService.validateFullName(name);
                break;
            } catch (IllegalArgumentException e) {
                userView.showMessage(e.getMessage());
            }
        }
        while (true) {
            email = userView.getInput("enter email: ");
            try {
                userService.validateEmail(email);
                break;
            } catch (IllegalArgumentException e) {
                userView.showMessage(e.getMessage());
            }
        }
        while (true) {
            phoneNumber = userView.getInput("enter phone: ");
            try {
                userService.validatePhoneNumber(phoneNumber);
                break;
            } catch (IllegalArgumentException e) {
                userView.showMessage(e.getMessage());
            }
        }
        try {
            userService.registerBuyer(username, password, phoneNumber, name, email);
            userView.showMessage("registered completed");
        } catch (IllegalArgumentException e) {
            userView.showMessage(e.getMessage());
        }
    }

    private void login() {
        String username = userView.getInput("enter username: ");
        String password = userView.getInput("enter password: ");

        loggedInUser = userService.loginUser(username, password);
        if (loggedInUser != null) {
            SessionManager.login(loggedInUser);
            userView.showMessage("Login success. Role: " + loggedInUser.getRoles());

            if (loggedInUser.getRoles().contains(Role.ROLE_BUYER)) {
                cartService.getOrCreateCart(loggedInUser);
            }

        } else {
            userView.showMessage("wrong password please enter again");
        }
    }

    //*************************** MENU ***************************

    //===== XỬ LÝ MENU DỰA THEO VAI TRÒ =====
    private void handleRoleBasedMenu() {
        if (loggedInUser.hasRole(Role.ROLE_ADMIN)) {
            handleAdminMenu();
        } else if (loggedInUser.hasRole(Role.ROLE_MANAGER)) {
            handleManagerMenu();
        } else if (loggedInUser.hasRole(Role.ROLE_SELLER)) {
            handleSellerMenu();
        } else if (loggedInUser.hasRole(Role.ROLE_BUYER)) {
            handleBuyerMenu();
        }
    }

    //======= MENU ADMIN =======
    private void handleAdminMenu() {
        while (true) {
            userView.showAdminMenu();
            String choice = userView.getInput("your choice: ");

            switch (choice) {
                case "1":
                    manageUsersAdmin();
                    break;
                case "2":
                    laptopController.showProductManagementMenu();
                    break;
                case "3":
                    cartController.viewCartByRole(String.valueOf(users));
                    break;
                case "4":
                    logout();
                    return;
                default:
                    userView.showMessage("invalid choice!");
            }
        }
    }

    //======= MENU QUẢN LÝ (ADMIN) =======
    private void manageUsersAdmin() {
        while (true) {
            userView.showManageUsersMenuAdmin();
            String choice = userView.getInput("Your choice: ");
            switch (choice) {
                case "1":
                    addBuyer();
                    break;
                case "2":
                    addSeller();
                    break;
                case "3":
                    addManager();
                    break;
                case "4":
                    removeUser();
                    break;
                case "5":
                    editUser();
                    break;
                case "6":
                    showUsers();
                    break;
                case "7":
                    return;
                default:
                    userView.showMessage("Choice invalid");
            }
        }
    }

    //======= MENU MANAGER=======
    private void handleManagerMenu() {
        while (true) {
            userView.showManagerMenu();
            String choice = userView.getInput("Your Choice: ");

            switch (choice) {
                case "1":
                    manageUsers();
                    break;
                case "2":
                    laptopController.showProductManagementMenu();
                    break;
                case "3":
                    cartController.viewCartByRole(String.valueOf(users));
                    break;
                case "4":
                    changePassword();
                    break;
                case "5":
                    logout();
                    return;
                default:
                    userView.showMessage("Invalid choice");
            }
        }
    }

    //======= MENU QUẢN LÝ (MANAGER) =======
    private void manageUsers() {
        while (true) {
            userView.showManageUsersMenu();
            String choice = userView.getInput("Your choice ");
            switch (choice) {
                case "1":
                    addBuyer();
                    break;
                case "2":
                    addSeller();
                    break;
                case "3":
                    removeUser();
                    break;
                case "4":
                    editUser();
                    break;
                case "5":
                    showUsers();
                    break;
                case "6":
                    return;
                default:
                    userView.showMessage("Invalid choice");
            }
        }
    }

    //======= MENU SELLER =======
    private void handleSellerMenu() {
        while (true) {
            userView.showSellerMenu();
            String choice = userView.getInput("Your Choice: ");

            switch (choice) {
                case "1":
                    laptopController.showProductManagementMenu();
                    break;
                case "2":
                    cartController.viewCartByRole(String.valueOf(users));
                    break;
                case "3":
                    editUser();
                    break;
                case "4":
                    changePassword();
                    break;
                case "5":
                    logout();
                    return;
                default:
                    userView.showMessage("Invalid choice");
            }
        }
    }

    //======= MENU BUYER =======
    private void handleBuyerMenu() {
        while (true) {
            userView.showBuyerMenu();
            String choice = userView.getInput("Your Choice: ");

            switch (choice) {
                case "1":
                    laptopController.showCategoryLaptops();
                    break;
                case "2":
                    cartController.placeOrder();
                    break;
                case "3":
                    cartController.cartMenu();
                    break;
                case "4":
                    editUser();
                    break;
                case "5":
                    changePassword();
                    break;
                case "6":
                    removeAccount();
                    return;
                case "7":
                    logout();
                    return;
                default:
                    userView.showMessage("Invalid choice");
            }
        }
    }

    //============================ QUẢN LÝ NGƯỜI DÙNG ============================

    //======= HIỂN THỊ THÔNG TIN USERS =======
    private void showUsers() {
        userView.displayUsers(userService.getAllUsers());
    }

    //===== REGISTER USERNAME =====
    private User getInputForRegister() {
        String username;
        while (true) {
            username = userView.getInput("Enter username ");
            try {
                userService.validateUsername(username);
                break;
            } catch (IllegalArgumentException e) {
                userView.showMessage(e.getMessage());
            }
        }

        String password;
        while (true) {
            password = userView.getInput("Enter password ");
            try {
                userService.validatePassword(password);
                break;
            } catch (IllegalArgumentException e) {
                userView.showMessage(e.getMessage());
            }
        }

        String name;
        while (true) {
            name = userView.getInput("Enter full name ");
            try {
                userService.validateFullName(name);
                break;
            } catch (IllegalArgumentException e) {
                userView.showMessage(e.getMessage());
            }
        }

        String email;
        while (true) {
            email = userView.getInput("Enter email: ");
            try {
                userService.validateEmail(email);
                break;
            } catch (IllegalArgumentException e) {
                userView.showMessage(e.getMessage());
            }
        }

        String phoneNumber;
        while (true) {
            phoneNumber = userView.getInput("Enter Phone number: ");
            try {
                userService.validatePhoneNumber(phoneNumber);
                break;
            } catch (IllegalArgumentException e) {
                userView.showMessage(e.getMessage());
            }
        }
        return new User(username, password, phoneNumber, name, email);
    }

    //======= MANAGE USERS =======
    private void addBuyer() {
        User newBuyer = getInputForRegister();
        userService.registerBuyer(newBuyer.getUsername(), newBuyer.getPassword(), newBuyer.getPhoneNumber(), newBuyer.getFullName(), newBuyer.getEmail());
        userView.showMessage("Register User completed");
    }

    private void addSeller() {
        User newSeller = getInputForRegister();
        userService.registerSeller(newSeller.getUsername(), newSeller.getPassword(), newSeller.getPhoneNumber(), newSeller.getFullName(), newSeller.getEmail());
        userView.showMessage("Register employee completed");
    }

    private void addManager() {
        User newManager = getInputForRegister();
        userService.registerManager(newManager.getUsername(), newManager.getPassword(), newManager.getPhoneNumber(), newManager.getFullName(), newManager.getEmail());
        userView.showMessage("Register manager completed");
    }

    private void removeUser() {
        showUsers();
        String username = userView.getInput("Enter username ");
        User userToRemove = userService.getUser(username);

        if (userToRemove == null) {
            userView.showMessage("Not found user name: " + username);
            return;
        }

        if (userToRemove.hasRole(Role.ROLE_MANAGER) && !loggedInUser.hasRole(Role.ROLE_ADMIN)) {
            userView.showMessage("Only admin can use.");
            return;
        }

        userView.confirmAction("You want to delete user?");

        if (userService.removeUser(username)) {
            userView.showMessage("has deleted successfully");
        } else {
            userView.showMessage("Cannot delete user: " + username);
        }
    }

    private void removeAccount() {
        boolean confirmed = userView.confirmAction("You want to delete account?");

        if (confirmed) {
            String username = loggedInUser.getUsername();

            if (userService.removeBuyer(username)) {
                loggedInUser = null;
                userView.showMessage("Your account has been deleted successfully");
            } else {
                userView.showMessage("Cannot delete account: " + username);
            }
        } else {
            userView.showMessage("The account deletion action has been canceled.");
        }
    }

    private void editUser() {
        if (loggedInUser.hasRole(Role.ROLE_ADMIN) || loggedInUser.hasRole(Role.ROLE_MANAGER) || loggedInUser.hasRole(Role.ROLE_SELLER)) {
            showUsers();

            String username = userView.getInput("enter account name to edit: ");
            User userToEdit = userService.getUser(username);

            if (userToEdit == null) {
                userView.showMessage("cannot found user name: " + username);
                return;
            }

            if (loggedInUser.hasRole(Role.ROLE_ADMIN)) {
                String newName = userView.getInput("enter new full name (now: " + userToEdit.getFullName() + "): ");
                String newEmail = userView.getInput("enter ne email (now: " + userToEdit.getEmail() + "): ");
                String newPhoneNumber = userView.getInput("enter new phone (now: " + userToEdit.getPhoneNumber() + "): ");
                userService.updateUserDetail(username, newPhoneNumber, newName, newEmail);
                userView.showMessage("Update completed");
            } else if (loggedInUser.hasRole(Role.ROLE_MANAGER)) {
                if (userToEdit.hasRole(Role.ROLE_SELLER) || userToEdit.hasRole(Role.ROLE_BUYER) || userToEdit.getUsername().equals(loggedInUser.getUsername())) {
                    String newName = userView.getInput("enter new name (now: " + userToEdit.getFullName() + "): ");
                    String newEmail = userView.getInput("enter new email (now: " + userToEdit.getEmail() + "): ");
                    String newPhoneNumber = userView.getInput("enter new phone (now: " + userToEdit.getPhoneNumber() + "): ");
                    userService.updateUserDetail(username, newPhoneNumber, newName, newEmail);
                    userView.showMessage("Update completed");
                } else {
                    userView.showMessage("you cannot edit this user");
                }
            } else if (loggedInUser.hasRole(Role.ROLE_SELLER)) {
                if (userToEdit.hasRole(Role.ROLE_BUYER) || userToEdit.getUsername().equals(loggedInUser.getUsername())) {
                    String newName = userView.getInput("enter new full name(now: " + userToEdit.getFullName() + "): ");
                    String newEmail = userView.getInput("enter new email(now: " + userToEdit.getEmail() + "): ");
                    String newPhoneNumber = userView.getInput("enter new number (now: " + userToEdit.getPhoneNumber() + "): ");
                    userService.updateUserDetail(username, newPhoneNumber, newName, newEmail);
                    userView.showMessage("update completed");
                } else {
                    userView.showMessage("cannot edit this user");
                }
            } else {
                userView.showMessage("you not have permission to do this");
            }
        } else if (loggedInUser.hasRole(Role.ROLE_BUYER)) {
            String username = loggedInUser.getUsername();
            User userToEdit = userService.getUser(username);

            if (userToEdit == null) {
                userView.showMessage("Không tìm thấy người dùng với tài khoản: " + username);
                return;
            }

            String newName = userView.getInput("Nhập họ tên mới (now: " + userToEdit.getFullName() + "): ");
            String newEmail = userView.getInput("enter new email (now: " + userToEdit.getEmail() + "): ");
            String newPhoneNumber = userView.getInput("enter new number (now: " + userToEdit.getPhoneNumber() + "): ");

            userService.updateUserDetail(username, newPhoneNumber, newName, newEmail);
            userView.showMessage("update your information completed");
        } else {
            userView.showMessage("you not have permission to do this");
        }
    }

    private void changePassword() {
        userView.confirmAction("you want to change password");
        String oldPassword;
        String newPassword;
        boolean valid = false;

        while (!valid) {
            oldPassword = userView.getInput("old password: ");

            if (userService.getUser(loggedInUser.getUsername()).getPassword().equals(oldPassword)) {
                newPassword = userView.getInput("new password: ");
                userService.changePasswordForUser(loggedInUser.getUsername(), oldPassword, newPassword);
                valid = true;
            } else {
                userView.showMessage("password wrong or invalid, please enter again");
            }
        }
    }
}
