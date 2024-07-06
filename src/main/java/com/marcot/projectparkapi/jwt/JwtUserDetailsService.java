package com.marcot.projectparkapi.jwt;

import com.marcot.projectparkapi.entity.UserEntity;
import com.marcot.projectparkapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userService.findByUsername(username);
        return new JwtUserDetails(user);
    }

    public JwtToken getTokenAuthenticate(String username){
        UserEntity.Role role = userService.findRoleByUsername(username);
        return JwtUtils.createToken(username,role.name().substring("ROLE_".length()));
    }
}
