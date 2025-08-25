package dev.taleb.retail.service.discount.strategy;

import dev.taleb.retail.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeDiscountStrategyTest {

    private final EmployeeDiscountStrategy strategy = new EmployeeDiscountStrategy();

    @Mock
    private User user;

    @Test
    void isMatch_returnsTrue_forEmployeeRole_only() {
        when(user.getRole()).thenReturn(User.Role.EMPLOYEE);
        assertTrue(strategy.isMatch(user));

        when(user.getRole()).thenReturn(User.Role.AFFILIATE);
        assertFalse(strategy.isMatch(user));

        when(user.getRole()).thenReturn(User.Role.CUSTOMER);
        assertFalse(strategy.isMatch(user));
    }

    @Test
    void applyDiscount_returns30PercentOfAmount() {
        double amount = 200.0;
        double discount = strategy.applyDiscount(amount);
        assertEquals(60.0, discount, 1e-9);
    }
}
