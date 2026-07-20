-- =====================================================================
-- MASTER DATA SEED
-- Runs after Hibernate creates the schema (spring.jpa.defer-datasource-
-- initialization=true, spring.sql.init.mode=always).
--
-- Nothing here is read by any Java code directly - it is all consumed
-- through the master-data services (DepartmentService, RoleService,
-- DesignationService, LanguageService, HierarchyRuleService). Adding a
-- brand-new department later is exactly this: more rows, via the
-- /admin/departments, /admin/roles and /admin/hierarchy-rules APIs -
-- never a code change.
-- =====================================================================

-- ---------------------------------------------------------------------
-- DEPARTMENTS
-- ---------------------------------------------------------------------
insert into departments (code, name, description, active, created_at, updated_at) values
('SALES',      'Sales',                 'Field sales and relationship management',        true, now(), now()),
('CREDIT',     'Credit',                'Underwriting and credit appraisal',               true, now(), now()),
('RISK',       'Risk',                  'Enterprise risk management',                      true, now(), now()),
('LEGAL',      'Legal',                 'Legal and compliance',                            true, now(), now()),
('IT',         'Information Technology','Technology and systems',                          true, now(), now()),
('OPERATIONS', 'Operations',            'Loan operations and processing',                  true, now(), now()),
('HR',         'Human Resources',       'Human resources and people operations',           true, now(), now()),
('ACCOUNTS',   'Accounts',              'Accounts and payables',                           true, now(), now()),
('INSURANCE',  'Insurance',             'Insurance and bancassurance',                     true, now(), now()),
('FINANCE',    'Finance',               'Finance, treasury and planning',                  true, now(), now()),
('AUDIT',      'Audit',                 'Internal audit',                                  true, now(), now()),
('TRAINING',   'Training',              'Learning and development',                        true, now(), now()),
('EXECUTIVE',  'Executive',             'Company-wide executive leadership',               true, now(), now());

-- ---------------------------------------------------------------------
-- LANGUAGES (for branch regional-language document generation)
-- ---------------------------------------------------------------------
insert into languages (code, name, active, created_at, updated_at) values
('en', 'English',   true, now(), now()),
('hi', 'Hindi',      true, now(), now()),
('mr', 'Marathi',    true, now(), now()),
('gu', 'Gujarati',   true, now(), now()),
('ta', 'Tamil',      true, now(), now()),
('te', 'Telugu',     true, now(), now()),
('kn', 'Kannada',    true, now(), now()),
('bn', 'Bengali',    true, now(), now()),
('pa', 'Punjabi',    true, now(), now()),
('ml', 'Malayalam',  true, now(), now()),
('or', 'Odia',       true, now(), now()),
('as', 'Assamese',   true, now(), now());

-- ---------------------------------------------------------------------
-- DESIGNATIONS (department-agnostic title ladder; HR can add
-- department-specific ones later via /admin/designations)
-- ---------------------------------------------------------------------
insert into designations (designation_id, name, department_code, active, created_at, updated_at) values
('TRAINEE',      'Trainee',                null, true, now(), now()),
('EXECUTIVE',    'Executive',              null, true, now(), now()),
('SR_EXECUTIVE', 'Senior Executive',       null, true, now(), now()),
('ASST_MANAGER', 'Assistant Manager',      null, true, now(), now()),
('MANAGER',      'Manager',                null, true, now(), now()),
('SR_MANAGER',   'Senior Manager',         null, true, now(), now()),
('AVP',          'Assistant Vice President', null, true, now(), now()),
('VP',           'Vice President',         null, true, now(), now()),
('SVP',          'Senior Vice President',  null, true, now(), now()),
('HEAD',         'Head',                   null, true, now(), now());

-- =====================================================================
-- ROLES
-- columns: role_id, role_name, department_code, is_top_level,
--          single_branch_only, requires_manager_branch_align,
--          max_per_branch, max_direct_reports, active, created_at, updated_at
-- =====================================================================

