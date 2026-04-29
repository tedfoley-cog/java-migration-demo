package com.acme.insurance.repository;

import com.acme.insurance.model.Claim;
import com.acme.insurance.model.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<Claim> findByClaimNumber(String claimNumber);

    List<Claim> findByStatus(ClaimStatus status);

    List<Claim> findByPolicyId(Long policyId);

    @Query("SELECT SUM(c.claimAmount) FROM Claim c WHERE c.status = 'APPROVED' OR c.status = 'SETTLED'")
    BigDecimal getTotalApprovedClaimAmount();

    @Query(value = "SELECT * FROM claims WHERE claim_amount > ?1 ORDER BY claim_amount DESC", nativeQuery = true)
    List<Claim> findHighValueClaims(BigDecimal threshold);
}
