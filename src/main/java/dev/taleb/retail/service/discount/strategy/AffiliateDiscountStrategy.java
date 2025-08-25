package dev.taleb.retail.service.discount.strategy;

import dev.taleb.retail.model.Bill;
import dev.taleb.retail.model.User;
import org.springframework.stereotype.Component;

@Component
public class AffiliateDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(Bill bill) {
        return bill.getTotalAmount() * 0.10;
    }

    @Override
    public boolean isMatch(User user) {
        return user.getRole() == User.Role.AFFILIATE;
    }
}
