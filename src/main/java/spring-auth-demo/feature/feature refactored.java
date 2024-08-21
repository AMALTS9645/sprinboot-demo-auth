// Refactored code

package com.example.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.example.security.UserDetailsService;
import com.example.security.PasswordEncoder;

/**
 * Controller to handle login API requests.
 */
@RestController
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user based on provided username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return Authentication success or failure.
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password) {
        try {
            // Validate user inputs
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                return ResponseEntity.badRequest().body("Username and password are required.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails == null) {
                return ResponseEntity.badRequest().body("User not found.");
            }

            // Use a password encoder to hash the password
            String encodedPassword = passwordEncoder.encode(password);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, encodedPassword);

            // Handle authentication exceptions
            Authentication auth = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            return ResponseEntity.ok().body("Logged in successfully.");
        } catch (AuthenticationException e) {
            // Log and handle authentication exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
