package dev.taleb.retail.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "bills")
public class Bill {

    @Id
    private String id;
    private List<Item> items;
    private double totalAmount;
    private double netPayableAmount;

}
