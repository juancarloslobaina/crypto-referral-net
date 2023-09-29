import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CryptocurrencyDetailComponent } from './cryptocurrency-detail.component';

describe('Cryptocurrency Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CryptocurrencyDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: CryptocurrencyDetailComponent,
              resolve: { cryptocurrency: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CryptocurrencyDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load cryptocurrency on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CryptocurrencyDetailComponent);

      // THEN
      expect(instance.cryptocurrency).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
