package dev.taleb.retail.service.discount.strategy;

import dev.taleb.retail.model.User;
import org.springframework.stereotype.Component;

@Component
public class AmountBasedDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(Double totalAmount) {
        int hundreds = (int) (totalAmount / 100);
        return (double) hundreds  * 5;

    }

    @Override
    public boolean isMatch(User user) {
        return false;
    }
}
