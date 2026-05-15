package com.acme.insurance.service;

import com.acme.insurance.dto.PolicyDTO;
import com.acme.insurance.model.Customer;
import com.acme.insurance.model.Policy;
import com.acme.insurance.model.PolicyStatus;
import com.acme.insurance.repository.CustomerRepository;
import com.acme.insurance.repository.PolicyRepository;
import com.acme.insurance.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PolicyService {

    // Legacy pattern: field injection instead of constructor injection
    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PolicyNumberGenerator policyNumberGenerator;

    @Autowired
    private PremiumCalculator premiumCalculator;

    @Autowired
    private AuditService auditService;

    // TODO: add pagination — this loads ALL policies into memory
    public List<PolicyDTO> getAllPolicies() {
        List<Policy> policies = policyRepository.findAllWithCustomer();
        List<PolicyDTO> dtos = new ArrayList<PolicyDTO>();
        for (Policy policy : policies) {
            dtos.add(toDTO(policy));
        }
        return dtos;
    }

    public PolicyDTO getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id).orElse(null);
        if (policy == null) {
            return null;
        }
        return toDTO(policy);
    }

    public PolicyDTO getPolicyByNumber(String policyNumber) {
        Policy policy = policyRepository.findByPolicyNumber(policyNumber).orElse(null);
        if (policy == null) {
            return null;
        }
        return toDTO(policy);
    }

    public List<PolicyDTO> getPoliciesByStatus(String status) {
        PolicyStatus policyStatus = PolicyStatus.valueOf(status.toUpperCase());
        List<Policy> policies = policyRepository.findByStatus(policyStatus);
        List<PolicyDTO> dtos = new ArrayList<PolicyDTO>();
        for (Policy policy : policies) {
            dtos.add(toDTO(policy));
        }
        return dtos;
    }

    @Transactional
    public PolicyDTO createPolicy(PolicyDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElse(null);
        if (customer == null) {
            throw new RuntimeException("Customer not found: " + dto.getCustomerId());
        }

        Policy policy = new Policy();
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

        Date now = new Date();
        policy.setCreatedAt(now);
        policy.setUpdatedAt(now);

        Policy saved = policyRepository.save(policy);
        auditService.log("POLICY", saved.getId(), "CREATED",
                null, "status=DRAFT, type=" + saved.getPolicyType(), "SYSTEM");
        return toDTO(saved);
    }

    @Transactional
    public PolicyDTO activatePolicy(Long id) {
        Policy policy = policyRepository.findById(id).orElse(null);
        if (policy == null) {
            throw new RuntimeException("Policy not found: " + id);
        }
        if (policy.getStatus() != PolicyStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT policies can be activated");
        }
        String previousStatus = policy.getStatus().name();
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setUpdatedAt(new Date());
        Policy saved = policyRepository.save(policy);
        auditService.log("POLICY", saved.getId(), "STATUS_CHANGED",
                previousStatus, "ACTIVE", "SYSTEM");
        return toDTO(saved);
    }

    @Transactional
    public PolicyDTO cancelPolicy(Long id) {
        Policy policy = policyRepository.findById(id).orElse(null);
        if (policy == null) {
            throw new RuntimeException("Policy not found: " + id);
        }
        String previousStatus = policy.getStatus().name();
        policy.setStatus(PolicyStatus.CANCELLED);
        policy.setUpdatedAt(new Date());

        BigDecimal refund = premiumCalculator.calculateProRataRefund(policy, new Date());
        // In production this would trigger a payment — here we just log it
        System.out.println("Pro-rata refund for " + policy.getPolicyNumber() + ": $" + refund);

        Policy saved = policyRepository.save(policy);
        auditService.log("POLICY", saved.getId(), "STATUS_CHANGED",
                previousStatus, "CANCELLED", "SYSTEM");
        return toDTO(saved);
    }

    public long countByStatus(String status) {
        return policyRepository.countByStatusNative(status);
    }

    private PolicyDTO toDTO(Policy policy) {
        PolicyDTO dto = new PolicyDTO();
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
