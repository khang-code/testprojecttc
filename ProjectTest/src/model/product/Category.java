package model.product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Category {

    public static final String WINDOWS_CATEGORY = "Windows";
    public static final String APPLE_CATEGORY = "Apple";

    private String categoryName;
    private List<Laptop> laptops;

    public Category(String categoryName) {
        if (!categoryName.equalsIgnoreCase(WINDOWS_CATEGORY) && !categoryName.equalsIgnoreCase(APPLE_CATEGORY)) {
            throw new IllegalArgumentException("Invalid category name. Only 'Windows' and 'Apple' categories are supported.");
        }
        this.categoryName = categoryName;
        this.laptops = new ArrayList<>();
    }

    public static boolean isWindowsBrand(String brand) {
        return !brand.equalsIgnoreCase("Apple");
    }

    public static boolean isAppleBrand(String brand) {
        return brand.equalsIgnoreCase("Apple");
    }

    public void addLaptopToCategory(Laptop laptop) {
        if ((categoryName.equalsIgnoreCase(WINDOWS_CATEGORY) && isWindowsBrand(laptop.getBrand())) ||
                (categoryName.equalsIgnoreCase(APPLE_CATEGORY) && isAppleBrand(laptop.getBrand()))) {
            laptops.add(laptop);
        } else {
            System.out.println("The laptop does not belong to the " + categoryName + " category. Cannot add.");
        }
    }

    public void displayLaptopsInCategory() {
        System.out.println("===== Category: " + categoryName + " =====");

        if (laptops.isEmpty()) {
            System.out.println("There are no laptops in this category.");
        } else {
            List<Laptop> sortedLaptops = laptops.stream()
                    .sorted(Comparator.comparingInt(Laptop::getProductId))
                    .toList();

            for (Laptop laptop : sortedLaptops) {
                System.out.println(laptop);
            }
        }
    }

    public List<Laptop> getLaptops() {
        return laptops.stream()
                .sorted(Comparator.comparingInt(Laptop::getProductId))
                .collect(Collectors.toList());
    }
}
