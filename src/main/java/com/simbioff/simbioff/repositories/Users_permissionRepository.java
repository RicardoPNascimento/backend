package com.simbioff.simbioff.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.UUID;

@Repository
public class Users_permissionRepository {


    @Autowired
    EntityManager entityManager = null;

    @Transactional
    public void insertWithQuery(UUID user, Long id_permission) {
        entityManager.createNativeQuery("INSERT INTO users_permission (id_user, id_permission) VALUES (?, ?)")
                .setParameter(1, user)
                .setParameter(2, id_permission)
                .executeUpdate();
    }

}
