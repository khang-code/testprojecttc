package model.service;
import model.user.*;
import model.view.UserView;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class UserService {


    private static final String USERS_FILE_PATH = "C:\\Users\\DELL\\IdeaProjects\\ProjectTest\\src\\model\\stored\\users.csv";


    private UserView userView;
    private Map<String, User> users = new HashMap<>();
    private final Map<String, Seller> sellers = new HashMap<>();
    private final Map<String, Buyer> buyers = new HashMap<>();
    private final Map<String, Manager> managers = new HashMap<>();
    private final Map<String, List<Role>> pendingSellers = new HashMap<>();


    public UserService() {
        users = readUsersFromFile();
    }


    public boolean isUsername(String username) {
        return users.containsKey(username);
    }


    public void registerBuyer(String username, String password, String phoneNumber, String fullName, String email) {
        if (isUsername(username)) {
            userView.showMessage("Tên tài khoản đã tồn tại. Vui lòng chọn tên tài khoản khác.");
        } else {
            User newUser = new User(username, password, phoneNumber, fullName, email);
            Buyer newBuyer = new Buyer(username, password, phoneNumber, fullName, email);

            users.put(username, newUser);
            buyers.put(username, newBuyer);
            addRoleToUser(username, Role.ROLE_BUYER);
            writeUsersToFile(users);
        }
    }


    public void registerSeller(String username, String password, String phoneNumber, String fullName, String email) {
        if (isUsername(username)) {
            userView.showMessage("Tên tài khoản đã tồn tại. Vui lòng chọn tên tài khoản khác.");
        } else {
            User newUser = new User(username, password, phoneNumber, fullName, email);
            Seller newSeller = new Seller(username, password, phoneNumber, fullName, email);
            users.put(username, newUser);
            sellers.put(username, newSeller);
            addRoleToUser(username, Role.ROLE_SELLER);
            writeUsersToFile(users);
        }
    }


    public void registerManager(String username, String password, String phoneNumber, String fullName, String email) {
        if (isUsername(username)) {
            userView.showMessage("Tên tài khoản đã tồn tại. Vui lòng chọn tên tài khoản khác.");
        } else {
            User newUser = new User(username, password, phoneNumber, fullName, email);
            Manager newManager = new Manager(username, password, phoneNumber, fullName, email);

            users.put(username, newUser);
            managers.put(username, newManager);
            addRoleToUser(username, Role.ROLE_MANAGER);
            writeUsersToFile(users);
        }
    }


    public void updateUserDetail(String username, String newPhoneNumber, String newFullName, String newEmail) {
        User user = users.get(username);
        if (user != null) {
            user.setFullName(newFullName);
            user.setEmail(newEmail);
            user.setPhoneNumber(newPhoneNumber);
            writeUsersToFile(users);
        }
    }


    public User loginUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void addRoleToUser(String username, Role role) {
        User user = users.get(username);
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với tài khoản: " + username);
        } else {
            user.addRole(role);
            writeUsersToFile(users);
        }
    }

    public boolean removeUser(String username) {
        boolean removed = users.remove(username) != null;
        if (removed) {
            writeUsersToFile(users);
        }
        return removed;
    }

    public boolean removeBuyer(String username) {
        User userToRemove = getUser(username);
        if (userToRemove != null && userToRemove.hasRole(Role.ROLE_BUYER)) {
            users.remove(username);
            writeUsersToFile(users);
            return true;
        }
        return false;
    }


    public void changePasswordForUser(String username, String oldPassword, String newPassword) {
        User user = getUser(username);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.changePassword(newPassword);
            System.out.println("Change password completed");
            writeUsersToFile(users);
        } else {
            throw new IllegalArgumentException("Wrong old password or account not exist");
        }
    }


    public User getUser(String username) {
        return users.get(username);
    }


    public Map<String, User> getAllUsers() {
        return users;
    }


    public void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username can't be null or empty");
        } else if (!username.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Username only have number or words not special symbol");
        } else if (isUsername(username)) {
            throw new IllegalArgumentException("Account name exists");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password can't be null or empty");
        }
    }

    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("0.*")) {
            throw new IllegalArgumentException("Phone numbers must start with 0\n");
        }
    }

    public void validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("fullName can't be null or empty");
        } else if (!fullName.matches("^[\\p{L} .'-]+$")) {
            throw new IllegalArgumentException("Full names can only contain letters, spaces, periods, dashes, and apostrophes\n");
        }
    }

    public void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("email not exist! Example: a@a.com");
        }
    }


    public void writeUsersToFile(Map<String, User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE_PATH))) {
            writer.write("UserName,Password,PhoneNumber,Email,FullName,Role\n");

            for (User user : users.values()) {
                String rolesString;
                rolesString = user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.joining("-"));

                writer.write(String.format("%s,%s,%s,%s,%s,%s%n",
                        user.getUsername(),
                        user.getPassword(),
                        user.getPhoneNumber(),
                        user.getEmail(),
                        user.getFullName(),
                        rolesString));
            }
        } catch (IOException e) {
            System.err.println("Error writing data to file: " + USERS_FILE_PATH);
            e.printStackTrace();
        }
    }
    private Map<String, User> readUsersFromFile() {
        Map<String, User> users = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE_PATH))) {
            // Skip header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");

                if (split.length != 6) {
                    System.err.println("Invalid data: " + line);
                    continue;
                }

                String username = split[0].trim();
                String password = split[1].trim();
                String phoneNumber = split[2].trim();
                String email = split[3].trim();
                String fullName = split[4].trim();
                String rolesString = split[5].trim();

                User user = new User(username, password, phoneNumber, fullName, email);

                // Use stream to parse roles
                Set<Role> roles = Arrays.stream(rolesString.split("-"))
                        .map(roleStr -> {
                            try {
                                return Role.valueOf(roleStr);
                            } catch (IllegalArgumentException e) {
                                System.err.println("Invalid role: " + roleStr);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                user.setRoles(roles);
                users.put(username, user);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + USERS_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error reading data from file: " + USERS_FILE_PATH);
            e.printStackTrace();
        }

        return users;
    }
}
