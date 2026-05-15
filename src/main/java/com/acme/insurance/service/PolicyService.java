package com.acme.insurance.service;

import com.acme.insurance.dto.PolicyDTO;
import com.acme.insurance.model.Policy;
import com.acme.insurance.model.PolicyStatus;
import com.acme.insurance.repository.CustomerRepository;
import com.acme.insurance.repository.PolicyRepository;
import com.acme.insurance.util.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final PolicyNumberGenerator policyNumberGenerator;
    private final PremiumCalculator premiumCalculator;

    public PolicyService(PolicyRepository policyRepository,
                         CustomerRepository customerRepository,
                         PolicyNumberGenerator policyNumberGenerator,
                         PremiumCalculator premiumCalculator) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
        this.policyNumberGenerator = policyNumberGenerator;
        this.premiumCalculator = premiumCalculator;
    }

    public List<PolicyDTO> getAllPolicies() {
        return policyRepository.findAllWithCustomer().stream()
                .map(this::toDTO)
                .toList();
    }

    public Page<PolicyDTO> getAllPolicies(Pageable pageable) {
        return policyRepository.findAllWithCustomer(pageable)
                .map(this::toDTO);
    }

    public PolicyDTO getPolicyById(Long id) {
        return policyRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public PolicyDTO getPolicyByNumber(String policyNumber) {
        return policyRepository.findByPolicyNumber(policyNumber)
                .map(this::toDTO)
                .orElse(null);
    }

    public List<PolicyDTO> getPoliciesByStatus(String status) {
        var policyStatus = PolicyStatus.valueOf(status.toUpperCase());
        return policyRepository.findByStatus(policyStatus).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public PolicyDTO createPolicy(PolicyDTO dto) {
        var customer = customerRepository.findById(dto.getCustomerId()).orElse(null);
        if (customer == null) {
            throw new RuntimeException("Customer not found: " + dto.getCustomerId());
        }

        var policy = new Policy();
        policy.setPolicyNumber(policyNumberGenerator.nextPolicyNumber());
        policy.setPolicyType(dto.getPolicyType());
        policy.setStatus(PolicyStatus.DRAFT);
        policy.setAnnualPremium(premiumCalculator.calculateAnnualPremium(dto.getAnnualPremium()));
        policy.setCoverageAmount(dto.getCoverageAmount());
        policy.setDeductible(dto.getDeductible());
        policy.setDescription(dto.getDescription());
        policy.setCustomer(customer);

        if (dto.getEffectiveDate() != null) {
            policy.setEffectiveDate(DateUtils.parseDate(dto.getEffectiveDate()));
        } else {
            policy.setEffectiveDate(DateUtils.today());
        }
        policy.setExpirationDate(DateUtils.addYears(policy.getEffectiveDate(), 1));

        var now = LocalDateTime.now();
        policy.setCreatedAt(now);
        policy.setUpdatedAt(now);

        var saved = policyRepository.save(policy);
        return toDTO(saved);
    }

    @Transactional
    public PolicyDTO activatePolicy(Long id) {
        var policy = policyRepository.findById(id).orElse(null);
        if (policy == null) {
            throw new RuntimeException("Policy not found: " + id);
        }
        if (policy.getStatus() != PolicyStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT policies can be activated");
        }
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setUpdatedAt(LocalDateTime.now());
        var saved = policyRepository.save(policy);
        return toDTO(saved);
    }

    @Transactional
    public PolicyDTO cancelPolicy(Long id) {
        var policy = policyRepository.findById(id).orElse(null);
        if (policy == null) {
            throw new RuntimeException("Policy not found: " + id);
        }
        policy.setStatus(PolicyStatus.CANCELLED);
        policy.setUpdatedAt(LocalDateTime.now());

        var refund = premiumCalculator.calculateProRataRefund(policy, LocalDate.now());
        System.out.println("Pro-rata refund for " + policy.getPolicyNumber() + ": $" + refund);

        var saved = policyRepository.save(policy);
        return toDTO(saved);
    }

    public long countByStatus(String status) {
        return policyRepository.countByStatusNative(status);
    }

    private PolicyDTO toDTO(Policy policy) {
        var dto = new PolicyDTO();
        dto.setId(policy.getId());
        dto.setPolicyNumber(policy.getPolicyNumber());
        dto.setPolicyType(policy.getPolicyType());
        dto.setStatus(policy.getStatus().name());
        dto.setAnnualPremium(policy.getAnnualPremium());
        dto.setCoverageAmount(policy.getCoverageAmount());
        dto.setDeductible(policy.getDeductible());
        dto.setEffectiveDate(DateUtils.formatDate(policy.getEffectiveDate()));
        dto.setExpirationDate(DateUtils.formatDate(policy.getExpirationDate()));
        dto.setDescription(policy.getDescription());
        if (policy.getCustomer() != null) {
            dto.setCustomerId(policy.getCustomer().getId());
            dto.setCustomerName(policy.getCustomer().getFullName());
        }
        return dto;
    }
}
