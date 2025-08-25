package dev.taleb.retail.controller.auth;

import dev.taleb.retail.controller.auth.request.LoginRequest;
import dev.taleb.retail.controller.auth.response.LoginResponse;
import dev.taleb.retail.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
       return  LoginResponse.fromToken(authService.login(req.getUsername(), req.getPassword()));
    }


}
