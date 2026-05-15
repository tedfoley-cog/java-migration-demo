package com.acme.insurance.service;

import com.acme.insurance.dto.ClaimDTO;
import com.acme.insurance.model.Claim;
import com.acme.insurance.model.ClaimStatus;
import com.acme.insurance.model.Policy;
import com.acme.insurance.repository.ClaimRepository;
import com.acme.insurance.repository.PolicyRepository;
import com.acme.insurance.util.DateUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final PolicyNumberGenerator policyNumberGenerator;
    private final JdbcTemplate jdbcTemplate;

    public ClaimService(ClaimRepository claimRepository,
                        PolicyRepository policyRepository,
                        PolicyNumberGenerator policyNumberGenerator,
                        JdbcTemplate jdbcTemplate) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
        this.policyNumberGenerator = policyNumberGenerator;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ClaimDTO> getAllClaims() {
        return claimRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public ClaimDTO getClaimById(Long id) {
        return claimRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public List<ClaimDTO> getClaimsByPolicyId(Long policyId) {
        return claimRepository.findByPolicyId(policyId).stream()
                .map(this::toDTO)
                .toList();
    }

    // TODO: add input validation for claim amount — negative values are currently accepted
    @Transactional
    public ClaimDTO fileClaim(ClaimDTO dto) {
        var policy = policyRepository.findById(dto.getPolicyId()).orElse(null);
        if (policy == null) {
            throw new RuntimeException("Policy not found: " + dto.getPolicyId());
        }

        var claim = new Claim();
        claim.setClaimNumber(policyNumberGenerator.nextClaimNumber());
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setClaimAmount(dto.getClaimAmount());
        claim.setDescription(dto.getDescription());
        claim.setPolicy(policy);

        if (dto.getIncidentDate() != null) {
            claim.setIncidentDate(DateUtils.parseDate(dto.getIncidentDate()));
        } else {
            claim.setIncidentDate(DateUtils.today());
        }
        claim.setFiledDate(LocalDateTime.now());

        var saved = claimRepository.save(claim);
        return toDTO(saved);
    }

    /**
     * Approve a claim and calculate the approved amount.
     *
     * BUG: NullPointerException when the policy's customer is null (can happen with
     * orphaned test data or if the customer was deleted). The code dereferences
     * policy.getCustomer().getEmail() without a null check, causing an NPE
     * in the notification step.
     */
    @Transactional
    public ClaimDTO approveClaim(Long claimId, BigDecimal approvedAmount) {
        var claim = claimRepository.findById(claimId).orElse(null);
        if (claim == null) {
            throw new RuntimeException("Claim not found: " + claimId);
        }

        claim.setStatus(ClaimStatus.APPROVED);
        claim.setApprovedAmount(approvedAmount);
        claim.setResolvedDate(LocalDateTime.now());

        // BUG: NPE if policy.getCustomer() is null (orphaned data, lazy-load issue)
        var policy = claim.getPolicy();
        var customerEmail = policy.getCustomer().getEmail();
        System.out.println("Sending approval notification to: " + customerEmail);

        var saved = claimRepository.save(claim);
        return toDTO(saved);
    }

    @Transactional
    public ClaimDTO denyClaim(Long claimId, String reason) {
        var claim = claimRepository.findById(claimId).orElse(null);
        if (claim == null) {
            throw new RuntimeException("Claim not found: " + claimId);
        }

        claim.setStatus(ClaimStatus.DENIED);
        claim.setAdjusterNotes(reason);
        claim.setResolvedDate(LocalDateTime.now());

        var saved = claimRepository.save(claim);
        return toDTO(saved);
    }

    public BigDecimal getTotalApprovedAmount() {
        var total = claimRepository.getTotalApprovedClaimAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Raw SQL query to get claim statistics — legacy pattern, should use
     * repository methods or JPQL instead.
     */
    public List<Map<String, Object>> getClaimStatsByStatus() {
        return jdbcTemplate.queryForList(
                "SELECT status, COUNT(*) as count, SUM(claim_amount) as total_amount " +
                "FROM claims GROUP BY status ORDER BY count DESC");
    }

    private ClaimDTO toDTO(Claim claim) {
        var dto = new ClaimDTO();
        dto.setId(claim.getId());
        dto.setClaimNumber(claim.getClaimNumber());
        dto.setStatus(claim.getStatus().name());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setApprovedAmount(claim.getApprovedAmount());
        dto.setIncidentDate(DateUtils.formatDate(claim.getIncidentDate()));
        dto.setFiledDate(DateUtils.formatDateTime(claim.getFiledDate()));
        dto.setResolvedDate(DateUtils.formatDateTime(claim.getResolvedDate()));
        dto.setDescription(claim.getDescription());
        dto.setAdjusterNotes(claim.getAdjusterNotes());
        if (claim.getPolicy() != null) {
            dto.setPolicyId(claim.getPolicy().getId());
            dto.setPolicyNumber(claim.getPolicy().getPolicyNumber());
        }
        return dto;
    }
}
