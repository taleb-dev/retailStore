package dev.taleb.retail.controller.auth.response;


import lombok.Getter;

@Getter
public class LoginResponse {
    private String token;

    public static LoginResponse fromToken(String token) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.token = token;
        return loginResponse;
    }
}
