import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IWallet, NewWallet } from '../wallet.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWallet for edit and NewWalletFormGroupInput for create.
 */
type WalletFormGroupInput = IWallet | PartialWithRequiredKeyOf<NewWallet>;

type WalletFormDefaults = Pick<NewWallet, 'id'>;

type WalletFormGroupContent = {
  id: FormControl<IWallet['id'] | NewWallet['id']>;
  address: FormControl<IWallet['address']>;
  balance: FormControl<IWallet['balance']>;
  user: FormControl<IWallet['user']>;
  cryto: FormControl<IWallet['cryto']>;
};

export type WalletFormGroup = FormGroup<WalletFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WalletFormService {
  createWalletFormGroup(wallet: WalletFormGroupInput = { id: null }): WalletFormGroup {
    const walletRawValue = {
      ...this.getFormDefaults(),
      ...wallet,
    };
    return new FormGroup<WalletFormGroupContent>({
      id: new FormControl(
        { value: walletRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      address: new FormControl(walletRawValue.address),
      balance: new FormControl(walletRawValue.balance),
      user: new FormControl(walletRawValue.user, {
        validators: [Validators.required],
      }),
      cryto: new FormControl(walletRawValue.cryto, {
        validators: [Validators.required],
      }),
    });
  }

  getWallet(form: WalletFormGroup): IWallet | NewWallet {
    return form.getRawValue() as IWallet | NewWallet;
  }

  resetForm(form: WalletFormGroup, wallet: WalletFormGroupInput): void {
    const walletRawValue = { ...this.getFormDefaults(), ...wallet };
    form.reset(
      {
        ...walletRawValue,
        id: { value: walletRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): WalletFormDefaults {
    return {
      id: null,
    };
  }
}
