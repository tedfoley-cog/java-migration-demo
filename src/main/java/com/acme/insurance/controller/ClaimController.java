package com.acme.insurance.controller;

import com.acme.insurance.dto.ClaimDTO;
import com.acme.insurance.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
@Tag(name = "Claims", description = "Insurance claims processing endpoints")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    @Operation(summary = "List all claims")
    public ResponseEntity<List<ClaimDTO>> getAllClaims() {
        var claims = claimService.getAllClaims();
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get claim by ID")
    public ResponseEntity<ClaimDTO> getClaimById(@PathVariable Long id) {
        var claim = claimService.getClaimById(id);
        if (claim == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(claim);
    }

    @GetMapping("/policy/{policyId}")
    @Operation(summary = "Get claims for a specific policy")
    public ResponseEntity<List<ClaimDTO>> getClaimsByPolicy(@PathVariable Long policyId) {
        var claims = claimService.getClaimsByPolicyId(policyId);
        return ResponseEntity.ok(claims);
    }

    @PostMapping
    @Operation(summary = "File a new claim", description = "Submit a claim against an existing policy. Requires policyId.")
    public ResponseEntity<ClaimDTO> fileClaim(@RequestBody ClaimDTO claimDTO) {
        var filed = claimService.fileClaim(claimDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(filed);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a claim", description = "Approve with a specified amount. May trigger NPE on orphaned data — known bug.")
    public ResponseEntity<ClaimDTO> approveClaim(
            @PathVariable Long id,
            @RequestParam BigDecimal approvedAmount) {
        var approved = claimService.approveClaim(id, approvedAmount);
        return ResponseEntity.ok(approved);
    }

    @PutMapping("/{id}/deny")
    @Operation(summary = "Deny a claim")
    public ResponseEntity<ClaimDTO> denyClaim(
            @PathVariable Long id,
            @RequestParam String reason) {
        var denied = claimService.denyClaim(id, reason);
        return ResponseEntity.ok(denied);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get claim statistics by status", description = "Raw SQL aggregation — legacy pattern")
    public ResponseEntity<List<Map<String, Object>>> getClaimStats() {
        var stats = claimService.getClaimStatsByStatus();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/total-approved")
    @Operation(summary = "Get total approved claim amount")
    public ResponseEntity<BigDecimal> getTotalApproved() {
        var total = claimService.getTotalApprovedAmount();
        return ResponseEntity.ok(total);
    }
}
