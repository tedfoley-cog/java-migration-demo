package com.acme.insurance.service;

import com.acme.insurance.dto.ClaimDTO;
import com.acme.insurance.model.Claim;
import com.acme.insurance.model.ClaimStatus;
import com.acme.insurance.model.Policy;
import com.acme.insurance.repository.ClaimRepository;
import com.acme.insurance.repository.PolicyRepository;
import com.acme.insurance.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private PolicyNumberGenerator policyNumberGenerator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuditService auditService;

    public List<ClaimDTO> getAllClaims() {
        List<Claim> claims = claimRepository.findAll();
        List<ClaimDTO> dtos = new ArrayList<ClaimDTO>();
        for (Claim claim : claims) {
            dtos.add(toDTO(claim));
        }
        return dtos;
    }

    public ClaimDTO getClaimById(Long id) {
        Claim claim = claimRepository.findById(id).orElse(null);
        if (claim == null) {
            return null;
        }
        return toDTO(claim);
    }

    public List<ClaimDTO> getClaimsByPolicyId(Long policyId) {
        List<Claim> claims = claimRepository.findByPolicyId(policyId);
        List<ClaimDTO> dtos = new ArrayList<ClaimDTO>();
        for (Claim claim : claims) {
            dtos.add(toDTO(claim));
        }
        return dtos;
    }

    // TODO: add input validation for claim amount — negative values are currently accepted
    @Transactional
    public ClaimDTO fileClaim(ClaimDTO dto) {
        Policy policy = policyRepository.findById(dto.getPolicyId()).orElse(null);
        if (policy == null) {
            throw new RuntimeException("Policy not found: " + dto.getPolicyId());
        }

        Claim claim = new Claim();
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
        claim.setFiledDate(new Date());

        Claim saved = claimRepository.save(claim);
        auditService.log("CLAIM", saved.getId(), "CREATED",
                null, "status=SUBMITTED, amount=" + saved.getClaimAmount(), "SYSTEM");
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
        Claim claim = claimRepository.findById(claimId).orElse(null);
        if (claim == null) {
            throw new RuntimeException("Claim not found: " + claimId);
        }

        String previousStatus = claim.getStatus().name();
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setApprovedAmount(approvedAmount);
        claim.setResolvedDate(new Date());

        // BUG: NPE if policy.getCustomer() is null (orphaned data, lazy-load issue)
        Policy policy = claim.getPolicy();
        String customerEmail = policy.getCustomer().getEmail();
        System.out.println("Sending approval notification to: " + customerEmail);

        Claim saved = claimRepository.save(claim);
        auditService.log("CLAIM", saved.getId(), "STATUS_CHANGED",
                previousStatus, "APPROVED", "SYSTEM");
        return toDTO(saved);
    }

    @Transactional
    public ClaimDTO denyClaim(Long claimId, String reason) {
        Claim claim = claimRepository.findById(claimId).orElse(null);
        if (claim == null) {
            throw new RuntimeException("Claim not found: " + claimId);
        }

        String previousStatus = claim.getStatus().name();
        claim.setStatus(ClaimStatus.DENIED);
        claim.setAdjusterNotes(reason);
        claim.setResolvedDate(new Date());

        Claim saved = claimRepository.save(claim);
        auditService.log("CLAIM", saved.getId(), "STATUS_CHANGED",
                previousStatus, "DENIED", "SYSTEM");
        return toDTO(saved);
    }

    public BigDecimal getTotalApprovedAmount() {
        BigDecimal total = claimRepository.getTotalApprovedClaimAmount();
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
        ClaimDTO dto = new ClaimDTO();
        dto.setId(claim.getId());
        dto.setClaimNumber(claim.getClaimNumber());
        dto.setStatus(claim.getStatus().name());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setApprovedAmount(claim.getApprovedAmount());
        dto.setIncidentDate(DateUtils.formatDate(claim.getIncidentDate()));
        dto.setFiledDate(DateUtils.formatDate(claim.getFiledDate()));
        dto.setResolvedDate(DateUtils.formatDate(claim.getResolvedDate()));
        dto.setDescription(claim.getDescription());
        dto.setAdjusterNotes(claim.getAdjusterNotes());
        if (claim.getPolicy() != null) {
            dto.setPolicyId(claim.getPolicy().getId());
            dto.setPolicyNumber(claim.getPolicy().getPolicyNumber());
        }
        return dto;
    }
}
