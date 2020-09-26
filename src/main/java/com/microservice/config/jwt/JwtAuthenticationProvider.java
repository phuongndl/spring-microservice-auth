package com.microservice.config.jwt;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.microservice.service.UserDetailsImpl;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JwtUtils jwtConfig;

    @SuppressWarnings("unchecked")
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();

        Map<String, Object> data = jwtConfig.getDataFromJwtToken(token);

        UserDetails userDetails = UserDetailsImpl.build(new Long((int) data.get("userId")), (String) data.get("username"), 
        		(List<String>) data.get("roles"), (List<String>) data.get("permissions"));
        return new JwtAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
