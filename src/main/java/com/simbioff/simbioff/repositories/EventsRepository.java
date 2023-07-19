package com.simbioff.simbioff.repositories;

import com.simbioff.simbioff.models.EventsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventsRepository extends JpaRepository<EventsModel, Integer> {

    @Query(value = "SElECT COUNT (id_user) FROM events WHERE (id_user = ?1 AND type_day='HALF_DAYOFF') AND status IN ('PENDING', 'APPROVED') ", nativeQuery = true)
    long countHalfDayOffs(UUID id_user);

    @Query(value = "SElECT COUNT (id_user) FROM events WHERE (id_user = ?1 AND type_day='DAYOFF') AND status IN ('PENDING', 'APPROVED' )", nativeQuery = true)
    long countDayOff(UUID id_user);

    @Override
    boolean existsById(Integer id);

    @Override
    EventsModel getReferenceById(Integer integer);

    @Override
    void deleteById(Integer integer);

    @Query("SELECT e FROM EventsModel e WHERE e.id = :eventId")
    EventsModel getEvent(@Param("eventId") int eventId);

    List<EventsModel> findByIdUser(UUID idUser);

    EventsModel findById(int id);

    @Query(value = "SELECT * FROM events WHERE status = 'PENDING' ", nativeQuery = true)
    Page<EventsModel> findAll(Pageable pageable);


    @Query(value = "SELECT * FROM events WHERE status = :statusDay", nativeQuery = true)
    Page<EventsModel> findByStatus(String statusDay, Pageable pageable);

    @Query(value = "SELECT * FROM events WHERE id_user = :idUSer AND status = :statusDay", nativeQuery = true)
    Page<EventsModel> findByIdUserAndStatus(UUID idUSer, String statusDay, Pageable pageable);
}
