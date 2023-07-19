package com.simbioff.simbioff.services;

import com.simbioff.simbioff.models.PermissionModel;
import com.simbioff.simbioff.repositories.Users_permissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsersPermissions {

    @Autowired
    Users_permissionRepository usersPermissionsRepository;

    public void insertUserPermission(UUID id_user, Long id_permission) {
        usersPermissionsRepository.insertWithQuery(id_user, id_permission);
    }


   }
