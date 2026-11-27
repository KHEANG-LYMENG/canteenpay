// Item.java
package edu.itc.canteen.core;

/** One line of the canteen menu. Prices are whole riel. */
public record Item(String code, String name, long price, Category category) {
    public Item {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("code");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name");
        if (price <= 0 || price % 100 != 0) throw new IllegalArgumentException("price must be a positive multiple of 100");
        if (category == null) throw new IllegalArgumentException("category");
    }
}