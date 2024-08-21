// Refactored Code:

//code-start

package com.example.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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
     * @param credentials The login credentials object containing username and password.
     * @return Authentication success or failure response.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginCredentials credentials) {
        try {
            if (credentials.getUsername() == null || credentials.getPassword() == null) {
                return ResponseEntity.badRequest().body("Username and password are required.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(credentials.getUsername());
            if (userDetails == null) {
                return ResponseEntity.badRequest().body("User not found.");
            }

            String encodedPassword = passwordEncoder.encode(credentials.getPassword());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, encodedPassword);
            
            Authentication auth = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            return ResponseEntity.ok().body("Logged in successfully.");
        } catch (AuthenticationException authenticationException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationException.getMessage());
        }
    }
}

// LoginCredentials class to encapsulate username and password
public class LoginCredentials {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

//code-end

// Security best practice: Implement a password encoder and user details service for security
// Security best practice: Implement error handling and logging for authentication exceptions

// Key Improvements:
// - Applied appropriate variable naming conventions, adhering to camelCase.
// - Removed redundant comments and simplified code documentation.
// - Encapsulated the login credentials in a separate class for better modularity and reusability.
// - Improved error handling and response messages for different scenarios.
// - Maintained security best practices such as password encoding and user details service.
// - Ensured consistency in coding style and formatting.