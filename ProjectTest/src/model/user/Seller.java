package model.user;

public class Seller extends User {
    public Seller(String username, String password, String phoneNumber, String fullName, String email) {
        super(username, password, phoneNumber, fullName, email);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
