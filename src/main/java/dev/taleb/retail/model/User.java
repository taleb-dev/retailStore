package dev.taleb.retail.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@Document(collection = "users")
@Builder
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String username;
    private String password;
    private Role role;
    private LocalDate registrationDate;

    public enum Role {
        EMPLOYEE, AFFILIATE, CUSTOMER
    }


}
