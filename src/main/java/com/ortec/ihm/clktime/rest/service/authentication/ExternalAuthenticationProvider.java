package com.ortec.ihm.clktime.rest.service.authentication;

import com.ortec.ihm.clktime.rest.common.user.TokenedUser;
import com.ortec.ihm.clktime.rest.database.model.dto.UserDTO;
import com.ortec.ihm.clktime.rest.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Author: romain.pillot
 * @Date: 25/07/2017
 */
@Component
public class ExternalAuthenticationProvider implements AuthenticationProvider, Serializable {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserRoleService userRoleService;

    /**
     * Perform a connection by username and password.
     *
     * @param authentication an user login process
     * @return a authentication token by username and password
     * @throws AuthenticationException in case of invalid credentials
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDTO user = authenticationService
                .loadByConnection(name, password)
                .orElseThrow(() ->
                        new BadCredentialsException(String.format("Bad user/pass for %s", name)));

        return new UsernamePasswordAuthenticationToken(new TokenedUser(user.getId()), null,
                userRoleService.mapToAppRoles(user.getRoles()));
    }

    /**
     * @param authentication an authentication template looking for its implementation
     * @return true if the expected authentication is an UsernamePasswordAuthenticationToken.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}