package com.marcot.projectparkapi.jwt;

import com.marcot.projectparkapi.entity.UserAccount;
import com.marcot.projectparkapi.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserAccountService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount user = userService.findByUsername(username);
        return new JwtUserDetails(user);
    }

    public JwtToken getTokenAuthenticate(String username){
        UserAccount.Role role = userService.findRoleByUsername(username);
        return JwtUtils.createToken(username,role.name().substring("ROLE_".length()));
    }
}
