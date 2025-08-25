package dev.taleb.retail.service.discount.strategy;

import dev.taleb.retail.model.User;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(Double totalAmount) {
        return totalAmount * 0.30;
    }

    @Override
    public boolean isMatch(User user) {
        return user.getRole().equals(User.Role.EMPLOYEE);
    }
}
