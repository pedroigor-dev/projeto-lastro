import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import {
  ChangeSpecDetails,
  ChangeSpecSummary,
  CreateSpecPayload,
  GateStatus,
  GateType
} from '../models/change-spec';

@Injectable({ providedIn: 'root' })
export class ChangeSpecApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/specs';

  list(): Observable<ChangeSpecSummary[]> {
    return this.http.get<ChangeSpecSummary[]>(this.baseUrl);
  }

  get(id: string): Observable<ChangeSpecDetails> {
    return this.http.get<ChangeSpecDetails>(`${this.baseUrl}/${id}`);
  }

  create(payload: CreateSpecPayload): Observable<ChangeSpecDetails> {
    return this.http.post<ChangeSpecDetails>(this.baseUrl, payload);
  }

  transition(
    id: string,
    action: 'submit' | 'approve' | 'return' | 'start' | 'release',
    actor: string,
    comment: string
  ): Observable<ChangeSpecDetails> {
    return this.http.post<ChangeSpecDetails>(`${this.baseUrl}/${id}/${action}`, {
      actor,
      comment
    });
  }

  verifyCriterion(
    id: string,
    criterionId: string,
    verified: boolean,
    actor: string
  ): Observable<ChangeSpecDetails> {
    return this.http.put<ChangeSpecDetails>(`${this.baseUrl}/${id}/criteria/${criterionId}`, {
      verified,
      actor
    });
  }

  recordGate(
    id: string,
    type: GateType,
    status: GateStatus,
    evidenceReference: string,
    actor: string
  ): Observable<ChangeSpecDetails> {
    return this.http.put<ChangeSpecDetails>(`${this.baseUrl}/${id}/gates/${type}`, {
      status,
      evidenceReference,
      actor
    });
  }
}
