package com.simbioff.simbioff.repositories;

import com.simbioff.simbioff.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.StyledEditorKit;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {

    UserModel findByEmail(String email);

    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    boolean existsByPixKey(String pixKey);
    
    boolean existsByPhone(String phone);
    
    boolean existsById(UUID id);

    @Transactional
    void deleteByEmail(String email);

    UserModel findByToken(String token);

    Page<UserModel> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    @Query(value = "SELECT u from UserModel u WHERE u.enabled = :enabled")
    Page<UserModel> findEnableUsers(Pageable pageable, @Param("enabled") Boolean enabled);

    Optional<UserModel> findByIdUser(UUID idUser);


    @Query(value = "SELECT t FROM UserModel t WHERE t.enabled = true AND (LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword,'%')) " +
            "OR LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword,'%')) Or LOWER(t.pixKey) " +
            "LIKE LOWER(CONCAT('%', :keyword,'%')) OR LOWER(t.phone) LIKE LOWER(CONCAT('%', :keyword,'%')))")
    Page<UserModel> findByKeywordTrue(String keyword, Pageable pageable);

    @Query(value = "SELECT t FROM UserModel t WHERE t.enabled = false AND (LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword,'%')) " +
            "OR LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword,'%')) Or LOWER(t.pixKey) " +
            "LIKE LOWER(CONCAT('%', :keyword,'%')) OR LOWER(t.phone) LIKE LOWER(CONCAT('%', :keyword,'%')))")
    Page<UserModel> findByKeywordFalse(String keyword, Pageable pageable);

	


}
