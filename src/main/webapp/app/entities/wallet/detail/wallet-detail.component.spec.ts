import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { WalletDetailComponent } from './wallet-detail.component';

describe('Wallet Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WalletDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: WalletDetailComponent,
              resolve: { wallet: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(WalletDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load wallet on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', WalletDetailComponent);

      // THEN
      expect(instance.wallet).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
