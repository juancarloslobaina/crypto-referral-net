import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICryptocurrency } from '../cryptocurrency.model';
import { CryptocurrencyService } from '../service/cryptocurrency.service';
import { CryptocurrencyFormService, CryptocurrencyFormGroup } from './cryptocurrency-form.service';

@Component({
  standalone: true,
  selector: 'jhi-cryptocurrency-update',
  templateUrl: './cryptocurrency-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CryptocurrencyUpdateComponent implements OnInit {
  isSaving = false;
  cryptocurrency: ICryptocurrency | null = null;

  editForm: CryptocurrencyFormGroup = this.cryptocurrencyFormService.createCryptocurrencyFormGroup();

  constructor(
    protected cryptocurrencyService: CryptocurrencyService,
    protected cryptocurrencyFormService: CryptocurrencyFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cryptocurrency }) => {
      this.cryptocurrency = cryptocurrency;
      if (cryptocurrency) {
        this.updateForm(cryptocurrency);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cryptocurrency = this.cryptocurrencyFormService.getCryptocurrency(this.editForm);
    if (cryptocurrency.id !== null) {
      this.subscribeToSaveResponse(this.cryptocurrencyService.update(cryptocurrency));
    } else {
      this.subscribeToSaveResponse(this.cryptocurrencyService.create(cryptocurrency));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICryptocurrency>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(cryptocurrency: ICryptocurrency): void {
    this.cryptocurrency = cryptocurrency;
    this.cryptocurrencyFormService.resetForm(this.editForm, cryptocurrency);
  }
}
