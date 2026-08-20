import { DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Observable, finalize } from 'rxjs';

import {
  ChangeSpecDetails,
  ChangeSpecSummary,
  GateStatus,
  GateType,
  RiskLevel,
  SpecStatus
} from './core/models/change-spec';
import { ChangeSpecApi } from './core/services/change-spec-api';

@Component({
  selector: 'app-root',
  imports: [DatePipe, ReactiveFormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  private readonly api = inject(ChangeSpecApi);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly specs = signal<ChangeSpecSummary[]>([]);
  protected readonly selected = signal<ChangeSpecDetails | null>(null);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly showCreate = signal(false);
  protected readonly query = signal('');
  protected readonly actor = signal('Pedro Igor Campos Costa');

  protected readonly filteredSpecs = computed(() => {
    const term = this.query().trim().toLocaleLowerCase('pt-BR');
    if (!term) {
      return this.specs();
    }
    return this.specs().filter((spec) =>
      `${spec.key} ${spec.title} ${spec.owner}`.toLocaleLowerCase('pt-BR').includes(term)
    );
  });
  protected readonly releasedCount = computed(
    () => this.specs().filter((spec) => spec.status === 'RELEASED').length
  );
  protected readonly implementingCount = computed(
    () => this.specs().filter((spec) => spec.status === 'IMPLEMENTING').length
  );
  protected readonly pendingEvidence = computed(() =>
    this.selected()?.qualityGates.filter((gate) => gate.status !== 'PASSED').length ?? 0
  );

  protected readonly createForm = this.formBuilder.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(160)]],
    problem: ['', [Validators.required, Validators.maxLength(4000)]],
    proposedSolution: ['', [Validators.required, Validators.maxLength(4000)]],
    owner: ['Pedro Igor Campos Costa', [Validators.required, Validators.maxLength(120)]],
    riskLevel: ['MEDIUM' as RiskLevel, Validators.required],
    acceptanceCriteria: this.formBuilder.nonNullable.array([
      this.formBuilder.nonNullable.control('', [Validators.required, Validators.maxLength(500)])
    ])
  });

  protected get criteria(): FormArray {
    return this.createForm.controls.acceptanceCriteria;
  }

  ngOnInit(): void {
    this.loadSpecs();
  }

  protected loadSpecs(selectId?: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.api.list().pipe(finalize(() => this.loading.set(false))).subscribe({
      next: (specs) => {
        this.specs.set(specs);
        const id = selectId ?? this.selected()?.id ?? specs[0]?.id;
        if (id) {
          this.selectSpec(id);
        }
      },
      error: () => this.error.set('Não foi possível consultar as especificações agora.')
    });
  }

  protected selectSpec(id: string): void {
    this.error.set(null);
    this.api.get(id).subscribe({
      next: (spec) => this.selected.set(spec),
      error: () => this.error.set('A especificação selecionada não pôde ser carregada.')
    });
  }

  protected addCriterion(): void {
    this.criteria.push(
      this.formBuilder.nonNullable.control('', [Validators.required, Validators.maxLength(500)])
    );
  }

  protected removeCriterion(index: number): void {
    if (this.criteria.length > 1) {
      this.criteria.removeAt(index);
    }
  }

  protected createSpec(): void {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.error.set(null);
    this.api.create(this.createForm.getRawValue())
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: (created) => {
          this.showCreate.set(false);
          this.resetCreateForm();
          this.loadSpecs(created.id);
        },
        error: () => this.error.set('Revise os campos. A especificação não foi salva.')
      });
  }

  protected move(action: 'submit' | 'approve' | 'return' | 'start' | 'release'): void {
    const spec = this.selected();
    if (!spec) {
      return;
    }
    const comments: Partial<Record<typeof action, string>> = {
      approve: 'Escopo, critérios e riscos revisados.',
      return: 'Detalhes adicionais solicitados durante a revisão.'
    };
    this.runMutation(this.api.transition(spec.id, action, this.actor(), comments[action] ?? ''));
  }

  protected verifyCriterion(criterionId: string, verified: boolean): void {
    const id = this.selected()?.id;
    if (id) {
      this.runMutation(this.api.verifyCriterion(id, criterionId, verified, this.actor()));
    }
  }

  protected passGate(type: GateType): void {
    const id = this.selected()?.id;
    if (id) {
      this.runMutation(this.api.recordGate(
        id,
        type,
        'PASSED',
        `local://evidence/${type.toLocaleLowerCase()}`,
        this.actor()
      ));
    }
  }

  protected statusLabel(status: SpecStatus): string {
    const labels: Record<SpecStatus, string> = {
      DRAFT: 'Rascunho', IN_REVIEW: 'Em revisão', APPROVED: 'Aprovada',
      IMPLEMENTING: 'Em implementação', RELEASED: 'Publicada'
    };
    return labels[status];
  }

  protected gateStatusLabel(status: GateStatus): string {
    return status === 'PASSED' ? 'Com evidência' : status === 'FAILED' ? 'Falhou' : 'Pendente';
  }

  protected can(action: string): boolean {
    const status = this.selected()?.status;
    return (action === 'submit' && status === 'DRAFT') ||
      ((action === 'approve' || action === 'return') && status === 'IN_REVIEW') ||
      (action === 'start' && status === 'APPROVED') ||
      (action === 'release' && status === 'IMPLEMENTING');
  }

  private resetCreateForm(): void {
    while (this.criteria.length > 1) {
      this.criteria.removeAt(this.criteria.length - 1);
    }
    this.createForm.reset({
      title: '', problem: '', proposedSolution: '', owner: 'Pedro Igor Campos Costa',
      riskLevel: 'MEDIUM', acceptanceCriteria: ['']
    });
  }

  private runMutation(request: Observable<ChangeSpecDetails>): void {
    this.saving.set(true);
    this.error.set(null);
    request.pipe(finalize(() => this.saving.set(false))).subscribe({
      next: (updated) => {
        this.selected.set(updated);
        this.loadSpecs(updated.id);
      },
      error: (response) => this.error.set(
        response.error?.detail ?? 'A operação não respeita o estado atual da especificação.'
      )
    });
  }
}
