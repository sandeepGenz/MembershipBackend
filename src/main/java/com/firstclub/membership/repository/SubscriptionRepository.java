package com.firstclub.membership.repository;

import com.firstclub.membership.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    @Query("select s from Subscription s where s.user.externalUserId = :ext and s.status = 'ACTIVE' and s.endAt > :now")
    Optional<Subscription> findActiveByUserExternalId(@Param("ext") String externalUserId, @Param("now") Instant now);

    Optional<Subscription> findByIdempotencyKey(String idempotencyKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Subscription s where s.id = :id")
    Optional<Subscription> findByIdForUpdate(@Param("id") Long id);

    @Query("select s from Subscription s where s.status = 'ACTIVE' and s.endAt <= :now")
    List<Subscription> findExpired(@Param("now") Instant now);
}