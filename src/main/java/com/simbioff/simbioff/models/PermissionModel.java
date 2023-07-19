package com.simbioff.simbioff.models;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "permission")
public class PermissionModel implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_permission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Role role;

    @Override
    public String getAuthority() {
        return this.role.toString();
    }

    public Long getId_permission() {
        return id_permission;
    }

    public void setId_permission(Long id_permission) {
        this.id_permission = id_permission;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return role.getName();
    }
}
