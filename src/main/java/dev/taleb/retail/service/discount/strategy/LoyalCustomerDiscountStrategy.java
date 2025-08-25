package dev.taleb.retail.service.discount.strategy;

import dev.taleb.retail.model.Bill;
import dev.taleb.retail.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class LoyalCustomerDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(Double totalAmount) {
        return totalAmount * 0.05;
    }

    @Override
    public boolean isMatch(User user) {
        if (User.Role.CUSTOMER.equals(user.getRole())) {
            long years = ChronoUnit.YEARS.between(user.getRegistrationDate(), LocalDate.now());
            return years >= 2;
        }
        return false;
    }
}
