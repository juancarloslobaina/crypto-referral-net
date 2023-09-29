import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../notifications.test-samples';

import { NotificationsFormService } from './notifications-form.service';

describe('Notifications Form Service', () => {
  let service: NotificationsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationsFormService);
  });

  describe('Service methods', () => {
    describe('createNotificationsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createNotificationsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            message: expect.any(Object),
            status: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing INotifications should create a new form with FormGroup', () => {
        const formGroup = service.createNotificationsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            message: expect.any(Object),
            status: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getNotifications', () => {
      it('should return NewNotifications for default Notifications initial value', () => {
        const formGroup = service.createNotificationsFormGroup(sampleWithNewData);

        const notifications = service.getNotifications(formGroup) as any;

        expect(notifications).toMatchObject(sampleWithNewData);
      });

      it('should return NewNotifications for empty Notifications initial value', () => {
        const formGroup = service.createNotificationsFormGroup();

        const notifications = service.getNotifications(formGroup) as any;

        expect(notifications).toMatchObject({});
      });

      it('should return INotifications', () => {
        const formGroup = service.createNotificationsFormGroup(sampleWithRequiredData);

        const notifications = service.getNotifications(formGroup) as any;

        expect(notifications).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing INotifications should not enable id FormControl', () => {
        const formGroup = service.createNotificationsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewNotifications should disable id FormControl', () => {
        const formGroup = service.createNotificationsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
