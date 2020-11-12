package com.vouchrtech.demo.auth.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class MyCustomerService {

    private static final Logger LOGGER = Logger.getLogger(MyCustomerService.class.getName());

    // by changing the following line I acknowledge responsibility to return a proper UserDetails object from
    // the client's already authorized session rather than an unverified randomized user contained here
    private static final boolean IGNORE_TOKEN_RETURN_RANDOM_USER = false;

    public Optional<UserDetails> findByToken(String token) {

        UserDetails userDetails = null;

        if (IGNORE_TOKEN_RETURN_RANDOM_USER) {
            userDetails = new UserDetails() {

                private final String username = UUID.randomUUID().toString();

                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return new ArrayList<>();
                }

                @Override
                public String getPassword() {
                    return null;
                }

                @Override
                public String getUsername() {
                    return username;
                }

                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }

                @Override
                public boolean isAccountNonLocked() {
                    return true;
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return true;
                }

                @Override
                public boolean isEnabled() {
                    return true;
                }
            };
        } else {
            if(token == null) {
                LOGGER.warning("No Bearer token found");
            }
            // return a UserDetails object based on existing user session
        }

        return Optional.ofNullable(userDetails);
    }
}
