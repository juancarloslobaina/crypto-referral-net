import dayjs from 'dayjs/esm';

import { INotifications, NewNotifications } from './notifications.model';

export const sampleWithRequiredData: INotifications = {
  id: 4933,
};

export const sampleWithPartialData: INotifications = {
  id: 18688,
  status: 'PROMOCIONESPECIAL',
};

export const sampleWithFullData: INotifications = {
  id: 13583,
  date: dayjs('2023-09-28T17:53'),
  message: 'faithfully',
  status: 'PROMOCIONESPECIAL',
};

export const sampleWithNewData: NewNotifications = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
