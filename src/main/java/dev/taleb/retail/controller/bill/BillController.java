package dev.taleb.retail.controller.bill;

import dev.taleb.retail.controller.bill.request.PayableBillRequest;
import dev.taleb.retail.controller.bill.response.PayableBillResponse;
import dev.taleb.retail.service.discount.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bills")
@RequiredArgsConstructor
public class BillController {

    private final DiscountService discountService;

    @PostMapping("/calculate")
    public ResponseEntity<PayableBillResponse> getPayableBill(@AuthenticationPrincipal UserDetails userDetails, @RequestBody PayableBillRequest payableBillRequest) {
        return ResponseEntity.ok(PayableBillResponse.from(discountService.getPayableBill(userDetails,payableBillRequest.toBill())));
    }
}
