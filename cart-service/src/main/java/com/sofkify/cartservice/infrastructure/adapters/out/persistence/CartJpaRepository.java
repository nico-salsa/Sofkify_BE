package com.sofkify.cartservice.infrastructure.adapters.out.persistence;

import com.sofkify.cartservice.domain.model.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartJpaRepository extends JpaRepository<CartJpaEntity, UUID> {
    
    Optional<CartJpaEntity> findByCustomerId(UUID customerId);
    
    Optional<CartJpaEntity> findByCustomerIdAndStatus(UUID customerId, CartStatus status);
    
    @Query("SELECT c FROM CartJpaEntity c WHERE c.status = 'ACTIVE' AND c.updatedAt < :cutoff")
    List<CartJpaEntity> findActiveCartsNotUpdatedSince(@Param("cutoff") LocalDateTime cutoff);
}
