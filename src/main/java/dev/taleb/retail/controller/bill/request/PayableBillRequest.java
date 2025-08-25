package dev.taleb.retail.controller.bill.request;

import dev.taleb.retail.model.Bill;
import dev.taleb.retail.model.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PayableBillRequest {

    private List<Item> items;
    private double totalAmount;

    public Bill toBill() {
        Bill bill = new Bill();
        bill.setTotalAmount(totalAmount);
        bill.setItems(items);
        return bill;
    }
}