-- SALES --------------------------------------------------------------
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('FIELD_OFFICER',              'Field Officer',              'SALES', false, true,  false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('RELATIONSHIP_OFFICER',       'Relationship Officer',       'SALES', false, true,  false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('RELATIONSHIP_MANAGER',       'Relationship Manager',       'SALES', false, true,  false, 2,    2,    true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('TERRITORY_MANAGER',          'Territory Manager',          'SALES', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('CLUSTER_BUSINESS_MANAGER',   'Cluster Business Manager',   'SALES', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('DIVISIONAL_BRANCH_MANAGER',  'Divisional Branch Manager',  'SALES', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('ZONAL_BUSINESS_MANAGER',     'Zonal Business Manager',     'SALES', false, false, false, null, null, true, now(), now());

-- CREDIT ---------------------------------------------------------------
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('CREDIT_ANALYST',             'Credit Analyst',             'CREDIT', false, true,  false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('BRANCH_CREDIT_MANAGER',      'Branch Credit Manager',      'CREDIT', false, false, true,  null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('CLUSTER_CREDIT_MANAGER',     'Cluster Credit Manager',     'CREDIT', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('DIVISIONAL_CREDIT_MANAGER',  'Divisional Credit Manager',  'CREDIT', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('ZONAL_CREDIT_MANAGER',       'Zonal Credit Manager',       'CREDIT', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('CREDIT_HEAD',                'Credit Head',                'CREDIT', true,  false, false, null, null, true, now(), now());

-- EXECUTIVE --------------------------------------------------------------
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('BUSINESS_HEAD', 'Business Head',                  'EXECUTIVE', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('DY_CEO',        'Deputy CEO',                     'EXECUTIVE', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('CEO',           'Chief Executive Officer',        'EXECUTIVE', true,  false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('MD',            'Managing Director',              'EXECUTIVE', true,  false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('CTO',           'Chief Technology Officer',       'EXECUTIVE', true,  false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('CHRO',          'Chief Human Resources Officer',  'EXECUTIVE', true,  false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('CFO',           'Chief Financial Officer',        'EXECUTIVE', true,  false, false, null, null, true, now(), now());

-- RISK ---------------------------------------------------------------
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('RISK_ANALYST', 'Risk Analyst', 'RISK', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('RISK_MANAGER', 'Risk Manager', 'RISK', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('RISK_HEAD',    'Risk Head',    'RISK', true,  false, false, null, null, true, now(), now());

-- AUDIT ---------------------------------------------------------------
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('AUDIT_EXECUTIVE', 'Audit Executive', 'AUDIT', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('AUDIT_MANAGER',   'Audit Manager',   'AUDIT', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('AUDIT_HEAD',      'Audit Head',      'AUDIT', true,  false, false, null, null, true, now(), now());

-- GENERIC 3-TIER PATTERN for the remaining departments ------------------
-- (Executive -> Manager -> Head -> Business Head). HR/Admin can refine
-- any of these later purely through the master-data APIs.
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('LEGAL_EXECUTIVE',      'Legal Executive',      'LEGAL',      false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('LEGAL_MANAGER',        'Legal Manager',        'LEGAL',      false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('LEGAL_HEAD',           'Legal Head',           'LEGAL',      false, false, false, null, null, true, now(), now());

insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('IT_EXECUTIVE',         'IT Executive',         'IT',         false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('IT_MANAGER',           'IT Manager',           'IT',         false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('IT_HEAD',              'IT Head',              'IT',         false, false, false, null, null, true, now(), now());

insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('OPERATIONS_EXECUTIVE', 'Operations Executive', 'OPERATIONS', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('OPERATIONS_MANAGER',   'Operations Manager',   'OPERATIONS', false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('OPERATIONS_HEAD',      'Operations Head',      'OPERATIONS', false, false, false, null, null, true, now(), now());

insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('HR_EXECUTIVE',         'HR Executive',         'HR',         false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('HR_MANAGER',           'HR Manager',           'HR',         false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('HR_HEAD',              'HR Head',              'HR',         false, false, false, null, null, true, now(), now());

insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('ACCOUNTS_EXECUTIVE',   'Accounts Executive',   'ACCOUNTS',   false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('ACCOUNTS_MANAGER',     'Accounts Manager',     'ACCOUNTS',   false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('ACCOUNTS_HEAD',        'Accounts Head',        'ACCOUNTS',   false, false, false, null, null, true, now(), now());

insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('INSURANCE_EXECUTIVE',  'Insurance Executive',  'INSURANCE',  false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('INSURANCE_MANAGER',    'Insurance Manager',    'INSURANCE',  false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('INSURANCE_HEAD',       'Insurance Head',       'INSURANCE',  false, false, false, null, null, true, now(), now());

insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('FINANCE_EXECUTIVE',    'Finance Executive',    'FINANCE',    false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('FINANCE_MANAGER',      'Finance Manager',      'FINANCE',    false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('FINANCE_HEAD',         'Finance Head',         'FINANCE',    false, false, false, null, null, true, now(), now());

insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('TRAINING_EXECUTIVE',   'Training Executive',   'TRAINING',   false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('TRAINING_MANAGER',     'Training Manager',     'TRAINING',   false, false, false, null, null, true, now(), now());
insert into roles (role_id, role_name, department_code, is_top_level, single_branch_only, requires_manager_branch_align, max_per_branch, max_direct_reports, active, created_at, updated_at) values ('TRAINING_HEAD',        'Training Head',        'TRAINING',   false, false, false, null, null, true, now(), now());

-- =====================================================================
-- HIERARCHY RULES
-- columns: id (auto), department_code, from_role_id, to_role_id,
--          branch_type (null = any), priority, active, created_at, updated_at
-- =====================================================================

-- SALES: RO/FO -> RM -> TM (if present) -> CBM (if present)
--        -> DBM (if present) -> ZBM ; DBM & ZBM both -> Business Head directly
insert into hierarchy_rules (department_code, from_role_id, to_role_id, branch_type, priority, active, created_at, updated_at) values
('SALES', 'FIELD_OFFICER',             'RELATIONSHIP_MANAGER',      null, 1, true, now(), now()),
('SALES', 'RELATIONSHIP_OFFICER',      'RELATIONSHIP_MANAGER',      null, 1, true, now(), now()),
('SALES', 'RELATIONSHIP_MANAGER',      'TERRITORY_MANAGER',         null, 1, true, now(), now()),
('SALES', 'RELATIONSHIP_MANAGER',      'CLUSTER_BUSINESS_MANAGER',  null, 2, true, now(), now()),
('SALES', 'RELATIONSHIP_MANAGER',      'DIVISIONAL_BRANCH_MANAGER', null, 3, true, now(), now()),
('SALES', 'RELATIONSHIP_MANAGER',      'ZONAL_BUSINESS_MANAGER',    null, 4, true, now(), now()),
('SALES', 'TERRITORY_MANAGER',         'CLUSTER_BUSINESS_MANAGER',  null, 1, true, now(), now()),
('SALES', 'TERRITORY_MANAGER',         'DIVISIONAL_BRANCH_MANAGER', null, 2, true, now(), now()),
('SALES', 'TERRITORY_MANAGER',         'ZONAL_BUSINESS_MANAGER',    null, 3, true, now(), now()),
('SALES', 'CLUSTER_BUSINESS_MANAGER',  'DIVISIONAL_BRANCH_MANAGER', null, 1, true, now(), now()),
('SALES', 'CLUSTER_BUSINESS_MANAGER',  'ZONAL_BUSINESS_MANAGER',    null, 2, true, now(), now()),
('SALES', 'DIVISIONAL_BRANCH_MANAGER', 'BUSINESS_HEAD',             null, 1, true, now(), now()),
('SALES', 'ZONAL_BUSINESS_MANAGER',    'BUSINESS_HEAD',              null, 1, true, now(), now());

-- CREDIT: Head-Office Credit Analysts (pre-sanctioning) skip the BCM chain
-- entirely and report straight to the Zonal Credit Manager. BCM (at a
-- normal branch) climbs Cluster -> Divisional -> Zonal Credit Manager,
-- whichever exists.
insert into hierarchy_rules (department_code, from_role_id, to_role_id, branch_type, priority, active, created_at, updated_at) values
('CREDIT', 'CREDIT_ANALYST',            'ZONAL_CREDIT_MANAGER',      'HEAD_OFFICE', 1, true, now(), now()),
('CREDIT', 'BRANCH_CREDIT_MANAGER',     'CLUSTER_CREDIT_MANAGER',    null, 1, true, now(), now()),
('CREDIT', 'BRANCH_CREDIT_MANAGER',     'DIVISIONAL_CREDIT_MANAGER', null, 2, true, now(), now()),
('CREDIT', 'BRANCH_CREDIT_MANAGER',     'ZONAL_CREDIT_MANAGER',      null, 3, true, now(), now()),
('CREDIT', 'CLUSTER_CREDIT_MANAGER',    'DIVISIONAL_CREDIT_MANAGER', null, 1, true, now(), now()),
('CREDIT', 'CLUSTER_CREDIT_MANAGER',    'ZONAL_CREDIT_MANAGER',      null, 2, true, now(), now()),
('CREDIT', 'DIVISIONAL_CREDIT_MANAGER', 'ZONAL_CREDIT_MANAGER',      null, 1, true, now(), now()),
('CREDIT', 'ZONAL_CREDIT_MANAGER',      'BUSINESS_HEAD',             null, 1, true, now(), now());

-- EXECUTIVE: Business Head -> CEO / MD / Dy-CEO ; Dy-CEO -> CEO / MD
insert into hierarchy_rules (department_code, from_role_id, to_role_id, branch_type, priority, active, created_at, updated_at) values
('EXECUTIVE', 'BUSINESS_HEAD', 'CEO',    null, 1, true, now(), now()),
('EXECUTIVE', 'BUSINESS_HEAD', 'MD',     null, 2, true, now(), now()),
('EXECUTIVE', 'BUSINESS_HEAD', 'DY_CEO', null, 3, true, now(), now()),
('EXECUTIVE', 'DY_CEO',        'CEO',    null, 1, true, now(), now()),
('EXECUTIVE', 'DY_CEO',        'MD',     null, 2, true, now(), now());

-- RISK
insert into hierarchy_rules (department_code, from_role_id, to_role_id, branch_type, priority, active, created_at, updated_at) values
('RISK', 'RISK_ANALYST', 'RISK_MANAGER', null, 1, true, now(), now()),
('RISK', 'RISK_MANAGER', 'RISK_HEAD',    null, 1, true, now(), now());

-- AUDIT
insert into hierarchy_rules (department_code, from_role_id, to_role_id, branch_type, priority, active, created_at, updated_at) values
('AUDIT', 'AUDIT_EXECUTIVE', 'AUDIT_MANAGER', null, 1, true, now(), now()),
('AUDIT', 'AUDIT_MANAGER',   'AUDIT_HEAD',    null, 1, true, now(), now());

-- GENERIC 3-TIER DEPARTMENTS: Executive -> Manager -> Head -> Business Head
insert into hierarchy_rules (department_code, from_role_id, to_role_id, branch_type, priority, active, created_at, updated_at) values
('LEGAL',      'LEGAL_EXECUTIVE',      'LEGAL_MANAGER',      null, 1, true, now(), now()),
('LEGAL',      'LEGAL_MANAGER',        'LEGAL_HEAD',         null, 1, true, now(), now()),
('LEGAL',      'LEGAL_HEAD',           'BUSINESS_HEAD',      null, 1, true, now(), now()),

('IT',         'IT_EXECUTIVE',         'IT_MANAGER',         null, 1, true, now(), now()),
('IT',         'IT_MANAGER',           'IT_HEAD',            null, 1, true, now(), now()),
('IT',         'IT_HEAD',              'BUSINESS_HEAD',      null, 1, true, now(), now()),

('OPERATIONS', 'OPERATIONS_EXECUTIVE', 'OPERATIONS_MANAGER', null, 1, true, now(), now()),
('OPERATIONS', 'OPERATIONS_MANAGER',   'OPERATIONS_HEAD',    null, 1, true, now(), now()),
('OPERATIONS', 'OPERATIONS_HEAD',      'BUSINESS_HEAD',      null, 1, true, now(), now()),

('HR',         'HR_EXECUTIVE',         'HR_MANAGER',         null, 1, true, now(), now()),
('HR',         'HR_MANAGER',           'HR_HEAD',            null, 1, true, now(), now()),
('HR',         'HR_HEAD',              'CHRO',               null, 1, true, now(), now()),

('ACCOUNTS',   'ACCOUNTS_EXECUTIVE',   'ACCOUNTS_MANAGER',   null, 1, true, now(), now()),
('ACCOUNTS',   'ACCOUNTS_MANAGER',     'ACCOUNTS_HEAD',      null, 1, true, now(), now()),
('ACCOUNTS',   'ACCOUNTS_HEAD',        'CFO',                null, 1, true, now(), now()),

('INSURANCE',  'INSURANCE_EXECUTIVE',  'INSURANCE_MANAGER',  null, 1, true, now(), now()),
('INSURANCE',  'INSURANCE_MANAGER',    'INSURANCE_HEAD',     null, 1, true, now(), now()),
('INSURANCE',  'INSURANCE_HEAD',       'BUSINESS_HEAD',      null, 1, true, now(), now()),

('FINANCE',    'FINANCE_EXECUTIVE',    'FINANCE_MANAGER',    null, 1, true, now(), now()),
('FINANCE',    'FINANCE_MANAGER',      'FINANCE_HEAD',       null, 1, true, now(), now()),
('FINANCE',    'FINANCE_HEAD',         'CFO',                null, 1, true, now(), now()),

('TRAINING',   'TRAINING_EXECUTIVE',   'TRAINING_MANAGER',   null, 1, true, now(), now()),
('TRAINING',   'TRAINING_MANAGER',     'TRAINING_HEAD',      null, 1, true, now(), now()),
('TRAINING',   'TRAINING_HEAD',        'CHRO',               null, 1, true, now(), now());
