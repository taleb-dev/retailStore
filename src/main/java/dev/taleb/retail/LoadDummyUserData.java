package dev.taleb.retail;

import dev.taleb.retail.model.User;
import dev.taleb.retail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("dummy")
@Log4j2
public class LoadDummyUserData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        List<User> dummyUsers = List.of(

                User.builder()
                        .role(User.Role.CUSTOMER)
                        .password(passwordEncoder.encode("customer"))
                        .username("customer")
                        .name("customer")
                        .registrationDate(LocalDate.now())
                        .build(),

                User.builder()
                        .role(User.Role.AFFILIATE)
                        .password(passwordEncoder.encode("affiliate"))
                        .username("affiliate")
                        .name("affiliate")
                        .registrationDate(LocalDate.now())
                        .build(),
                User.builder()
                        .role(User.Role.EMPLOYEE)
                        .password(passwordEncoder.encode("employee"))
                        .username("employee")
                        .name("employee")
                        .registrationDate(LocalDate.now())
                        .build()
        );

        userRepository.saveAll(dummyUsers);
        log.info("dummy users saved");
    }
}
