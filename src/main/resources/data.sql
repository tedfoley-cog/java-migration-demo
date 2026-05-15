-- =============================================================================
-- Seed data for ACME Insurance Platform demo
-- =============================================================================

-- Customers
INSERT INTO customers (id, first_name, last_name, email, phone_number, date_of_birth, created_at)
VALUES (1, 'John', 'Smith', 'john.smith@example.com', '555-0101', '1978-03-15', '2023-01-10 09:00:00');

INSERT INTO customers (id, first_name, last_name, email, phone_number, date_of_birth, created_at)
VALUES (2, 'Sarah', 'Johnson', 'sarah.j@example.com', '555-0102', '1985-07-22', '2023-02-14 10:30:00');

INSERT INTO customers (id, first_name, last_name, email, phone_number, date_of_birth, created_at)
VALUES (3, 'Michael', 'Chen', 'mchen@example.com', '555-0103', '1992-11-08', '2023-03-20 14:15:00');

INSERT INTO customers (id, first_name, last_name, email, phone_number, date_of_birth, created_at)
VALUES (4, 'Emily', 'Davis', 'emily.d@example.com', NULL, '1990-05-30', '2023-04-05 11:00:00');

INSERT INTO customers (id, first_name, last_name, email, phone_number, date_of_birth, created_at)
VALUES (5, 'Robert', 'Wilson', 'rwilson@example.com', '555-0105', '1975-09-12', '2023-05-18 08:45:00');

-- Policies
INSERT INTO policies (id, policy_number, policy_type, status, annual_premium, coverage_amount,
                      deductible, effective_date, expiration_date, description, customer_id,
                      created_at, updated_at)
VALUES (1, 'POL-001001', 'HOME', 'ACTIVE', 2400.00, 350000.00, 1000.00,
        '2024-01-01', '2025-01-01', 'Homeowners insurance — single family dwelling',
        1, '2023-12-15 10:00:00', '2024-01-01 00:00:00');

INSERT INTO policies (id, policy_number, policy_type, status, annual_premium, coverage_amount,
                      deductible, effective_date, expiration_date, description, customer_id,
                      created_at, updated_at)
VALUES (2, 'POL-001002', 'AUTO', 'ACTIVE', 1800.00, 100000.00, 500.00,
        '2024-03-15', '2025-03-15', 'Auto insurance — full coverage, 2022 sedan',
        2, '2024-03-01 09:00:00', '2024-03-15 00:00:00');

INSERT INTO policies (id, policy_number, policy_type, status, annual_premium, coverage_amount,
                      deductible, effective_date, expiration_date, description, customer_id,
                      created_at, updated_at)
VALUES (3, 'POL-001003', 'LIFE', 'ACTIVE', 3600.00, 500000.00, 0.00,
        '2024-06-01', '2025-06-01', 'Term life insurance — 20-year term',
        3, '2024-05-20 14:00:00', '2024-06-01 00:00:00');

INSERT INTO policies (id, policy_number, policy_type, status, annual_premium, coverage_amount,
                      deductible, effective_date, expiration_date, description, customer_id,
                      created_at, updated_at)
VALUES (4, 'POL-001004', 'HOME', 'DRAFT', 2100.00, 280000.00, 1500.00,
        '2024-09-01', '2025-09-01', 'Homeowners insurance — condo unit',
        4, '2024-08-25 11:30:00', '2024-08-25 11:30:00');

INSERT INTO policies (id, policy_number, policy_type, status, annual_premium, coverage_amount,
                      deductible, effective_date, expiration_date, description, customer_id,
                      created_at, updated_at)
VALUES (5, 'POL-001005', 'AUTO', 'CANCELLED', 2200.00, 150000.00, 750.00,
        '2023-11-01', '2024-11-01', 'Auto insurance — two vehicles, liability + collision',
        5, '2023-10-15 08:00:00', '2024-04-10 16:00:00');

INSERT INTO policies (id, policy_number, policy_type, status, annual_premium, coverage_amount,
                      deductible, effective_date, expiration_date, description, customer_id,
                      created_at, updated_at)
VALUES (6, 'POL-001006', 'COMMERCIAL', 'ACTIVE', 8500.00, 1000000.00, 5000.00,
        '2024-02-01', '2025-02-01', 'Commercial property — small retail storefront',
        1, '2024-01-20 13:00:00', '2024-02-01 00:00:00');

INSERT INTO policies (id, policy_number, policy_type, status, annual_premium, coverage_amount,
                      deductible, effective_date, expiration_date, description, customer_id,
                      created_at, updated_at)
VALUES (7, 'POL-001007', 'LIFE', 'EXPIRED', 4200.00, 750000.00, 0.00,
        '2022-07-01', '2023-07-01', 'Term life insurance — expired, not renewed',
        5, '2022-06-15 10:00:00', '2023-07-01 00:00:00');

-- Claims
INSERT INTO claims (id, claim_number, status, claim_amount, approved_amount, incident_date,
                    filed_date, resolved_date, description, adjuster_notes, policy_id)
