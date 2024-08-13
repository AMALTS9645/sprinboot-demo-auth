 //code-start

package com.example.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

// Security best practice: Avoid hardcoding sensitive information
import com.example.security.UserDetailsService;
import com.example.security.PasswordEncoder;

/**
 * Controller to handle login API requests.
 */
@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user based on provided username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return Authentication success or failure.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String username, @RequestParam String password) {
        try {
            // Security best practice: Validate user inputs
            if (username == null || password == null) {
                return ResponseEntity.badRequest().body("Username and password are required.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails == null) {
                return ResponseEntity.badRequest().body("User not found.");
            }

            // Security best practice: Use a password encoder to hash the password
            String encodedPassword = passwordEncoder.encode(password);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, encodedPassword);

            // Security best practice: Handle authentication exceptions
            Authentication auth = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            return ResponseEntity.ok().body("Logged in successfully.");
        } catch (AuthenticationException authenticationException) {
            // Security best practice: Log detailed authentication exceptions
            // Log the authentication exception details for debugging
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationException.getMessage());
        }
    }
}

// Security best practice: Implement a password encoder and user details service for security
// Security best practice: Implement error handling and logging for authentication exceptions

//code-end
