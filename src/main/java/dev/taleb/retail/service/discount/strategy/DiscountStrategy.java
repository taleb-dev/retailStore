package dev.taleb.retail.service.discount.strategy;

import dev.taleb.retail.model.Bill;
import dev.taleb.retail.model.User;

public interface DiscountStrategy {
    double applyDiscount(Bill bill);
    boolean isMatch(User user);

}
