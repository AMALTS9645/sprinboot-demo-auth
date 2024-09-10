```java
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

@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginCredentials credentials) {
        try {
            validateCredentials(credentials);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(credentials.getUsername());
            if (userDetails == null) {
                return ResponseEntity.badRequest().body("User not found.");
            }

            String encodedPassword = passwordEncoder.encode(credentials.getPassword());
            Authentication auth = authenticate(userDetails, encodedPassword);
            SecurityContextHolder.getContext().setAuthentication(auth);

            return ResponseEntity.ok().body("Logged in successfully.");
        } catch (InvalidCredentialsException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    private void validateCredentials(LoginCredentials credentials) {
        if (credentials == null || credentials.getUsername() == null || credentials.getPassword() == null) {
            throw new InvalidCredentialsException("Username and password are required.");
        }
    }

    private Authentication authenticate(UserDetails userDetails, String encodedPassword) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, encodedPassword);
        return authenticationManager.authenticate(authenticationToken);
    }
}
```

```java
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
```

```java
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
```