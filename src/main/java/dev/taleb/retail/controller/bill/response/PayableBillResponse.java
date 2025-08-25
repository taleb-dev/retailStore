package dev.taleb.retail.controller.bill.response;

import dev.taleb.retail.model.Bill;
import dev.taleb.retail.model.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PayableBillResponse {
    private List<Item> items;
    private double totalAmount;
    private double netPayableAmount;

    public static PayableBillResponse from(Bill bill) {
        PayableBillResponse payableBillResponse = new PayableBillResponse();
        payableBillResponse.setItems(bill.getItems());
        payableBillResponse.setTotalAmount(bill.getTotalAmount());
        payableBillResponse.setNetPayableAmount(bill.getNetPayableAmount());
        return payableBillResponse;
    }
}
