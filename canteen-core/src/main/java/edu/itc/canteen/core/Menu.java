// Menu.java
package edu.itc.canteen.core;

import java.util.List;

/** The fixed sample menu used in every lab and example. */
public final class Menu {
    public static final Item BAI_SACH_CHROUK = new Item("M01", "Bai sach chrouk", 6_000, Category.MEAL);
    public static final Item KUY_TEAV = new Item("M02", "Kuy teav", 7_000, Category.MEAL);
    public static final Item MI_CHAR = new Item("M03", "Mi char", 5_500, Category.MEAL);
    public static final Item ICED_COFFEE = new Item("D01", "Iced coffee", 3_000, Category.DRINK);
    public static final Item WATER = new Item("D02", "Water", 1_000, Category.DRINK);
    public static final Item NUM_ANSOM = new Item("S01", "Num ansom", 2_000, Category.SNACK);

    public static final List<Item> ALL = List.of(BAI_SACH_CHROUK, KUY_TEAV, MI_CHAR, ICED_COFFEE, WATER, NUM_ANSOM);

    private Menu() {}
}