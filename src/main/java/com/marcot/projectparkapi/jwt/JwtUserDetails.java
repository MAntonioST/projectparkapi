package com.marcot.projectparkapi.jwt;

import com.marcot.projectparkapi.entity.UserAccount;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails  extends User {

  private UserAccount user;


    public JwtUserDetails(UserAccount user) {
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public Long getId(){
        return  this.user.getId();
    }

    public String getRole(){
        return this.user.getRole().name();
    }
}
