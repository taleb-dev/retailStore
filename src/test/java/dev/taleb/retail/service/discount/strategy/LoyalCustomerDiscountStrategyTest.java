package dev.taleb.retail.service.discount.strategy;

import dev.taleb.retail.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoyalCustomerDiscountStrategyTest {

    private final LoyalCustomerDiscountStrategy strategy = new LoyalCustomerDiscountStrategy();

    @Mock
    private User user;

    @Test
    void isMatch_true_whenCustomerRegisteredAtLeastTwoYears() {
        when(user.getRole()).thenReturn(User.Role.CUSTOMER);
        when(user.getRegistrationDate()).thenReturn(LocalDate.now().minusYears(2));
        assertTrue(strategy.isMatch(user));

        when(user.getRegistrationDate()).thenReturn(LocalDate.now().minusYears(3));
        assertTrue(strategy.isMatch(user));
    }

    @Test
    void isMatch_false_whenCustomerRegisteredLessThanTwoYears_orNotCustomer() {
        when(user.getRole()).thenReturn(User.Role.CUSTOMER);
        when(user.getRegistrationDate()).thenReturn(LocalDate.now().minusYears(1));
        assertFalse(strategy.isMatch(user));

        when(user.getRole()).thenReturn(User.Role.EMPLOYEE);
        assertFalse(strategy.isMatch(user));

        when(user.getRole()).thenReturn(User.Role.AFFILIATE);
        assertFalse(strategy.isMatch(user));
    }

    @Test
    void applyDiscount_returns5PercentOfAmount() {
        double discount = strategy.applyDiscount(200.0);
        assertEquals(10.0, discount, 1e-9);
    }
}
