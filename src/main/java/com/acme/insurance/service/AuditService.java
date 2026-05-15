package com.acme.insurance.service;

import com.acme.insurance.dto.AuditLogDTO;
import com.acme.insurance.model.AuditLog;
import com.acme.insurance.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional
    public void log(String entityType, Long entityId, String action,
                    String previousValue, String newValue, String performedBy) {
        AuditLog entry = new AuditLog();
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setAction(action);
        entry.setPreviousValue(previousValue);
        entry.setNewValue(newValue);
        entry.setPerformedBy(performedBy);
        entry.setTimestamp(new Date());
        auditLogRepository.save(entry);
    }

    public List<AuditLogDTO> getAllAuditEntries(String entityType, Long entityId) {
        List<AuditLog> entries;
        if (entityType != null && entityId != null) {
            entries = auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
        } else if (entityType != null) {
            entries = auditLogRepository.findByEntityType(entityType);
        } else if (entityId != null) {
            entries = auditLogRepository.findByEntityId(entityId);
        } else {
            entries = auditLogRepository.findAll();
        }
        List<AuditLogDTO> dtos = new ArrayList<AuditLogDTO>();
        for (AuditLog entry : entries) {
            dtos.add(toDTO(entry));
        }
        return dtos;
    }

    public AuditLogDTO getAuditEntryById(Long id) {
        AuditLog entry = auditLogRepository.findById(id).orElse(null);
        if (entry == null) {
            return null;
        }
        return toDTO(entry);
    }

    public List<AuditLogDTO> getRecentAuditEntries() {
        List<AuditLog> entries = auditLogRepository.findTop10ByOrderByTimestampDesc();
        List<AuditLogDTO> dtos = new ArrayList<AuditLogDTO>();
        for (AuditLog entry : entries) {
            dtos.add(toDTO(entry));
        }
        return dtos;
    }

    private AuditLogDTO toDTO(AuditLog entry) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(entry.getId());
        dto.setEntityType(entry.getEntityType());
        dto.setEntityId(entry.getEntityId());
        dto.setAction(entry.getAction());
        dto.setPreviousValue(entry.getPreviousValue());
        dto.setNewValue(entry.getNewValue());
        dto.setPerformedBy(entry.getPerformedBy());
        if (entry.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dto.setTimestamp(sdf.format(entry.getTimestamp()));
        }
        return dto;
    }
}
