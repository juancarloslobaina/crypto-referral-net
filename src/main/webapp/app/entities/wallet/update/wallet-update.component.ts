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
import { WalletService } from '../service/wallet.service';
import { IWallet } from '../wallet.model';
import { WalletFormService, WalletFormGroup } from './wallet-form.service';

@Component({
  standalone: true,
  selector: 'jhi-wallet-update',
  templateUrl: './wallet-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class WalletUpdateComponent implements OnInit {
  isSaving = false;
  wallet: IWallet | null = null;

  usersSharedCollection: IUser[] = [];
  cryptocurrenciesSharedCollection: ICryptocurrency[] = [];

  editForm: WalletFormGroup = this.walletFormService.createWalletFormGroup();

  constructor(
    protected walletService: WalletService,
    protected walletFormService: WalletFormService,
    protected userService: UserService,
    protected cryptocurrencyService: CryptocurrencyService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareCryptocurrency = (o1: ICryptocurrency | null, o2: ICryptocurrency | null): boolean =>
    this.cryptocurrencyService.compareCryptocurrency(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ wallet }) => {
      this.wallet = wallet;
      if (wallet) {
        this.updateForm(wallet);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const wallet = this.walletFormService.getWallet(this.editForm);
    if (wallet.id !== null) {
      this.subscribeToSaveResponse(this.walletService.update(wallet));
    } else {
      this.subscribeToSaveResponse(this.walletService.create(wallet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWallet>>): void {
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

  protected updateForm(wallet: IWallet): void {
    this.wallet = wallet;
    this.walletFormService.resetForm(this.editForm, wallet);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, wallet.user);
    this.cryptocurrenciesSharedCollection = this.cryptocurrencyService.addCryptocurrencyToCollectionIfMissing<ICryptocurrency>(
      this.cryptocurrenciesSharedCollection,
      wallet.cryto,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.wallet?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.cryptocurrencyService
      .query()
      .pipe(map((res: HttpResponse<ICryptocurrency[]>) => res.body ?? []))
      .pipe(
        map((cryptocurrencies: ICryptocurrency[]) =>
          this.cryptocurrencyService.addCryptocurrencyToCollectionIfMissing<ICryptocurrency>(cryptocurrencies, this.wallet?.cryto),
        ),
      )
      .subscribe((cryptocurrencies: ICryptocurrency[]) => (this.cryptocurrenciesSharedCollection = cryptocurrencies));
  }
}
