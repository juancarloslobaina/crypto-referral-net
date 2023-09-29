import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { NotificationsDetailComponent } from './notifications-detail.component';

describe('Notifications Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationsDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: NotificationsDetailComponent,
              resolve: { notifications: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(NotificationsDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load notifications on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', NotificationsDetailComponent);

      // THEN
      expect(instance.notifications).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
