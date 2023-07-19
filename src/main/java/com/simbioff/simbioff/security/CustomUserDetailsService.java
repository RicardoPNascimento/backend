package com.simbioff.simbioff.security;

import com.simbioff.simbioff.models.PermissionModel;
import com.simbioff.simbioff.models.Role;
import com.simbioff.simbioff.models.UserModel;
import com.simbioff.simbioff.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService  implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(email);
        return new User(user.getEmail(), user.getPassword() , getAuthority(user));
    }

    private Collection<? extends GrantedAuthority> getAuthority(UserModel user) {
        List<PermissionModel> roles = user.getRoles();
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }

    private List<SimpleGrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }
}
