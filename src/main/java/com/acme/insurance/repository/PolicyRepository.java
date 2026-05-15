package com.acme.insurance.repository;

import com.acme.insurance.model.Policy;
import com.acme.insurance.model.PolicyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByPolicyNumber(String policyNumber);

    List<Policy> findByStatus(PolicyStatus status);

    List<Policy> findByCustomerId(Long customerId);

    @Query("SELECT p FROM Policy p WHERE p.expirationDate < :date AND p.status = 'ACTIVE'")
    List<Policy> findExpiredPolicies(@Param("date") LocalDate date);

    @Query("SELECT p FROM Policy p JOIN FETCH p.customer")
    List<Policy> findAllWithCustomer();

    @Query(value = "SELECT p FROM Policy p JOIN FETCH p.customer",
           countQuery = "SELECT COUNT(p) FROM Policy p")
    Page<Policy> findAllWithCustomer(Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM policies WHERE status = ?1", nativeQuery = true)
    long countByStatusNative(String status);
}
