package model.user;

public class Admin extends User {
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";

    public Admin() {
        super(DEFAULT_USERNAME, DEFAULT_PASSWORD, "123456789", "Administrator", "admin@gmail.com");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
