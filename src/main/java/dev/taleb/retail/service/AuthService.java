package dev.taleb.retail.service;

import dev.taleb.retail.model.User;
import dev.taleb.retail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final String ROLE = "role";
    public static final String ID = "id";
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository  userRepository;

    public String login(String inputUsername, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(inputUsername, password)
        );
        UserDetails principal = (UserDetails) auth.getPrincipal();
        List<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        User loginUser = userRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Map<String, Object> extraClaims = Map.of(ROLE, roles, ID,loginUser.getId());
        return jwtService.generateToken(principal.getUsername(), extraClaims);
    }
}
