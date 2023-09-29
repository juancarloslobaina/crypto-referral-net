import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { INotifications, NewNotifications } from '../notifications.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts INotifications for edit and NewNotificationsFormGroupInput for create.
 */
type NotificationsFormGroupInput = INotifications | PartialWithRequiredKeyOf<NewNotifications>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends INotifications | NewNotifications> = Omit<T, 'date'> & {
  date?: string | null;
};

type NotificationsFormRawValue = FormValueOf<INotifications>;

type NewNotificationsFormRawValue = FormValueOf<NewNotifications>;

type NotificationsFormDefaults = Pick<NewNotifications, 'id' | 'date'>;

type NotificationsFormGroupContent = {
  id: FormControl<NotificationsFormRawValue['id'] | NewNotifications['id']>;
  date: FormControl<NotificationsFormRawValue['date']>;
  message: FormControl<NotificationsFormRawValue['message']>;
  status: FormControl<NotificationsFormRawValue['status']>;
  user: FormControl<NotificationsFormRawValue['user']>;
};

export type NotificationsFormGroup = FormGroup<NotificationsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class NotificationsFormService {
  createNotificationsFormGroup(notifications: NotificationsFormGroupInput = { id: null }): NotificationsFormGroup {
    const notificationsRawValue = this.convertNotificationsToNotificationsRawValue({
      ...this.getFormDefaults(),
      ...notifications,
    });
    return new FormGroup<NotificationsFormGroupContent>({
      id: new FormControl(
        { value: notificationsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(notificationsRawValue.date),
      message: new FormControl(notificationsRawValue.message),
      status: new FormControl(notificationsRawValue.status),
      user: new FormControl(notificationsRawValue.user, {
        validators: [Validators.required],
      }),
    });
  }

  getNotifications(form: NotificationsFormGroup): INotifications | NewNotifications {
    return this.convertNotificationsRawValueToNotifications(form.getRawValue() as NotificationsFormRawValue | NewNotificationsFormRawValue);
  }

  resetForm(form: NotificationsFormGroup, notifications: NotificationsFormGroupInput): void {
    const notificationsRawValue = this.convertNotificationsToNotificationsRawValue({ ...this.getFormDefaults(), ...notifications });
    form.reset(
      {
        ...notificationsRawValue,
        id: { value: notificationsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): NotificationsFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertNotificationsRawValueToNotifications(
    rawNotifications: NotificationsFormRawValue | NewNotificationsFormRawValue,
  ): INotifications | NewNotifications {
    return {
      ...rawNotifications,
      date: dayjs(rawNotifications.date, DATE_TIME_FORMAT),
    };
  }

  private convertNotificationsToNotificationsRawValue(
    notifications: INotifications | (Partial<NewNotifications> & NotificationsFormDefaults),
  ): NotificationsFormRawValue | PartialWithRequiredKeyOf<NewNotificationsFormRawValue> {
    return {
      ...notifications,
      date: notifications.date ? notifications.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
