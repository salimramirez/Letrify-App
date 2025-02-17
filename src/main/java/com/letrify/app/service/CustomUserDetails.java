package com.letrify.app.service;

import com.letrify.app.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final Long userId;
    private final String email;
    private final String password;
    private final String role;
    private final User.UserType userType;
    private final Long companyId;
    private final Long individualId;

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.userType = user.getUserType();
        this.companyId = (user.isCompany() && user.getCompany() != null) ? user.getCompany().getId() : null;
        this.individualId = (user.isIndividual() && user.getIndividual() != null) ? user.getIndividual().getId() : null;
    }

    public Long getUserId() {
        return userId;
    }

    public User.UserType getUserType() {
        return userType;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Long getIndividualId() {
        return individualId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
}