VALUES (1, 'CLM-005001', 'SETTLED', 12500.00, 11000.00, '2024-04-10',
        '2024-04-12 09:00:00', '2024-05-15 16:00:00',
        'Water damage from burst pipe in basement — affected flooring and drywall',
        'Verified damage. Deductible applied. Contractor estimate confirmed.', 1);

INSERT INTO claims (id, claim_number, status, claim_amount, approved_amount, incident_date,
                    filed_date, resolved_date, description, adjuster_notes, policy_id)
VALUES (2, 'CLM-005002', 'UNDER_REVIEW', 45000.00, NULL, '2024-07-20',
        '2024-07-22 14:30:00', NULL,
        'Rear-end collision at intersection — vehicle towed, driver uninjured',
        NULL, 2);

INSERT INTO claims (id, claim_number, status, claim_amount, approved_amount, incident_date,
                    filed_date, resolved_date, description, adjuster_notes, policy_id)
VALUES (3, 'CLM-005003', 'SUBMITTED', 8200.00, NULL, '2024-08-05',
        '2024-08-06 10:15:00', NULL,
        'Hail damage to roof — multiple shingles displaced after severe storm',
        NULL, 1);

INSERT INTO claims (id, claim_number, status, claim_amount, approved_amount, incident_date,
                    filed_date, resolved_date, description, adjuster_notes, policy_id)
VALUES (4, 'CLM-005004', 'DENIED', 3500.00, 0.00, '2024-06-15',
        '2024-06-18 11:00:00', '2024-07-10 09:30:00',
        'Windshield crack — claimed as vandalism',
        'Investigation found pre-existing damage. Claim denied per exclusion clause.', 2);

INSERT INTO claims (id, claim_number, status, claim_amount, approved_amount, incident_date,
                    filed_date, resolved_date, description, adjuster_notes, policy_id)
VALUES (5, 'CLM-005005', 'APPROVED', 75000.00, 70000.00, '2024-05-01',
        '2024-05-03 08:00:00', '2024-06-20 15:00:00',
        'Fire damage to retail storefront — electrical fault in back office',
        'Fire department report confirms electrical origin. Coverage approved minus deductible.', 6);

-- Audit Log
INSERT INTO audit_log (id, entity_type, entity_id, action, previous_value, new_value, performed_by, timestamp)
VALUES (1, 'POLICY', 1, 'CREATED', NULL, 'status=DRAFT, type=HOME', 'SYSTEM', '2023-12-15 10:00:00');

INSERT INTO audit_log (id, entity_type, entity_id, action, previous_value, new_value, performed_by, timestamp)
VALUES (2, 'POLICY', 1, 'STATUS_CHANGED', 'DRAFT', 'ACTIVE', 'SYSTEM', '2024-01-01 00:00:00');

INSERT INTO audit_log (id, entity_type, entity_id, action, previous_value, new_value, performed_by, timestamp)
VALUES (3, 'POLICY', 2, 'CREATED', NULL, 'status=DRAFT, type=AUTO', 'SYSTEM', '2024-03-01 09:00:00');

INSERT INTO audit_log (id, entity_type, entity_id, action, previous_value, new_value, performed_by, timestamp)
VALUES (4, 'POLICY', 2, 'STATUS_CHANGED', 'DRAFT', 'ACTIVE', 'SYSTEM', '2024-03-15 00:00:00');

INSERT INTO audit_log (id, entity_type, entity_id, action, previous_value, new_value, performed_by, timestamp)
VALUES (5, 'POLICY', 5, 'STATUS_CHANGED', 'ACTIVE', 'CANCELLED', 'SYSTEM', '2024-04-10 16:00:00');

INSERT INTO audit_log (id, entity_type, entity_id, action, previous_value, new_value, performed_by, timestamp)
VALUES (6, 'CLAIM', 1, 'CREATED', NULL, 'status=SUBMITTED, amount=12500.00', 'SYSTEM', '2024-04-12 09:00:00');

INSERT INTO audit_log (id, entity_type, entity_id, action, previous_value, new_value, performed_by, timestamp)
VALUES (7, 'CLAIM', 1, 'STATUS_CHANGED', 'SUBMITTED', 'SETTLED', 'SYSTEM', '2024-05-15 16:00:00');

INSERT INTO audit_log (id, entity_type, entity_id, action, previous_value, new_value, performed_by, timestamp)
VALUES (8, 'CLAIM', 4, 'CREATED', NULL, 'status=SUBMITTED, amount=3500.00', 'SYSTEM', '2024-06-18 11:00:00');

INSERT INTO audit_log (id, entity_type, entity_id, action, previous_value, new_value, performed_by, timestamp)
VALUES (9, 'CLAIM', 4, 'STATUS_CHANGED', 'SUBMITTED', 'DENIED', 'SYSTEM', '2024-07-10 09:30:00');

INSERT INTO audit_log (id, entity_type, entity_id, action, previous_value, new_value, performed_by, timestamp)
VALUES (10, 'CLAIM', 5, 'STATUS_CHANGED', 'SUBMITTED', 'APPROVED', 'SYSTEM', '2024-06-20 15:00:00');
