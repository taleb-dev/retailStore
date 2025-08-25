package dev.taleb.retail.service.discount;

import dev.taleb.retail.model.Bill;
import dev.taleb.retail.model.Item;
import dev.taleb.retail.model.User;
import dev.taleb.retail.repository.UserRepository;
import dev.taleb.retail.service.discount.strategy.AmountBasedDiscountStrategy;
import dev.taleb.retail.service.discount.strategy.DiscountStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class DiscountService {

    private final List<DiscountStrategy> discountStrategies;
    private final UserRepository  userRepository;
    private final AmountBasedDiscountStrategy amountBasedDiscountStrategy;

    public Bill getPayableBill(UserDetails userDetails, Bill bill) {
        Optional<User> currentUser = userRepository.findByUsername(userDetails.getUsername());
        Optional<DiscountStrategy> matchStrategy = getMatchStrategy(currentUser.get());
        double discountFromPercentage = 0;
        if (matchStrategy.isPresent()) {

            double eligibleAmount = bill.getItems().stream()
                    .filter(i -> i.getCategory() != Item.Category.GROCERY)
                    .mapToDouble(Item::getPrice)
                    .sum();

            discountFromPercentage = matchStrategy.get().applyDiscount(eligibleAmount);
        }
        double discountFromAmount = amountBasedDiscountStrategy.applyDiscount(bill.getTotalAmount());
        double finalAmount = bill.getTotalAmount() - discountFromPercentage - discountFromAmount;
        bill.setNetPayableAmount(finalAmount);
        return bill;

    }
    
    
    public Optional<DiscountStrategy> getMatchStrategy(User user) {
        return discountStrategies.stream().filter(it -> it.isMatch(user)).findFirst();
    }
}
