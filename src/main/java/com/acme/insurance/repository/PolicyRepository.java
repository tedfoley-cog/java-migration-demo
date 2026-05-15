package com.acme.insurance.repository;

import com.acme.insurance.model.Policy;
import com.acme.insurance.model.PolicyStatus;
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

    // TODO: add pagination support — currently returns all policies which won't scale
    @Query("SELECT p FROM Policy p JOIN FETCH p.customer")
    List<Policy> findAllWithCustomer();

    @Query(value = "SELECT COUNT(*) FROM policies WHERE status = ?1", nativeQuery = true)
    long countByStatusNative(String status);
}
