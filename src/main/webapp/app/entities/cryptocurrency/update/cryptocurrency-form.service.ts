import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICryptocurrency, NewCryptocurrency } from '../cryptocurrency.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICryptocurrency for edit and NewCryptocurrencyFormGroupInput for create.
 */
type CryptocurrencyFormGroupInput = ICryptocurrency | PartialWithRequiredKeyOf<NewCryptocurrency>;

type CryptocurrencyFormDefaults = Pick<NewCryptocurrency, 'id'>;

type CryptocurrencyFormGroupContent = {
  id: FormControl<ICryptocurrency['id'] | NewCryptocurrency['id']>;
  name: FormControl<ICryptocurrency['name']>;
  symbol: FormControl<ICryptocurrency['symbol']>;
  exchangeRate: FormControl<ICryptocurrency['exchangeRate']>;
};

export type CryptocurrencyFormGroup = FormGroup<CryptocurrencyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CryptocurrencyFormService {
  createCryptocurrencyFormGroup(cryptocurrency: CryptocurrencyFormGroupInput = { id: null }): CryptocurrencyFormGroup {
    const cryptocurrencyRawValue = {
      ...this.getFormDefaults(),
      ...cryptocurrency,
    };
    return new FormGroup<CryptocurrencyFormGroupContent>({
      id: new FormControl(
        { value: cryptocurrencyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(cryptocurrencyRawValue.name),
      symbol: new FormControl(cryptocurrencyRawValue.symbol),
      exchangeRate: new FormControl(cryptocurrencyRawValue.exchangeRate),
    });
  }

  getCryptocurrency(form: CryptocurrencyFormGroup): ICryptocurrency | NewCryptocurrency {
    return form.getRawValue() as ICryptocurrency | NewCryptocurrency;
  }

  resetForm(form: CryptocurrencyFormGroup, cryptocurrency: CryptocurrencyFormGroupInput): void {
    const cryptocurrencyRawValue = { ...this.getFormDefaults(), ...cryptocurrency };
    form.reset(
      {
        ...cryptocurrencyRawValue,
        id: { value: cryptocurrencyRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CryptocurrencyFormDefaults {
    return {
      id: null,
    };
  }
}
