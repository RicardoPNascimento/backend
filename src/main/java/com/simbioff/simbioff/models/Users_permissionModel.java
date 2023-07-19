package com.simbioff.simbioff.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;


@Entity
@Table(name = "users_permission")
public class Users_permissionModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column
    private UUID id_user;
    @Column
    private Long id_permission;
}
