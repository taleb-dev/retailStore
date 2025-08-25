package dev.taleb.retail.service.discount.strategy;

import dev.taleb.retail.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AffiliateDiscountStrategyTest {

    private final AffiliateDiscountStrategy strategy = new AffiliateDiscountStrategy();

    @Mock
    private User user;

    @Test
    void isMatch_returnsTrue_forAffiliateRole_only() {
        when(user.getRole()).thenReturn(User.Role.AFFILIATE);
        assertTrue(strategy.isMatch(user));

        when(user.getRole()).thenReturn(User.Role.EMPLOYEE);
        assertFalse(strategy.isMatch(user));

        when(user.getRole()).thenReturn(User.Role.CUSTOMER);
        assertFalse(strategy.isMatch(user));
    }

    @Test
    void applyDiscount_returns10PercentOfAmount() {
        double amount = 200.0;
        double discount = strategy.applyDiscount(amount);
        assertEquals(20.0, discount, 1e-9);
    }
}
