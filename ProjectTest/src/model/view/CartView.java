package model.view;
import java.util.ArrayList;
import java.util.List;

public class CartView {
    private UserView userView;

    public CartView(UserView userView) {
        this.userView = userView;
    }
    public void showCartMenu() {
        List<String> options = new ArrayList<>();
        options.add("Show cart");
        options.add("add product into cart");
        options.add("delete product from cart");
        options.add("delete all products from cart");
        options.add("Return to main menu");
        userView.showMenu("Cart menu", options);
    }
}
