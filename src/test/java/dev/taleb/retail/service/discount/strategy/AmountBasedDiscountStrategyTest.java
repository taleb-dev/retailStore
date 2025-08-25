package dev.taleb.retail.service.discount.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmountBasedDiscountStrategyTest {

    private final AmountBasedDiscountStrategy strategy = new AmountBasedDiscountStrategy();

    @Test
    void isMatch_alwaysFalse() {
        // isMatch is hardcoded to false regardless of user
        assertFalse(strategy.isMatch(null));
    }

    @Test
    void applyDiscount_fiveDollarsPerHundred_floorDivision() {
        assertEquals(0.0, strategy.applyDiscount(0.0));
        assertEquals(0.0, strategy.applyDiscount(99.99));
        assertEquals(5.0, strategy.applyDiscount(100.0));
        assertEquals(5.0, strategy.applyDiscount(199.99));
        assertEquals(10.0, strategy.applyDiscount(200.0));
        assertEquals(45.0, strategy.applyDiscount(900.0));
    }
}
