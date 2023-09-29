import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TransactionDetailComponent } from './transaction-detail.component';

describe('Transaction Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TransactionDetailComponent,
              resolve: { transaction: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TransactionDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load transaction on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TransactionDetailComponent);

      // THEN
      expect(instance.transaction).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
