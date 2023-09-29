import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICryptocurrency } from 'app/entities/cryptocurrency/cryptocurrency.model';
import { CryptocurrencyService } from 'app/entities/cryptocurrency/service/cryptocurrency.service';
import { TransactionStatus } from 'app/entities/enumerations/transaction-status.model';
import { TransactionService } from '../service/transaction.service';
import { ITransaction } from '../transaction.model';
import { TransactionFormService, TransactionFormGroup } from './transaction-form.service';

@Component({
  standalone: true,
  selector: 'jhi-transaction-update',
  templateUrl: './transaction-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TransactionUpdateComponent implements OnInit {
  isSaving = false;
  transaction: ITransaction | null = null;
  transactionStatusValues = Object.keys(TransactionStatus);

  usersSharedCollection: IUser[] = [];
  cryptocurrenciesSharedCollection: ICryptocurrency[] = [];

  editForm: TransactionFormGroup = this.transactionFormService.createTransactionFormGroup();

  constructor(
    protected transactionService: TransactionService,
    protected transactionFormService: TransactionFormService,
    protected userService: UserService,
    protected cryptocurrencyService: CryptocurrencyService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareCryptocurrency = (o1: ICryptocurrency | null, o2: ICryptocurrency | null): boolean =>
    this.cryptocurrencyService.compareCryptocurrency(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transaction }) => {
      this.transaction = transaction;
      if (transaction) {
        this.updateForm(transaction);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const transaction = this.transactionFormService.getTransaction(this.editForm);
    if (transaction.id !== null) {
      this.subscribeToSaveResponse(this.transactionService.update(transaction));
    } else {
      this.subscribeToSaveResponse(this.transactionService.create(transaction));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransaction>>): void {
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

  protected updateForm(transaction: ITransaction): void {
    this.transaction = transaction;
    this.transactionFormService.resetForm(this.editForm, transaction);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      transaction.userFrom,
      transaction.userTo,
    );
    this.cryptocurrenciesSharedCollection = this.cryptocurrencyService.addCryptocurrencyToCollectionIfMissing<ICryptocurrency>(
      this.cryptocurrenciesSharedCollection,
      transaction.crypto,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.transaction?.userFrom, this.transaction?.userTo),
        ),
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.cryptocurrencyService
      .query()
      .pipe(map((res: HttpResponse<ICryptocurrency[]>) => res.body ?? []))
      .pipe(
        map((cryptocurrencies: ICryptocurrency[]) =>
          this.cryptocurrencyService.addCryptocurrencyToCollectionIfMissing<ICryptocurrency>(cryptocurrencies, this.transaction?.crypto),
        ),
      )
      .subscribe((cryptocurrencies: ICryptocurrency[]) => (this.cryptocurrenciesSharedCollection = cryptocurrencies));
  }
}
