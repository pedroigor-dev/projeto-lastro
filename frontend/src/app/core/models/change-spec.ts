export type SpecStatus = 'DRAFT' | 'IN_REVIEW' | 'APPROVED' | 'IMPLEMENTING' | 'RELEASED';
export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH';
export type GateStatus = 'PENDING' | 'PASSED' | 'FAILED';
export type GateType =
  | 'BACKEND_TESTS'
  | 'FRONTEND_TESTS'
  | 'QUALITY'
  | 'SECURITY'
  | 'RELEASE_NOTES';

export interface ChangeSpecSummary {
  id: string;
  key: string;
  title: string;
  owner: string;
  riskLevel: RiskLevel;
  status: SpecStatus;
  updatedAt: string;
}

export interface AcceptanceCriterion {
  id: string;
  description: string;
  verified: boolean;
}

export interface QualityGate {
  type: GateType;
  status: GateStatus;
  evidenceReference: string | null;
  updatedAt: string;
}

export interface AuditEvent {
  id: string;
  type: string;
  actor: string;
  detail: string;
  occurredAt: string;
}

export interface ChangeSpecDetails extends ChangeSpecSummary {
  problem: string;
  proposedSolution: string;
  acceptanceCriteria: AcceptanceCriterion[];
  qualityGates: QualityGate[];
  history: AuditEvent[];
  createdAt: string;
  version: number;
}

export interface CreateSpecPayload {
  title: string;
  problem: string;
  proposedSolution: string;
  owner: string;
  riskLevel: RiskLevel;
  acceptanceCriteria: string[];
}
