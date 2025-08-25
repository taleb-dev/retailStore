package dev.taleb.retail.service.discount.strategy;

import dev.taleb.retail.model.User;

public interface  DiscountStrategy {
    double applyDiscount(Double price);
     boolean isMatch(User user);


}
