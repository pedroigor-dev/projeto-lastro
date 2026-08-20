create table change_specs (
    id uuid primary key,
    spec_key varchar(30) not null unique,
    title varchar(160) not null,
    problem_text varchar(4000) not null,
    proposed_solution varchar(4000) not null,
    owner_name varchar(120) not null,
    risk_level varchar(20) not null,
    status varchar(30) not null,
    version bigint not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table acceptance_criteria (
    id uuid primary key,
    spec_id uuid not null references change_specs(id) on delete cascade,
    criterion_order integer not null,
    description varchar(500) not null,
    verified boolean not null
);

create table quality_gates (
    id uuid primary key,
    spec_id uuid not null references change_specs(id) on delete cascade,
    gate_type varchar(30) not null,
    gate_status varchar(20) not null,
    evidence_reference varchar(500),
    updated_at timestamp with time zone not null,
    constraint uk_quality_gate unique (spec_id, gate_type)
);

create table audit_events (
    id uuid primary key,
    spec_id uuid not null references change_specs(id) on delete cascade,
    event_type varchar(60) not null,
    actor varchar(120) not null,
    detail varchar(1000) not null,
    occurred_at timestamp with time zone not null
);

create index idx_change_specs_status on change_specs(status);
create index idx_audit_events_spec_time on audit_events(spec_id, occurred_at);
