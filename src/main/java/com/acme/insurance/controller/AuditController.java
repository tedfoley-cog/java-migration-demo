package com.acme.insurance.controller;

import com.acme.insurance.dto.AuditLogDTO;
import com.acme.insurance.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit Trail", description = "Audit trail for policy and claim state changes")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @GetMapping
    @Operation(summary = "List audit entries", description = "Returns all audit entries. Supports optional filtering by entityType (POLICY/CLAIM) and entityId.")
    public ResponseEntity<List<AuditLogDTO>> getAuditEntries(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long entityId) {
        List<AuditLogDTO> entries = auditService.getAllAuditEntries(entityType, entityId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single audit entry by ID")
    public ResponseEntity<AuditLogDTO> getAuditEntryById(@PathVariable Long id) {
        AuditLogDTO entry = auditService.getAuditEntryById(id);
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(entry);
    }
}
