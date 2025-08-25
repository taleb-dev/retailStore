package dev.taleb.retail.service.discount;

import dev.taleb.retail.model.Bill;
import dev.taleb.retail.model.Item;
import dev.taleb.retail.model.User;
import dev.taleb.retail.repository.UserRepository;
import dev.taleb.retail.service.discount.strategy.AmountBasedDiscountStrategy;
import dev.taleb.retail.service.discount.strategy.DiscountStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DiscountServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AmountBasedDiscountStrategy amountBasedDiscountStrategy;
    @Mock
    private DiscountStrategy percentageStrategy1;
    @Mock
    private DiscountStrategy percentageStrategy2;
    @Mock
    private UserDetails userDetails;

    private DiscountService discountService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Default service with two percentage strategies
        discountService = new DiscountService(List.of(percentageStrategy1, percentageStrategy2), userRepository, amountBasedDiscountStrategy);
        testUser = User.builder()
                .id("u1")
                .name("John Doe")
                .username("john")
                .password("secret")
                .role(User.Role.CUSTOMER)
                .registrationDate(LocalDate.now().minusYears(3))
                .build();
    }

    @Test
    void getPayableBill_appliesPercentageOnNonGrocery_andAmountOnTotal() {
        // Bill: grocery=100, other=200 and 50; eligible for % discount = 250; total = 350
        Bill bill = new Bill();
        Item item1 = new Item(); item1.setName("Milk"); item1.setCategory(Item.Category.GROCERY); item1.setPrice(100.0);
        Item item2 = new Item(); item2.setName("Jeans"); item2.setCategory(Item.Category.OTHER); item2.setPrice(200.0);
        Item item3 = new Item(); item3.setName("Book"); item3.setCategory(Item.Category.OTHER); item3.setPrice(50.0);
        bill.setItems(Arrays.asList(item1, item2, item3));
        bill.setTotalAmount(350.0);

        when(userDetails.getUsername()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));

        // First strategy matches and returns 10% of eligible amount
        when(percentageStrategy1.isMatch(testUser)).thenReturn(true);
        when(percentageStrategy1.applyDiscount(ArgumentMatchers.anyDouble()))
                .thenAnswer(inv -> 0.10 * inv.getArgument(0, Double.class));

        // Second strategy should not be asked to match or apply
        when(percentageStrategy2.isMatch(any())).thenReturn(false);

        // Amount-based discount on total = 15 (5 per 100)
        when(amountBasedDiscountStrategy.applyDiscount(350.0)).thenReturn(15.0);

        Bill result = discountService.getPayableBill(userDetails, bill);

        // Expected: % discount = 10% of 250 = 25; amount-based = 15; final = 350 - 25 - 15 = 310
        assertEquals(310.0, result.getNetPayableAmount(), 1e-6);

        verify(userDetails).getUsername();
        verify(userRepository).findByUsername("john");
        verify(percentageStrategy1).isMatch(testUser);
        verify(percentageStrategy1).applyDiscount(250.0);
        verify(amountBasedDiscountStrategy).applyDiscount(350.0);
        verifyNoInteractionsOnPercentageApplyForSecondStrategy();
    }

    @Test
    void getPayableBill_whenNoMatchingStrategy_appliesOnlyAmountBased() {
        // Bill: grocery=50, other=150; eligible = 150; total = 200
        Bill bill = new Bill();
        Item item1 = new Item(); item1.setName("Bread"); item1.setCategory(Item.Category.GROCERY); item1.setPrice(50.0);
        Item item2 = new Item(); item2.setName("Shirt"); item2.setCategory(Item.Category.OTHER); item2.setPrice(150.0);
        bill.setItems(List.of(item1, item2));
        bill.setTotalAmount(200.0);

        when(userDetails.getUsername()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));

        when(percentageStrategy1.isMatch(testUser)).thenReturn(false);
        when(percentageStrategy2.isMatch(testUser)).thenReturn(false);

        when(amountBasedDiscountStrategy.applyDiscount(200.0)).thenReturn(10.0); // 2*5

        Bill result = discountService.getPayableBill(userDetails, bill);

        assertEquals(190.0, result.getNetPayableAmount(), 1e-6);

        // Ensure no percentage discount applied
        verify(percentageStrategy1, never()).applyDiscount(anyDouble());
        verify(percentageStrategy2, never()).applyDiscount(anyDouble());
        verify(amountBasedDiscountStrategy).applyDiscount(200.0);
    }

    @Test
    void getMatchStrategy_returnsFirstMatchingStrategy() {
        // Create service with ordered strategies
        DiscountService svc = new DiscountService(List.of(percentageStrategy1, percentageStrategy2), userRepository, amountBasedDiscountStrategy);
        when(percentageStrategy1.isMatch(testUser)).thenReturn(true);
        when(percentageStrategy2.isMatch(testUser)).thenReturn(true);

        Optional<DiscountStrategy> match = svc.getMatchStrategy(testUser);
        assertTrue(match.isPresent());
        assertSame(percentageStrategy1, match.get());
    }

    @Test
    void getPayableBill_userNotFound_throwsNoSuchElementException() {
        when(userDetails.getUsername()).thenReturn("jane");
        when(userRepository.findByUsername("jane")).thenReturn(Optional.empty());

        Bill bill = new Bill();
        bill.setItems(List.of());
        bill.setTotalAmount(0.0);

        assertThrows(NoSuchElementException.class, () -> discountService.getPayableBill(userDetails, bill));

        verify(userRepository).findByUsername("jane");
        verifyNoInteractions(percentageStrategy1, percentageStrategy2, amountBasedDiscountStrategy);
    }

    @Test
    void getPayableBill_usesSecondStrategy_ifFirstDoesNotMatch() {
        // Bill: grocery=100, other=300 => eligible=300, total=400
        Item g = new Item(); g.setName("Grocery"); g.setCategory(Item.Category.GROCERY); g.setPrice(100.0);
        Item o = new Item(); o.setName("Other"); o.setCategory(Item.Category.OTHER); o.setPrice(300.0);
        Bill bill = new Bill();
        bill.setItems(List.of(g, o));
        bill.setTotalAmount(400.0);

        when(userDetails.getUsername()).thenReturn("jane");
        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(testUser));

        when(percentageStrategy1.isMatch(testUser)).thenReturn(false);
        when(percentageStrategy2.isMatch(testUser)).thenReturn(true);
        when(percentageStrategy2.applyDiscount(ArgumentMatchers.anyDouble()))
                .thenAnswer(inv -> 0.20 * inv.getArgument(0, Double.class)); // 20%

        when(amountBasedDiscountStrategy.applyDiscount(400.0)).thenReturn(20.0); // $5 per $100

        Bill result = discountService.getPayableBill(userDetails, bill);

        // 20% of 300 = 60; amount-based 20 => 400 - 60 - 20 = 320
        assertEquals(320.0, result.getNetPayableAmount(), 1e-6);
        verify(percentageStrategy2).applyDiscount(300.0);
        verify(percentageStrategy1, never()).applyDiscount(anyDouble());
    }

    @Test
    void getPayableBill_onlyGroceries_appliesOnlyAmountBased() {
        // Bill: groceries only => eligible for % = 0, total=180
        Item g1 = new Item(); g1.setName("Rice"); g1.setCategory(Item.Category.GROCERY); g1.setPrice(80.0);
        Item g2 = new Item(); g2.setName("Eggs"); g2.setCategory(Item.Category.GROCERY); g2.setPrice(100.0);
        Bill bill = new Bill();
        bill.setItems(List.of(g1, g2));
        bill.setTotalAmount(180.0);

        when(userDetails.getUsername()).thenReturn("jane");
        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(testUser));

        when(percentageStrategy1.isMatch(testUser)).thenReturn(false);
        when(percentageStrategy2.isMatch(testUser)).thenReturn(false);
        when(amountBasedDiscountStrategy.applyDiscount(180.0)).thenReturn(5.0);

        Bill result = discountService.getPayableBill(userDetails, bill);

        assertEquals(175.0, result.getNetPayableAmount(), 1e-6);
        verify(percentageStrategy1, never()).applyDiscount(anyDouble());
        verify(percentageStrategy2, never()).applyDiscount(anyDouble());
        verify(amountBasedDiscountStrategy).applyDiscount(180.0);
    }

    @Test
    void getPayableBill_whenBothStrategiesMatch_usesFirstOnly() {
        // Bill: other=250, grocery=50 => eligible=250, total=300
        Item o1 = new Item(); o1.setName("Shoes"); o1.setCategory(Item.Category.OTHER); o1.setPrice(250.0);
        Item g = new Item(); g.setName("Veggies"); g.setCategory(Item.Category.GROCERY); g.setPrice(50.0);
        Bill bill = new Bill();
        bill.setItems(List.of(o1, g));
        bill.setTotalAmount(300.0);

        when(userDetails.getUsername()).thenReturn("jane");
        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(testUser));

        when(percentageStrategy1.isMatch(testUser)).thenReturn(true);
        when(percentageStrategy2.isMatch(testUser)).thenReturn(true);
        when(percentageStrategy1.applyDiscount(ArgumentMatchers.anyDouble()))
                .thenAnswer(inv -> 0.10 * inv.getArgument(0, Double.class)); // 10% for first
        // even if second matches, it should not be used

        when(amountBasedDiscountStrategy.applyDiscount(300.0)).thenReturn(10.0);

        Bill result = discountService.getPayableBill(userDetails, bill);

        // 10% of 250 = 25; amount-based 10 => 300 - 25 - 10 = 265
        assertEquals(265.0, result.getNetPayableAmount(), 1e-6);
        verify(percentageStrategy1).applyDiscount(250.0);
        verify(percentageStrategy2, never()).applyDiscount(anyDouble());
    }

    @Test
    void getPayableBill_returnsSameBillInstance() {
        Bill bill = new Bill();
        bill.setItems(List.of());
        bill.setTotalAmount(50.0);

        when(userDetails.getUsername()).thenReturn("jane");
        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(testUser));

        when(percentageStrategy1.isMatch(testUser)).thenReturn(false);
        when(percentageStrategy2.isMatch(testUser)).thenReturn(false);
        when(amountBasedDiscountStrategy.applyDiscount(50.0)).thenReturn(0.0);

        Bill result = discountService.getPayableBill(userDetails, bill);
        assertSame(bill, result);
    }

    private void verifyNoInteractionsOnPercentageApplyForSecondStrategy() {
        // helper to make intent explicit
        verify(percentageStrategy2, never()).applyDiscount(anyDouble());
    }
}
