import dayjs from 'dayjs/esm';

import { ITransaction, NewTransaction } from './transaction.model';

export const sampleWithRequiredData: ITransaction = {
  id: 14466,
};

export const sampleWithPartialData: ITransaction = {
  id: 18736,
};

export const sampleWithFullData: ITransaction = {
  id: 16210,
  amount: 10707.76,
  date: dayjs('2023-09-29T08:24'),
  status: 'CANCELADA',
};

export const sampleWithNewData: NewTransaction = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
