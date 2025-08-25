package dev.taleb.retail.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    private String name;
    private Category category;
    private double price;

    public enum Category {
        GROCERY, OTHER
    }
}
