package com.acme.insurance.controller;

import com.acme.insurance.dto.PolicyDTO;
import com.acme.insurance.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policies", description = "Insurance policy management endpoints")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @GetMapping
    @Operation(summary = "List all policies", description = "Returns all policies. WARNING: no pagination — loads entire table.")
    public ResponseEntity<List<PolicyDTO>> getAllPolicies() {
        List<PolicyDTO> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get policy by ID")
    public ResponseEntity<PolicyDTO> getPolicyById(@PathVariable Long id) {
        PolicyDTO policy = policyService.getPolicyById(id);
        if (policy == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(policy);
    }

    @GetMapping("/number/{policyNumber}")
    @Operation(summary = "Get policy by policy number")
    public ResponseEntity<PolicyDTO> getPolicyByNumber(@PathVariable String policyNumber) {
        PolicyDTO policy = policyService.getPolicyByNumber(policyNumber);
        if (policy == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(policy);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get policies by status", description = "Valid statuses: DRAFT, ACTIVE, SUSPENDED, CANCELLED, EXPIRED, LAPSED")
    public ResponseEntity<List<PolicyDTO>> getPoliciesByStatus(@PathVariable String status) {
        List<PolicyDTO> policies = policyService.getPoliciesByStatus(status);
        return ResponseEntity.ok(policies);
    }

    @PostMapping
    @Operation(summary = "Create a new policy", description = "Creates a draft policy. Requires customerId in the request body.")
    public ResponseEntity<PolicyDTO> createPolicy(@RequestBody PolicyDTO policyDTO) {
        PolicyDTO created = policyService.createPolicy(policyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate a draft policy")
    public ResponseEntity<PolicyDTO> activatePolicy(@PathVariable Long id) {
        PolicyDTO activated = policyService.activatePolicy(id);
        return ResponseEntity.ok(activated);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an active policy", description = "Calculates pro-rata refund and cancels the policy.")
    public ResponseEntity<PolicyDTO> cancelPolicy(@PathVariable Long id) {
        PolicyDTO cancelled = policyService.cancelPolicy(id);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/count")
    @Operation(summary = "Count policies by status")
    public ResponseEntity<Long> countByStatus(@RequestParam String status) {
        long count = policyService.countByStatus(status);
        return ResponseEntity.ok(count);
    }
}
