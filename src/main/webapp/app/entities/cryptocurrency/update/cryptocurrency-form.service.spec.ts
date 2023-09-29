import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../cryptocurrency.test-samples';

import { CryptocurrencyFormService } from './cryptocurrency-form.service';

describe('Cryptocurrency Form Service', () => {
  let service: CryptocurrencyFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CryptocurrencyFormService);
  });

  describe('Service methods', () => {
    describe('createCryptocurrencyFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCryptocurrencyFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            symbol: expect.any(Object),
            exchangeRate: expect.any(Object),
          }),
        );
      });

      it('passing ICryptocurrency should create a new form with FormGroup', () => {
        const formGroup = service.createCryptocurrencyFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            symbol: expect.any(Object),
            exchangeRate: expect.any(Object),
          }),
        );
      });
    });

    describe('getCryptocurrency', () => {
      it('should return NewCryptocurrency for default Cryptocurrency initial value', () => {
        const formGroup = service.createCryptocurrencyFormGroup(sampleWithNewData);

        const cryptocurrency = service.getCryptocurrency(formGroup) as any;

        expect(cryptocurrency).toMatchObject(sampleWithNewData);
      });

      it('should return NewCryptocurrency for empty Cryptocurrency initial value', () => {
        const formGroup = service.createCryptocurrencyFormGroup();

        const cryptocurrency = service.getCryptocurrency(formGroup) as any;

        expect(cryptocurrency).toMatchObject({});
      });

      it('should return ICryptocurrency', () => {
        const formGroup = service.createCryptocurrencyFormGroup(sampleWithRequiredData);

        const cryptocurrency = service.getCryptocurrency(formGroup) as any;

        expect(cryptocurrency).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICryptocurrency should not enable id FormControl', () => {
        const formGroup = service.createCryptocurrencyFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCryptocurrency should disable id FormControl', () => {
        const formGroup = service.createCryptocurrencyFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
